package dao.jdbiDao;

import dao.HashtagDao;
import dao.ImageDao;
import dao.PostDao;
import dao.PostHashtagDao;
import exceptions.DaoException;
import model.Category;
import model.Hashtag;
import model.Image;
import model.Post;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import util.jdbiResultSetHandler.ResultSetLinkedHashMapAccumulatorProvider;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class jdbiPostDao implements PostDao {

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
  public jdbiPostDao(Jdbi jdbi) {
    this.jdbi = jdbi;
    imageDao = new jdbiImageDao(jdbi);
    hashtagDao = new jdbiHashtagDao(jdbi);
    postHashtagDao = new jdbiPostHashtagDao(jdbi);
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
        handle.createUpdate(insertPostSql).bindBean(post);
        if (!post.getImages().isEmpty()) {
          imageDao.create(post.getImages());
        }

        if (!post.getHashtags().isEmpty()) {
          List<Hashtag> createdHashtags = hashtagDao.create(post.getHashtags());
          List<String> hashtagIds = new ArrayList<>();
          List<String> postIds = new ArrayList<>();
          for (Hashtag hashtag : createdHashtags) {
            hashtagIds.add(hashtag.getId());
            postIds.add(post.getId());
          }
          postHashtagDao.create(postIds, hashtagIds);
        }
        return new ArrayList<>(handle.createQuery(SELECT_POST_GIVEN_ID).bind("id", post.getId())
            .reduceResultSet(new LinkedHashMap<>(),
                postAccumulator).values()).get(0);
      });
    } catch (IllegalStateException | NullPointerException ex) {
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
          .get(0));
    } catch (IllegalStateException | NullPointerException ex) {
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
    } catch (IllegalStateException ex) {
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
    } catch (IllegalStateException ex) {
      throw new DaoException("Unable to read a post with partialTitle " + titleQuery, ex);
    }
  }

  @Override
  public List<Post> readAllAdvanced(String specified, String searchQuery, Map<String, String> sortParams) {
    try {
      String sql = SELECT_POSTS;
      // Handle category query parameter
      // Adapted from searchCategory
      Category category = null;
      if (specified != null) {
        category = Category.valueOf(specified.toUpperCase()); // convert to enum
        sql = sql + " WHERE " +
            "post.category = CAST(:specifiedCategory AS Category)";
      }
      // Handle keyword search
      // Adapted from searchCategory
      if (searchQuery != null) {
        if (specified == null) {
          sql = sql + " WHERE ";
        } else {
          sql = sql + " AND ";
        }
        sql = sql + "(post.title ILIKE :partialTitle OR " +
            "post.description ILIKE :partialDescription OR " +
            "post.location ILIKE :partialLocation)";
      }
      // Handle sort
      // Adapted from readAll
      if (sortParams != null && !sortParams.isEmpty()) {
        StringBuilder sb = new StringBuilder(sql + " ORDER BY ");
        for (String key : sortParams.keySet()) {
          sb.append(key + " " + sortParams.get(key).toUpperCase() + ", ");
        }
        sb.delete(sb.length() - 2, sb.length()); // remove the extra comma and space
        sb.append(";");
        sql = sb.toString();
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
          query.bind("partialTitle", "%" + searchQuery + "%")
              .bind("partialDescription", "%" + searchQuery + "%")
              .bind("partialLocation", "%" + searchQuery + "%");
        }
        return new ArrayList<>(query.reduceResultSet(new LinkedHashMap<>(), postAccumulator).values());
      });

    } catch (IllegalStateException | NullPointerException ex) {
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

    String updateSql = "UPDATE post SET " +
        "user_id = :userId, " +
        "title = :title, " +
        "price = :price, " +
        "sale_state = CAST(:saleState AS SaleState)," +
        "description = :description, " +
        "category = CAST(:category AS Category), " +
        "location = :location " +
        "WHERE id = :id;";

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
      }

      return jdbi.inTransaction(handle -> {
        handle.createUpdate(updateSql)
            .bindBean(post).bind("id", id).execute();
        List<Image> toBeUpdatedImages = new ArrayList<>();
        List<Hashtag> toBeUpdatedHashtags = new ArrayList<>();
        List<String> toBeUpdatedHashtagsIds = new ArrayList<>();
        List<Image> deleteImages = imageDao.getImagesOfPost(post.getId());
        List<String> deleteImagesIds = new ArrayList<>();
        List<Hashtag> deleteHashtags = hashtagDao.getHashtagsOfPost(post.getId());
        List<String> deleteHashtagsIds = new ArrayList<>();
        for (Image image : post.getImages()) {
          image.setPostId(post.getId());
          // if existing images contain yet, updated version doesn't
          if (deleteImages.contains(image) && !post.getImages().contains(image)) {
            deleteImagesIds.add(image.getId());
            // add new image
          } else if (!deleteImages.contains(image) && post.getImages().contains(image)) {
            toBeUpdatedImages.add(image);
          }
        }
        imageDao.delete(deleteImagesIds);
        imageDao.create(toBeUpdatedImages);

        for (Hashtag hashtag : post.getHashtags()) {
          if (deleteHashtags.contains(hashtag) && !post.getHashtags().contains(hashtag)) {
            deleteHashtagsIds.add(hashtag.getId());
          } else if (!deleteHashtags.contains(hashtag) && post.getHashtags().contains(hashtag)) {
            toBeUpdatedHashtags.add(hashtag);
            toBeUpdatedHashtagsIds.add(hashtag.getId());
          }
        }
        postHashtagDao.delete(post.getId(), deleteHashtagsIds);
        hashtagDao.create(toBeUpdatedHashtags);
        postHashtagDao.create(post.getId(), toBeUpdatedHashtagsIds);

        return new ArrayList<>(handle.createQuery(SELECT_POST_GIVEN_ID)
            .bind("id", post.getId())
            .reduceResultSet(new LinkedHashMap<>(), postAccumulator)
            .values()).get(0);
      });
    } catch (IllegalStateException | NullPointerException ex) { //otherwise, fail
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
        Post post = handle.createQuery(sql).bind("id", id).mapToBean(Post.class).one();
        if (post != null) {
          post.setImages(images);
          post.setHashtags(hashtags);
        }
        return post;
      });
    } catch (IllegalStateException ex) { //otherwise, fail
      throw new DaoException("Unable to delete this post!", ex);
    }

  }

  @Override
  public List<Post> getCategory(Category specified) {
    String sql = SELECT_POST_BASE + " WHERE post.category = CAST(:specifiedCategory AS Category);";

    try {
      return jdbi.inTransaction(handle ->
          new ArrayList<>(handle.createQuery(sql)
              .bind("specifiedCategory", specified)
              .reduceResultSet(new LinkedHashMap<>(), postAccumulator)
              .values()));
    } catch (IllegalStateException | NullPointerException ex) {
      throw new DaoException("Unable to read a post with Category " + specified, ex);
    }

  }
}