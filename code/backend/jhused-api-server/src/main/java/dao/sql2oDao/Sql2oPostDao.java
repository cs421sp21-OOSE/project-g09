package dao.sql2oDao;

import dao.PostDao;
import exceptions.DaoException;

import java.util.UUID;
import model.Category;
import model.Hashtag;
import model.Image;
import model.Post;
import org.postgresql.jdbc.PgArray;
import org.simpleflatmapper.sql2o.SfmResultSetHandlerFactoryBuilder;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Sql2oPostDao implements PostDao {

  private final Sql2o sql2o;
  /**
   * The rule is add [<child class name>s_] before child's column name,
   * for example, Post has List<Image> images, then in order for simpleflatmapper to
   * know how to convert, add "images_" to the column of table image, that is to convert
   * image.id to images_id, image.post_id to images_post_id, image.url to images_url.
   */
  private final String SELECT_POST_GIVEN_ID= "WITH posts AS (" +
          "SELECT post.*," +
          "image.id as images_id," +
          "image.post_id as images_post_id," +
          "image.url as images_url," +
          "hashtag.id as hashtags_id," +
          "hashtag.hashtag as hashtags_hashtag " +
          "FROM post " +
          "LEFT JOIN image on image.post_id=post.id " +
          "LEFT JOIN post_hashtag on post_hashtag.post_id=post.id " +
          "LEFT JOIN hashtag on hashtag.id=post_hashtag.hashtag_id " +
          "ORDER BY post.id) " +
          "SELECT * FROM posts "+
          "WHERE posts.id = :id";

  private final String SELECT_ALL_POSTS = "SELECT post.*," +
      "image.id as images_id," +
      "image.post_id as images_post_id," +
      "image.url as images_url," +
      "hashtag.id as hashtags_id," +
      "hashtag.hashtag as hashtags_hashtag " +
      "FROM post " +
      "LEFT JOIN image on image.post_id=post.id " +
      "LEFT JOIN post_hashtag on post_hashtag.post_id=post.id " +
      "LEFT JOIN hashtag on hashtag.id=post_hashtag.hashtag_id " +
      "ORDER BY post.id";

  /**
   * Construct Sql2oPostDao.
   *
   * @param sql2o A Sql2o object is injected as a dependency;
   *              it is assumed sql2o is connected to a database that  contains a table called
   *              "Posts" with two columns: "id" and "title".
   */
  public Sql2oPostDao(Sql2o sql2o) {
    this.sql2o = sql2o;
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
   * @param post The Post item to be created
   * @return
   * @throws DaoException
   */
  @Override
  public Post create(Post post) throws DaoException {
    if (post.getId().isEmpty()) {
      post.setId(UUID.randomUUID().toString());
    }

    String sql = "WITH inserted AS ("
        + "INSERT INTO posts(uuid, userid, title, price, description, "
        + "category, location) "
        + "VALUES(:uuid, :userid, :title, :price, :description, ARRAY[:imageurls], "
        + "ARRAY[:hashtags], CAST(:category AS Category), :location) RETURNING *"
        + ") SELECT * FROM inserted;";



    try (Connection conn = this.sql2o.open()) {
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      query.setResultSetHandlerFactoryBuilder(new SfmResultSetHandlerFactoryBuilder());
      return query.bind(Post.class).executeAndFetchFirst(Post.class);
    } catch (Sql2oException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public Post read(String id) throws DaoException {
    try (Connection conn = sql2o.open()) {
      return mapToPostsGetFirst(conn.createQuery("SELECT * FROM posts WHERE uuid = :id;")
          .addParameter("id", id)

          .executeAndFetchTable().asList());
    } catch (Sql2oException|SQLException ex) {
      throw new DaoException("Unable to read a post with id " + id, ex);
    }
  }

  @Override
  public List<Post> readAll() throws DaoException {
    try (Connection conn = sql2o.open()) {
      return mapToPosts(conn.createQuery("SELECT * FROM posts;").executeAndFetchTable().asList());
    } catch (Sql2oException | SQLException ex) {
      throw new DaoException("Unable to read posts from the database", ex);
    }
  }

  @Override
  public List<Post> readAll(String titleQuery) throws DaoException {
    try (Connection conn = sql2o.open()) {
      return mapToPosts(conn.createQuery("SELECT * FROM posts WHERE lower(title) LIKE :partial;")
          .addParameter("partial", "%" + titleQuery.toLowerCase() + "%")
          .executeAndFetchTable().asList());
    } catch (Sql2oException|SQLException ex) {
      throw new DaoException("Unable to read a post with partialTitle " + titleQuery, ex);
    }
  }

  @Override
  public Post update(String id, Post post) throws DaoException {

    //Need to check if post is valid before we check its fields.
    if(post == null) {
      Sql2oException ex = new Sql2oException();
      throw new DaoException("Unable to update this post!", ex);
    }

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
    String sql = "WITH updated AS (UPDATE posts SET " +
            "title = :newTitle, " +
            "price = :newPrice, " +
            "description = :newDescription, " +
            "imageUrls = ARRAY[:newImageUrls], " +
            "hashtags = ARRAY[:newHashtags], " +
            "category = CAST(:newCategory AS Category), " +
            "location = :newLocation " +
            "WHERE uuid = :thisID RETURNING *) SELECT * FROM updated;";

    //make placer-holder variables for fields that might be null.
    String newDescription;
    List<Image> imageUrls;
    List<Hashtag> hashtags;

    //check each from passed post to ensure no errors occur.
    if(post.getDescription() == null) {
      newDescription = "";
    } else {
      newDescription = post.getDescription();
    }

    if(post.getImages() == null) {
      imageUrls = new ArrayList<>();
    } else {
      imageUrls = post.getImages();
    }

    if(post.getHashtags() == null) {
      hashtags = new ArrayList<>();
    } else {
      hashtags = post.getHashtags();
    }

    //attempt to open connection and perform sql string.
    try (Connection conn = sql2o.open()) {
      return mapToPostsGetFirst(conn.createQuery(sql)
              .addParameter("newTitle", post.getTitle())
              .addParameter("newPrice", post.getPrice())
              .addParameter("newDescription", newDescription)
              .addParameter("newImageUrls", imageUrls)
              .addParameter("newHashtags", hashtags)
              .addParameter("newCategory", post.getCategory())
              .addParameter("newLocation", post.getLocation())
              .addParameter("thisID", id)
              .executeAndFetchTable().asList());
    } catch (Sql2oException|SQLException ex) { //otherwise, fail
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
            + "DELETE FROM posts WHERE uuid = :thisId RETURNING *"
            + ") SELECT * FROM deleted;";

    //attempt to open connection and perform sql string.
    try (Connection conn = sql2o.open()) {
      return mapToPostsGetFirst(conn.createQuery(sql)
              .addParameter("thisId", id)
              .executeAndFetchTable().asList());
    } catch (Sql2oException|SQLException ex) { //otherwise, fail
      throw new DaoException("Unable to delete this post!", ex);
    }

  }

  /**
   * Convert a list of maps returned by sql2o to a List of Post.
   * @param postMaps a list of maps returned by sql2o.
   * @return the converted list of Posts.
   * @throws SQLException
   */
  private List<Post> mapToPosts(List<Map<String, Object>> postMaps) throws SQLException{
    List<Post> posts = new ArrayList<>();
    for (Map<String, Object> post : postMaps) {
      posts.add(mapToPost(post));
    }
    return posts;
  }

  /**
   * Convert a list of maps returned by sql2o to a List of Post.
   * @param postMaps a list of maps returned by sql2o.
   * @return the first returned Post
   * @throws SQLException
   */
  private Post mapToPostsGetFirst(List<Map<String, Object>> postMaps) throws SQLException{
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
          new ArrayList<Image>(Arrays.asList((Image [])(((PgArray) post.get("imageurls")).getArray()))),
          new ArrayList<Hashtag>(Arrays.asList((Hashtag[])(((PgArray) post.get("hashtags")).getArray()))),
          Category.valueOf((String)post.get("category")),
          (String) post.get("location"));
    return convertedPost;
  }
}