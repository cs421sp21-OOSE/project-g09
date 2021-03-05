package dao;

import exceptions.DaoException;
import model.Category;
import model.Post;
import org.postgresql.jdbc.PgArray;
import org.sql2o.Connection;
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
   * Construct Sql2oPostDao.
   *
   * @param sql2o A Sql2o object is injected as a dependency;
   *              it is assumed sql2o is connected to a database that  contains a table called
   *              "Posts" with two columns: "id" and "title".
   */
  public Sql2oPostDao(Sql2o sql2o) {
    this.sql2o = sql2o;
  }

  @Override
  public Post create(Post post) throws DaoException {
    return null; // stub
  }

  @Override
  public Post read(String id) throws DaoException {
    return null; // stub
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
    return null; // stub
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
    String sql = "WITH updated AS ("
            + "UPDATE posts SET title = :newTitle, price = :newPrice, " +
            "description  = :newDescription, imageUrls = :newImageUrls, " +
            "hashtags = :newHashtags, " +
            "location = :newLocation WHERE uuid = :thisID " +
            "RETURNING *) SELECT * FROM updated;";

    //use this when testing category updates.
    /*String sql = "WITH updated AS ("
            + "UPDATE posts SET title = :newTitle, price = :newPrice, " +
            "description  = :newDescription, imageUrls = :newImageUrls, " +
            "hashtags = :newHashtags, category = :newCategory," +
            "location = :newLocation WHERE uuid = :thisID " +
            "RETURNING *) SELECT * FROM updated;";*/


    //make placer-holder variables for fields that might be null.
    String newDescription;
    List<String> imageUrls, hashtags;

    //check each from passed post to ensure no errors occur.
    if(post.getDescription() == null) {
      newDescription = "";
    } else {
      newDescription = post.getDescription();
    }

    if(post.getImageUrls() == null) {
      imageUrls = new ArrayList<>();
    } else {
      imageUrls = post.getImageUrls();
    }

    if(post.getHashtags() == null) {
      hashtags = new ArrayList<>();
    } else {
      hashtags = post.getHashtags();
    }

    //attempt to open connection and perform sql string.
    try (Connection conn = sql2o.open()) {
      return conn.createQuery(sql)
              .addParameter("newTitle", post.getTitle())
              .addParameter("newPrice", post.getPrice())
              .addParameter("newDescription", newDescription)
              .addParameter("newImageUrls", imageUrls)
              .addParameter("newHashtags", hashtags)
              //TODO Get category update working.
              //.addParameter("newCategory", post.getCategory())
              .addParameter("newLocation", post.getLocation())
              .addParameter("thisID", id)
              .executeAndFetchFirst(Post.class);
    } catch (Sql2oException ex) { //otherwise, fail
      throw new DaoException("Unable to update this post!", ex);
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
            + "DELETE FROM posts WHERE postId = :thisId RETURNING *"
            + ") SELECT * FROM deleted;";

    //attempt to open connection and perform sql string.
    try (Connection conn = sql2o.open()) {
      return conn.createQuery(sql)
              .addParameter("thisID", id)
              .executeAndFetchFirst(Post.class);
    } catch (Sql2oException ex) { //otherwise, fail
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
          new ArrayList<String>(Arrays.asList((String [])(((PgArray) post.get("imageurls")).getArray()))),
          new ArrayList<String>(Arrays.asList((String [])(((PgArray) post.get("hashtags")).getArray()))),
          Category.valueOf((String)post.get("category")),
          (String) post.get("location"));
    return convertedPost;
  }
}