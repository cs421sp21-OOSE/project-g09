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
    return null; // stub
  }

  @Override
  public Post delete(String id) throws DaoException {
    String sql = "WITH deleted AS ("
            + "DELETE FROM posts WHERE postId = :thisId RETURNING *"
            + ") SELECT * FROM deleted;";
    try (Connection conn = sql2o.open()) {
      return conn.createQuery(sql)
              .addParameter("thisID", id)
              .executeAndFetchFirst(Post.class);
    } catch (Sql2oException ex) {
      throw new DaoException("Unable to delete this post", ex);
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