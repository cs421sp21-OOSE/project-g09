package dao.sql2oDao;

import dao.PostHashtagDao;
import exceptions.DaoException;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;
import java.util.Map;

public class Sql2oPostHashtagDao implements PostHashtagDao {
  private final Sql2o sql2o;

  public Sql2oPostHashtagDao(Sql2o sql2o) {
    this.sql2o = sql2o;
  }

  @Override
  public Map<String, String> create(String postId, String hashtagId) throws DaoException {
    String sql = "WITH inserted AS ("
        + "INSERT INTO post_hashtag(post_id, hashtag_id) "
        + "VALUES(:postId, :hashtagId) RETURNING *"
        + ") SELECT * FROM inserted;";

    try (Connection conn = this.sql2o.open()) {
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      Map<String, Object> resultSet =
          query.addParameter("postId", postId)
              .addParameter("hashtagId", hashtagId)
              .executeAndFetchTable().asList().get(0);
      return Map.of("postId", (String)resultSet.get("post_id"),
          "hashtagId", (String)resultSet.get("hashtag_id"));
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public List<Map<String, String>> create(List<String> postIds, List<String> hashtagIds) throws DaoException {
    // stud
    return null;
  }

  @Override
  public List<Map<String, String>> create(String postId, List<String> hashtagIds) throws DaoException {
    // stud
    return null;
  }

  @Override
  public List<Map<String, String>> delete(List<String> postIds, List<String> hashtagIds) throws DaoException {
    // stud
    return null;
  }

  @Override
  public List<Map<String, String>> delete(String postId, List<String> hashtagIds) throws DaoException {
    // stud
    return null;
  }
}
