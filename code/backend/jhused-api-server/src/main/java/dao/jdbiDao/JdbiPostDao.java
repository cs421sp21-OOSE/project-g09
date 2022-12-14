package dao.jdbiDao;

import dao.HashtagDao;
import dao.ImageDao;
import dao.PostDao;
import dao.PostHashtagDao;
import email.Wishlist.WishlistEmails;
import exceptions.DaoException;
import model.Category;
import model.Hashtag;
import model.Image;
import model.Post;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.jdbi.v3.core.statement.StatementException;
import util.jdbiResultSetHandler.ResultSetLinkedHashMapAccumulatorProvider;

import java.io.IOException;
import java.util.*;

public class JdbiPostDao implements PostDao {

  private final Jdbi jdbi;
  private final ImageDao imageDao;
  private final HashtagDao hashtagDao;
  private final PostHashtagDao postHashtagDao;
  private final ResultSetLinkedHashMapAccumulatorProvider<Post> postAccumulator;
  private final String SELECT_POST_BASE =
      "SELECT post.*, "
          + "image.id as images_id, "
          + "image.url as images_url,"
          + "image.post_id as images_post_id, "
          + "hashtag.id as hashtags_id, "
          + "hashtag.hashtag as hashtags_hashtag "
          + "FROM post "
          + "LEFT JOIN image ON image.post_id = post.id "
          + "LEFT JOIN post_hashtag ON post_hashtag.post_id = post.id "
          + "LEFT JOIN hashtag ON hashtag.id = post_hashtag.hashtag_id ";
  private final String SELECT_POST_GIVEN_ID = SELECT_POST_BASE + "WHERE post.id = :id;";
  private final String SELECT_POSTS = SELECT_POST_BASE + "ORDER BY post.id;";

  /**
   * Construct JdbiPostDao.
   *
   * @param jdbi A Jdbi object is injected as a dependency;
   *             it is assumed jdbi is connected to a database that  contains a table called
   *             "Posts" with two columns: "id" and "title".
   */
  public JdbiPostDao(Jdbi jdbi) {
    this.jdbi = jdbi;
    imageDao = new JdbiImageDao(jdbi);
    hashtagDao = new JdbiHashtagDao(jdbi);
    postHashtagDao = new JdbiPostHashtagDao(jdbi);
    postAccumulator = new ResultSetLinkedHashMapAccumulatorProvider<>(Post.class);
  }

