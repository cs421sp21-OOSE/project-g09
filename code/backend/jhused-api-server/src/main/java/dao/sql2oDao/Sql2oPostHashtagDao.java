package dao.jdbiDao;

import dao.PostHashtagDao;
import exceptions.DaoException;
import org.jdbi.v3.core.Jdbi;

import java.util.Map;

public class JdbiPostHashtagDao implements PostHashtagDao {
  private final Jdbi jdbi;

  public JdbiPostHashtagDao(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Map<String, String> create(String postId, String hashtagId) throws DaoException {
    String sql = "WITH inserted AS ("
        + "INSERT INTO post_hashtag(post_id, hashtag_id) "
        + "VALUES(:postId, :hashtagId) RETURNING *"
        + ") SELECT * FROM inserted;";

    try {

      Map<String, Object> resultSet = jdbi.inTransaction(handle ->
          handle.createQuery(sql)
      .bind("postId", postId)
      .bind("hashtagId", hashtagId)
      .mapToMap()
      .one());
      return Map.of("postId", (String)resultSet.get("post_id"),
          "hashtagId", (String)resultSet.get("hashtag_id"));
    } catch (IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }
}
