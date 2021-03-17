package dao.sql2oDao;

import dao.HashtagDao;
import dao.ImageDao;
import dao.PostDao;
import dao.PostHashtagDao;
import exceptions.DaoException;
import model.Category;
import model.Hashtag;
import model.Image;
import model.Post;
import org.postgresql.jdbc.PgArray;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

public class Sql2oPostDao implements PostDao {

  private static final String SELECT_ALL_POSTS = "";
  private final Sql2o sql2o;
  private final ImageDao imageDao;
  private final HashtagDao hashtagDao;
  private final PostHashtagDao postHashtagDao;

  /**
   * Construct Sql2oPostDao.
   *
   * @param sql2o A Sql2o object is injected as a dependency;
   *              it is assumed sql2o is connected to a database that  contains a table called
   *              "Posts" with two columns: "id" and "title".
   */
  public Sql2oPostDao(Sql2o sql2o) {
    this.sql2o = sql2o;
    imageDao = new Sql2oImageDao(sql2o);
    hashtagDao = new Sql2oHashtagDao(sql2o);
    postHashtagDao = new Sql2oPostHashtagDao(sql2o);
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
    String insertPostSql = "WITH posts AS (INSERT INTO post(" +
        "id, user_id, title, price, description, category, location) " +
        "VALUES(:id, :userId, :title, :price, :description, CAST(:category AS Category), :location)" +
        "RETURNING *) SELECT * FROM posts;";

    try (Connection conn = this.sql2o.open()) {
      // will catch NullPointerException
      if (post.getId().isEmpty()) {
        post.setId(UUID.randomUUID().toString());
      }

      // first insert the post
      Post createdPost =
          conn.createQuery(insertPostSql).setAutoDeriveColumnNames(true).bind(post).executeAndFetchFirst(Post.class);

      if (post != null) {
        // then insert image and store created images
        List<Image> createdImages = new ArrayList<>();
        if (!post.getImages().isEmpty()) {
          for (Image image : post.getImages()) {
            createdImages.add(imageDao.create(image));
          }
        }

        // then insert hashtag and post_hashtag, and store created hashtags
        List<Hashtag> createdHashtags = new ArrayList<>();
        if (!post.getHashtags().isEmpty()) {
          for (Hashtag hashtag : post.getHashtags()) {
            // check if the hashtag already exists, using exact search (though case insensitive)
            List<Hashtag> existingHashtag = hashtagDao.readAllExactCaseInsensitive(hashtag.getHashtag());
            if (existingHashtag.isEmpty()) {
              createdHashtags.add(hashtagDao.create(hashtag));
              postHashtagDao.create(post.getId(), hashtag.getId());
            } else {
              createdHashtags.add(hashtag);
              postHashtagDao.create(post.getId(), existingHashtag.get(0).getId());
            }
          }
        }

        // add images and hashtags to post
        createdPost.setImages(createdImages);
        createdPost.setHashtags(createdHashtags);
      }
      return createdPost;
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public Post read(String id) throws DaoException {
    try (Connection conn = sql2o.open()) {
      Post post = conn.createQuery("SELECT * FROM post WHERE post.id=:id")
          .setAutoDeriveColumnNames(true)
          .addParameter("id", id)
          .executeAndFetchFirst(Post.class);
      if (post != null) {
        post.setImages(imageDao.getImagesOfPost(post.getId()));
        post.setHashtags(hashtagDao.getHashtagsOfPost(post.getId()));
      }
      return post;
    } catch (Sql2oException | NullPointerException ex) {
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
    try (Connection conn = sql2o.open()) {
      List<Post> posts = conn.createQuery("SELECT * FROM post")
          .setAutoDeriveColumnNames(true)
          .executeAndFetch(Post.class);
      if (!posts.isEmpty()) {
        for (Post post : posts) {
          post.setImages(imageDao.getImagesOfPost(post.getId()));
          post.setHashtags(hashtagDao.getHashtagsOfPost(post.getId()));
        }
      }
      return posts;
    } catch (Sql2oException ex) {
      throw new DaoException("Unable to read posts from the database", ex);
    }
  }

  @Override
  public List<Post> readAll(String titleQuery) throws DaoException {
    try (Connection conn = sql2o.open()) {
      String sql = "SELECT * FROM post WHERE post.title ILIKE :partial;";
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      List<Post> posts = query.addParameter("partial", "%" + titleQuery + "%").executeAndFetch(Post.class);
      if (!posts.isEmpty()) {
        for (Post post : posts) {
          post.setImages(imageDao.getImagesOfPost(post.getId()));
          post.setHashtags(hashtagDao.getHashtagsOfPost(post.getId()));
        }
      }
      return posts;
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException("Unable to read a post with partialTitle " + titleQuery, ex);
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

    /**
     * TODO not necessary, but using the ARRAY cast spews out the same
     *  strange error as the delete function.
     */
    String updateSql = "WITH updated AS (UPDATE post SET " +
        "user_id = :userId, " +
        "title = :title, " +
        "price = :price, " +
        "description = :description, " +
        "category = CAST(:category AS Category), " +
        "location = :location " +
        "WHERE id = :id RETURNING *) SELECT * FROM updated;";

    //attempt to open connection and perform sql string.
    try (Connection conn = sql2o.open()) {
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
      Post updatedPost = conn.createQuery(updateSql).setAutoDeriveColumnNames(true)
          .bind(post).addParameter("id", id).executeAndFetchFirst(Post.class);
      if (updatedPost != null) {
        List<Image> toBeUpdatedImages = new ArrayList<>();
        List<Hashtag> toBeUpdatedHashtags = new ArrayList<>();
        for (Image image:post.getImages()) {
            toBeUpdatedImages.add(imageDao.createOrUpdate(image.getId(), image));
        }
        for (Hashtag hashtag:post.getHashtags()) {
          toBeUpdatedHashtags.add(hashtagDao.createOrUpdate(hashtag.getId(), hashtag));
        }
        updatedPost.setImages(toBeUpdatedImages);
        updatedPost.setHashtags(toBeUpdatedHashtags);
      }

      return updatedPost;
    } catch (Sql2oException | NullPointerException ex) { //otherwise, fail
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
    try (Connection conn = sql2o.open()) {
      List<Image> images = imageDao.getImagesOfPost(id);
      List<Hashtag> hashtags = hashtagDao.getHashtagsOfPost(id);
      Post post = conn.createQuery(sql).setAutoDeriveColumnNames(true)
          .addParameter("id", id).executeAndFetchFirst(Post.class);
      if (post != null) {
        post.setImages(images);
        post.setHashtags(hashtags);
      }
      return post;
    } catch (Sql2oException ex) { //otherwise, fail
      throw new DaoException("Unable to delete this post!", ex);
    }

  }

  @Override
  public List<Post> searchAll(String searchQuery) {
    String sql = "SELECT * FROM post WHERE " +
            "post.title ILIKE :partialTitle OR " +
            "post.description ILIKE :partialDescription OR " +
            "post.location ILIKE :partialLocation;";

    try (Connection conn = sql2o.open()) {
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      List<Post> posts = query
              .addParameter("partialTitle", "%" + searchQuery + "%")
              .addParameter("partialDescription", "%" + searchQuery + "%")
              .addParameter("partialLocation", "%" + searchQuery + "%")
              .executeAndFetch(Post.class);
      if (!posts.isEmpty()) {
        for (Post post : posts) {
          post.setImages(imageDao.getImagesOfPost(post.getId()));
          post.setHashtags(hashtagDao.getHashtagsOfPost(post.getId()));
        }
      }
      return posts;
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException("Unable to read a post with matching items: " +
              searchQuery, ex);
    }
  }

  @Override
  public List<Post> searchCategory(String searchQuery, Category specified) {
    return null; //stub
  }


  @Override
  public List<Post> getCategory(Category specified) {
    String sql = "SELECT * FROM post WHERE post.category = CAST(:specifiedCategory AS Category);";

    try (Connection conn = sql2o.open()) {
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      List<Post> posts = query
              .addParameter("specifiedCategory", specified)
              .executeAndFetch(Post.class);
      if (!posts.isEmpty()) {
        for (Post post : posts) {
          post.setImages(imageDao.getImagesOfPost(post.getId()));
          post.setHashtags(hashtagDao.getHashtagsOfPost(post.getId()));
        }
      }
      return posts;
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException("Unable to read a post with Category " + specified, ex);
    }

  }

  /**
   * Convert a list of maps returned by sql2o to a List of Post.
   *
   * @param postMaps a list of maps returned by sql2o.
   * @return the converted list of Posts.
   * @throws SQLException
   */
  private List<Post> mapToPosts(List<Map<String, Object>> postMaps) throws SQLException {
    List<Post> posts = new ArrayList<>();
    for (Map<String, Object> post : postMaps) {
      posts.add(mapToPost(post));
    }
    return posts;
  }

  /**
   * Convert a list of maps returned by sql2o to a List of Post.
   *
   * @param postMaps a list of maps returned by sql2o.
   * @return the first returned Post
   * @throws SQLException
   */
  private Post mapToPostsGetFirst(List<Map<String, Object>> postMaps) throws SQLException {
    List<Post> posts = new ArrayList<>();
    for (Map<String, Object> post : postMaps) {
      posts.add(mapToPost(post));
    }
    if (posts.isEmpty())
      posts.add(null);
    return posts.get(0);
  }

  /**
   * Convert a Map returned by sql2o to Post.
   *
   * @param post a Map returned by sql2o.
   * @return the converted Post.
   * @throws SQLException
   */
  private Post mapToPost(Map<String, Object> post) throws SQLException {
    // Note "imageurls" and "userid" must be in small case!!!
    // Database is not case sensitive to column name!
    // Might need refactor in the future.
    Post convertedPost;
    convertedPost = new Post((String) post.get("uuid"),
        (String) post.get("userid"),
        (String) post.get("title"),
        ((BigDecimal) post.get("price")).doubleValue(),
        (String) post.get("description"),
        new ArrayList<Image>(Arrays.asList((Image[]) (((PgArray) post.get("imageurls")).getArray()))),
        new ArrayList<Hashtag>(Arrays.asList((Hashtag[]) (((PgArray) post.get("hashtags")).getArray()))),
        Category.valueOf((String) post.get("category")),
        (String) post.get("location"));
    return convertedPost;
  }
}