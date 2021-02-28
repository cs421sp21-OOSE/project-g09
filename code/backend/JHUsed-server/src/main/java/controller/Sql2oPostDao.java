package controller;

import exceptions.DaoException;
import java.util.List;
import model.Post;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

public class Sql2oPostDao implements PostDao {

  private final Sql2o sql2o;

  /**
   * Construct Sql2oPostDao.
   *
   * @param sql2o A Sql2o object is injected as a dependency; 
   *   it is assumed sql2o is connected to a database that  contains a table called 
   *   "Posts" with two columns: "id" and "title".
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
      return conn.createQuery("SELECT * FROM posts;").executeAndFetch(Post.class);
    } catch (Sql2oException ex) {
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
    return null; // stub
  }
}
