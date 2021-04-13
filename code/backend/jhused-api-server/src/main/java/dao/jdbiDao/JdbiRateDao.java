package dao.jdbiDao;

import dao.RateDao;
import exceptions.DaoException;
import model.Rate;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementException;

import java.util.List;

public class JdbiRateDao implements RateDao {

  private final Jdbi jdbi;

  public JdbiRateDao(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Rate create(Rate rate) throws DaoException {
    String sql = "WITH inserted AS (INSERT INTO rate("
        + "rater_id, seller_id, rate) VALUES("
        + ":raterId, sellerId, rate) RETURNING *) "
        + "SELECT * FROM inserted;";
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql)
              .bindBean(rate)
              .mapToBean(Rate.class)
              .findOne()).orElse(null);
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public Rate createOrUpdate(String raterId, String sellerId, Rate rate) throws DaoException {
    String sql = "WITH inserted AS (INSERT INTO rate("
        + "rater_id, seller_id, rate) VALUES("
        + ":raterId, sellerId, rate) "
        + "ON CONFLICT (rater_id, seller_id) DO UPDATE "
        + "SET rate = :rate "
        + "RETURNING *) "
        + "SELECT * FROM inserted;";
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql)
              .bind("raterId", raterId)
              .bind("sellerId", sellerId)
              .bind("rate", rate.getRate())
              .mapToBean(Rate.class)
              .findOne()).orElse(null);
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public Rate read(String raterId, String sellerId) throws DaoException {
    String sql = "SELECT * FROM rate WHERE rate.rater_id = :raterId AND rate.seller_id = :sellerId;";
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql)
              .mapToBean(Rate.class)
              .findOne()).orElse(null);
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public List<Rate> read(String sellerId) throws DaoException {
    String sql = "SELECT * FROM rate WHERE rate.seller_id = :sellerId;";
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql)
              .mapToBean(Rate.class)
              .list());
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public Rate update(String raterId, String sellerId, Rate rate) throws DaoException {
    String sql = "WITH updated AS (UPDATE rate "
        + "SET rate = :rate WHERE rate.rater_id = :raterId AND rate.seller_id = :sellerId "
        + "RETURNING *) "
        + "SELECT * FROM updated;";
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql)
              .bind("raterId", raterId)
              .bind("sellerId", sellerId)
              .mapToBean(Rate.class)
              .findOne()).orElse(null);
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public Rate delete(String raterId, String sellerId) throws DaoException {
    String sql = "WITH deleted AS ("
        + "DELETE FROM rate WHERE rate.rater_id = :raterId AND rate.seller_id = :sellerId RETURNING *)"
        + "SELECT * FROM deleted;";
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql)
              .bind("raterId", raterId)
              .bind("sellerId", sellerId)
              .mapToBean(Rate.class).findOne()).orElse(null);
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to delete the rate with raterId: " + raterId + "sellerId: " + sellerId
          +" error message: " + ex.getMessage(), ex);
    }
  }
}
