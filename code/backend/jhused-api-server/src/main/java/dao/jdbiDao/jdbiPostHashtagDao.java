package dao.jdbiDao;

import dao.PostHashtagDao;
import exceptions.DaoException;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.List;
import java.util.Map;

public class jdbiPostHashtagDao implements PostHashtagDao {
  private final Jdbi jdbi;

  public jdbiPostHashtagDao(Jdbi jdbi) {
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
      return Map.of("postId", (String) resultSet.get("post_id"),
          "hashtagId", (String) resultSet.get("hashtag_id"));
    } catch (IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public List<Map<String, String>> create(List<String> postIds, List<String> hashtagIds) throws DaoException {
    String sql = "WITH inserted AS ("
        + "INSERT INTO post_hashtag(post_id, hashtag_id) "
        + "VALUES(:postId, :hashtagId) RETURNING *"
        + ") SELECT * FROM inserted;";
    return getMaps(postIds, hashtagIds, sql);
  }

  @Override
  public List<Map<String, String>> create(String postId, List<String> hashtagIds) throws DaoException {
    String sql = "WITH inserted AS ("
        + "INSERT INTO post_hashtag(post_id, hashtag_id) "
        + "VALUES(:postId, :hashtagId) RETURNING *"
        + ") SELECT * FROM inserted;";
    return getMaps(postId, hashtagIds, sql);
  }

  @Override
  public List<Map<String, String>> delete(List<String> postIds, List<String> hashtagIds) throws DaoException {
    String sql = "WITH deleted AS ("
        + "DELETE FROM post_hashtag "
        + "WHERE post_hashtag.post_id = :postId "
        + "AND post_hashtag.hashtag_id = :hashtagId) "
        + "SELECT * FROM deleted;";
    return getMaps(postIds, hashtagIds, sql);
  }

  @Override
  public List<Map<String, String>> delete(String postId, List<String> hashtagIds) throws DaoException {
    String sql = "WITH deleted AS ("
        + "DELETE FROM post_hashtag "
        + "WHERE post_hashtag.post_id = :postId "
        + "AND post_hashtag.hashtag_id = :hashtagId) "
        + "SELECT * FROM deleted;";
    return getMaps(postId, hashtagIds, sql);
  }

  private List<Map<String, String>> getMaps(String postId, List<String> hashtagIds, String sql) throws DaoException {
    try {
      return jdbi.inTransaction(handle -> {
        PreparedBatch batch = handle.prepareBatch(sql);
        for (int i = 0; i < hashtagIds.size(); ++i) {
          batch.bind("postId", postId).bind("hashtagId", hashtagIds.get(i)).add();
        }
        return batch.mapToMap(String.class).list();
      });
    } catch (IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  private List<Map<String, String>> getMaps(List<String> postIds, List<String> hashtagIds, String sql) throws DaoException {
    try {
      return jdbi.inTransaction(handle -> {
        PreparedBatch batch = handle.prepareBatch(sql);
        if (postIds.size() != hashtagIds.size())
          throw new DaoException("postIds.size != hashtagIds", null);
        for (int i = 0; i < postIds.size(); ++i) {
          batch.bind("postId", postIds.get(i)).bind("hashtagId", hashtagIds.get(i)).add();
        }
        return batch.mapToMap(String.class).list();
      });
    } catch (IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }
}