  /**
   * Insert the post into database, then return
   * the inserted post.
   * Need to
   * 1. insert post to post table,
   * 2. then insert image by calling create of ImageDao,
   * 3. then insert hashtag by calling create of HashtagDao,
   * 4. then insert post_hashtag, do this here.
   * Sequence between 2 and 3 can exchange.
   * Be careful about foreign key constrain.
   *
   * @param post The Post item to be created
   * @return
   * @throws DaoException
   */
  @Override
  public Post create(Post post) throws DaoException {
    String insertPostSql = "INSERT INTO post(" +
        "id, user_id, title, price, sale_state, description, category, location) " +
        "VALUES(:id, :userId, :title, :price, CAST(:saleState AS SaleState), " +
        ":description, CAST(:category AS Category), :location);";

    if (post != null && (post.getId() == null || post.getId().length() != 36)) {
      post.setId(UUID.randomUUID().toString());
    }

    try {
      return jdbi.inTransaction(handle -> {
        handle.createUpdate(insertPostSql).bindBean(post).execute();
        if (!post.getImages().isEmpty()) {
          for (Image image : post.getImages())
            image.setPostId(post.getId());
          imageDao.create(post.getImages());
        }

        if (!post.getHashtags().isEmpty()) {
          List<String> hashtagIds = new ArrayList<>();
          for (Hashtag hashtag : post.getHashtags()) {
            hashtagIds.add(hashtagDao.createIfNotExist(hashtag).getId());
          }
          postHashtagDao.create(post.getId(), hashtagIds);
        }
        return new ArrayList<>(handle.createQuery(SELECT_POST_GIVEN_ID).bind("id", post.getId())
            .reduceResultSet(new LinkedHashMap<>(),
                postAccumulator).values()).get(0);
      });
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public Post read(String id) throws DaoException {
    try {
      return jdbi.inTransaction(handle -> new ArrayList<>(handle.
          createQuery(SELECT_POST_GIVEN_ID)
          .bind("id", id)
          .reduceResultSet(new LinkedHashMap<>(), postAccumulator)
          .values())
          .stream().findFirst().orElse(null));
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException("Unable to read a post with id " + id, ex);
    }
  }

  /**
   * This will be very slow if we have lots of posts.
   * TODO: Need to refactor.
   *
   * @return A list of posts
   * @throws DaoException
   */
  @Override
  public List<Post> readAll() throws DaoException {
    try {
      return jdbi.inTransaction(handle -> new ArrayList<>(handle
          .createQuery(SELECT_POSTS)
          .reduceResultSet(new LinkedHashMap<>(), postAccumulator)
          .values()));
    } catch (StatementException | IllegalStateException ex) {
      throw new DaoException("Unable to read posts from the database", ex);
    }
  }

  @Deprecated
  @Override
  public List<Post> readAll(String titleQuery) throws DaoException {
    String sql = SELECT_POST_BASE + "WHERE post.title ILIKE :partial;";
    try {
      return jdbi.inTransaction(handle -> new ArrayList<>(handle.createQuery(sql)
          .bind("partial", "%" + titleQuery + "%")
          .reduceResultSet(new LinkedHashMap<>(), postAccumulator)
          .values()));
    } catch (StatementException | IllegalStateException ex) {
      throw new DaoException("Unable to read a post with partialTitle " + titleQuery, ex);
    }
  }

  @Override
  public List<Post> readAllFromUser(String userId) throws DaoException {
    String sql = SELECT_POST_BASE + "WHERE post.user_id = :userId;";
    try {
      return jdbi.inTransaction(handle -> new ArrayList<>(handle.createQuery(sql)
          .bind("userId", userId)
          .reduceResultSet(new LinkedHashMap<>(), postAccumulator)
          .values()));
    } catch (StatementException | IllegalStateException ex) {
      throw new DaoException("Unable to read a post for userId" + userId, ex);
    }
  }

  @Override
  public List<Post> readAllAdvanced(String specified, String searchQuery, Map<String, String> sortParams) {
    return readAllAdvanced(specified, searchQuery, sortParams, 0, 0);
  }

  @Override
  public List<Post> readAllAdvanced(String specified, String searchQuery, Map<String, String> sortParams, int page,
                                    int limit) {
    try {
      String sql = SELECT_POST_BASE;
      sql = getSearchQueryForReadAllAdvanced(sql, specified, searchQuery, sortParams, page, limit, true);
      Category category = null;
      if (specified != null) {
        category = Category.valueOf(specified.toUpperCase()); // convert to enum
      }
      // Build query
      String finalSql = sql;
      Category finalCategory = category;
      return jdbi.inTransaction(handle -> {
        Query query = handle.createQuery(finalSql);
        if (specified != null) {
          query.bind("specifiedCategory", finalCategory);
        }
        if (searchQuery != null) {
          query.bind("hashtagQuery", "%" + searchQuery + "%")
              .bind("partialTitle", "%" + searchQuery + "%")
              .bind("partialDescription", "%" + searchQuery + "%")
              .bind("partialLocation", "%" + searchQuery + "%");
        }
        if (page > 0 && limit > 0) {
          query.bind("limit", limit)
              .bind("offset", ((page - 1) * limit));
        } else if (page < 0 || limit < 0) {
          throw new DaoException("Invalid page or limit", null);
        }
        return new ArrayList<>(query.reduceResultSet(new LinkedHashMap<>(), postAccumulator).values());
      });

    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException("Unable to read post with the query parameters", ex);
    }
  }

  @Override
  public Post update(String id, Post post) throws DaoException {
    /**
     * SQL string to be given to database.
     * Here we are updating the post with the passed id, and setting it to
     * all of the information stored within the separate post passed.
     *
     * Updates all fields except for uuid and userId since those should not
     * change.
     */

    String updateSql = "WITH updated AS (UPDATE post SET "
        + "user_id = :userId, "
        + "title = :title, "
        + "price = :price, "
        + "sale_state = CAST(:saleState AS SaleState),"
        + "description = :description, "
        + "category = CAST(:category AS Category), "
        + "location = :location "
        + "WHERE id = :id RETURNING *)"
        + "SELECT * FROM updated;";

    //attempt to open connection and perform sql string.
    try {
      //make placer-holder variables for fields that might be null.

      //check each from passed post to ensure no errors occur.
      if (post.getDescription() == null) {
        post.setDescription("");
      }
      if (post.getImages() == null) {
        post.setImages(new ArrayList<>());
      }
      if (post.getHashtags() == null) {
        post.setHashtags(new ArrayList<>());
      }else{
        Set<Hashtag> hashtags = new HashSet<>(post.getHashtags());
        post.getHashtags().clear();
        post.getHashtags().addAll(hashtags);
      }

      return jdbi.inTransaction(handle -> {
        Post updatedPost = handle.createQuery(updateSql)
            .bindBean(post).bind("id", id).mapToBean(Post.class).findFirst().orElse(null);
        if (updatedPost != null) {
          List<Image> toBeUpdatedImages = new ArrayList<>();
          List<Image> deleteImages = imageDao.getImagesOfPost(post.getId());
          List<String> deleteImagesIds = new ArrayList<>();
          deleteImages.forEach(k -> deleteImagesIds.add(k.getId()));

          for (Image image : post.getImages()) {
            image.setPostId(post.getId());
            // if existing images contain yet, updated version doesn't
            if (deleteImages.contains(image)) {
              deleteImagesIds.remove(image.getId());
              // add new image
            } else if (!deleteImages.contains(image)) {
              toBeUpdatedImages.add(image);
            }
          }
          imageDao.delete(deleteImagesIds);
          imageDao.create(toBeUpdatedImages);

          List<Hashtag> toBeUpdatedPostHashtags = new ArrayList<>();
          List<Hashtag> deletePostHashtags = hashtagDao.getHashtagsOfPost(post.getId());
          List<String> deletePostHashtagsIds = new ArrayList<>();
          List<Hashtag> toCreateHashtags = new ArrayList<>();
          deletePostHashtags.forEach(k -> deletePostHashtagsIds.add(k.getId()));

          for (Hashtag hashtag : post.getHashtags()) {
            if (deletePostHashtags.contains(hashtag)) {
              deletePostHashtags.forEach(k -> {
                if (k.equals(hashtag))
                  deletePostHashtagsIds.remove(k.getId());
              });
            } else if (!deletePostHashtags.contains(hashtag)) {
              List<Hashtag> existingHashtags = hashtagDao.readAllExactCaseSensitive(hashtag.getHashtag());
              if (existingHashtags == null || existingHashtags.isEmpty())
                toCreateHashtags.add(hashtag);
              else
                toBeUpdatedPostHashtags.addAll(existingHashtags);
            }
          }
          postHashtagDao.delete(post.getId(), deletePostHashtagsIds);
          List<String> toBeUpdatedPostHashtagsIds = new ArrayList<>();
          List<Hashtag> createdHashtags = hashtagDao.create(toCreateHashtags);
          toBeUpdatedPostHashtags.addAll(createdHashtags);
          toBeUpdatedPostHashtags.forEach(hashtag -> toBeUpdatedPostHashtagsIds.add(hashtag.getId()));
          postHashtagDao.create(post.getId(), toBeUpdatedPostHashtagsIds);

          //send emails!
          //WishlistEmails.basicWishlistUpdateEmail(jdbi, id);

          //send styled emails!
          WishlistEmails.styledWishlistUpdateEmail(jdbi, id);

        }
        return new ArrayList<>(handle.createQuery(SELECT_POST_GIVEN_ID)
            .bind("id", post.getId())
            .reduceResultSet(new LinkedHashMap<>(), postAccumulator)
            .values()).stream().findFirst().orElse(null);
      });
    } catch (StatementException | IllegalStateException | NullPointerException | IOException ex) { //otherwise, fail
      throw new DaoException("Unable to update this post! Check if missing fields.", ex);
    }
  }

  @Override
  public Post delete(String id) throws DaoException {
    /**
     * SQL string to be given to database.
     *
     * Deletes the post with the passed id, and returns it after deletion.
     */
    String sql = "WITH deleted AS ("
        + "DELETE FROM post WHERE id = :id RETURNING *"
        + ") SELECT * FROM deleted;";

    //attempt to open connection and perform sql string.
    try {
      List<Image> images = imageDao.getImagesOfPost(id);
      List<Hashtag> hashtags = hashtagDao.getHashtagsOfPost(id);
      return jdbi.inTransaction(handle -> {
        Post post = handle.createQuery(sql).bind("id", id).mapToBean(Post.class).findOne().orElse(null);
        if (post != null) {
          post.setImages(images);
          post.setHashtags(hashtags);
        }
        return post;
      });
    } catch (StatementException | IllegalStateException ex) { //otherwise, fail
      throw new DaoException("Unable to delete this post!", ex);
    }

  }

  @Override
  public List<Post> searchAll(String searchQuery) throws DaoException {
    throw new DaoException("Not implemented!", null);
  }

  @Override
  public List<Post> searchCategory(String searchQuery, Category specified) throws DaoException {
    throw new DaoException("Not implemented!", null);
  }

  @Override
  public List<Post> getCategory(Category specified) throws DaoException {
    String sql = SELECT_POST_BASE + " WHERE post.category = CAST(:specifiedCategory AS Category);";

    try {
      return jdbi.inTransaction(handle ->
          new ArrayList<>(handle.createQuery(sql)
              .bind("specifiedCategory", specified)
              .reduceResultSet(new LinkedHashMap<>(), postAccumulator)
              .values()));
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException("Unable to read a post with Category " + specified, ex);
    }
  }

  @Override
  public int getTotalRowNum(
      String specified, String searchQuery, Map<String, String> sortParams, int page, int limit) throws DaoException {
    try {
      String sql = getSearchQueryForReadAllAdvanced("SELECT COUNT(post.id) FROM post",
          specified, searchQuery, sortParams, page, limit, false);
      Category category = null;
      if (specified != null) {
        category = Category.valueOf(specified.toUpperCase()); // convert to enum
      }
      Category finalCategory = category;
      return jdbi.inTransaction(handle -> {
        // Build query
        Query query = handle.createQuery(sql);
        if (specified != null) {
          query.bind("specifiedCategory", finalCategory);
        }
        if (searchQuery != null) {
          query.bind("hashtagQuery", "%" + searchQuery + "%")
              .bind("partialTitle", "%" + searchQuery + "%")
              .bind("partialDescription", "%" + searchQuery + "%")
              .bind("partialLocation", "%" + searchQuery + "%");
        }
        if (page > 0 && limit > 0) {
          query.bind("limit", limit)
              .bind("offset", ((page - 1) * limit));
        } else if (page < 0 || limit < 0) {
          throw new DaoException("Invalid page or limit", null);
        }
        return query
            .mapTo(Integer.class)
            .findOne().orElse(0);
      });
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException("Unable to get the number of rows in post table ", ex);
    }
  }

  private String getSearchQueryForReadAllAdvanced(
      String baseSql, String specified, String searchQuery, Map<String, String> sortParams,
      int page, int limit, boolean appendOrderQuery) {
    String preSql = "WITH post AS (SELECT DISTINCT(post.*) FROM post ";
    if (searchQuery != null) {
      preSql = preSql + "LEFT JOIN post_hashtag ON post.id = post_hashtag.post_id "
          + "LEFT JOIN hashtag ON post_hashtag.hashtag_id = hashtag.id ";
    }

    if (specified != null) {
      preSql = preSql + " WHERE " +
          "post.category = CAST(:specifiedCategory AS Category)";
    }
    // Handle keyword search
    // Adapted from searchCategory
    if (searchQuery != null) {
      preSql = preSql + (specified == null ? " WHERE " : " AND ") + "(hashtag.hashtag ILIKE :hashtagQuery OR "
          + "post.title ILIKE :partialTitle OR "
          + "post.description ILIKE :partialDescription OR "
          + "post.location ILIKE :partialLocation) ";
    }
    // Handle sort
    // Adapted from readAll
    StringBuilder query = new StringBuilder(preSql);
    StringBuilder sortQuery = new StringBuilder();
    if (sortParams != null && !sortParams.isEmpty()) {
      sortQuery.append(" ORDER BY ");
      for (String key : sortParams.keySet()) {
        sortQuery.append(key).append(" ").append(sortParams.get(key).toUpperCase()).append(", ");
      }
      sortQuery.delete(sortQuery.length() - 2, sortQuery.length()); // remove the extra comma and space
      query.append(sortQuery);
      if (page > 0 && limit > 0) {
        query.append(" LIMIT :limit OFFSET :offset");
      }
    } else if (page > 0 && limit > 0) {
      // filter only valid page and limit
      sortQuery.append(" ORDER BY ");
      sortQuery.append("post.id ");
      query.append(sortQuery);
      query.append(" LIMIT :limit OFFSET :offset");
    }

    query.append(") ").append(baseSql).append(appendOrderQuery?sortQuery:"").append(';');
    baseSql = query.toString();
    return baseSql;
  }
}