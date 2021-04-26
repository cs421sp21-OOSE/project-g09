package dao.jdbiDao;

import dao.PostVisitDao;
import exceptions.DaoException;
import model.PostVisit;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementException;

public class JdbiPostVisitDao implements PostVisitDao {
  private final Jdbi jdbi;

  public JdbiPostVisitDao(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public PostVisit create(PostVisit postVisit) throws DaoException {
    String sql = "WITH inserted AS( INSERT INTO post_visit(post_id, user_id) "
        + "VALUES(:postId, :userId) ON CONFLICT DO NOTHING RETURNING *) SELECT * FROM inserted;";
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql).bindBean(postVisit).mapToBean(PostVisit.class).findOne().orElse(null)
      );
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public PostVisit read(String postId, String userId) throws DaoException {
    String sql = "SELECT * FROM post_visit WHERE post_visit.post_id=:postId AND post_visit.user_id=:userId;";
    try {
      return jdbi.inTransaction(handle -> handle.createQuery(sql).bind("postId", postId)
          .bind("userId", userId).mapToBean(PostVisit.class).findOne().orElse(null));
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public int visitCount(String postId) throws DaoException {
    String sql = "SELECT COUNT(user_id) FROM post_visit WHERE post_id=:postId;";
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql)
              .bind("postId", postId)
              .mapTo(Integer.class)
              .findOne().orElse(0));
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }
}
