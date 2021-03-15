package dao.sql2oDao;

import dao.HashtagDao;
import exceptions.DaoException;
import model.Hashtag;
import org.simpleflatmapper.sql2o.SfmResultSetHandlerFactoryBuilder;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oHashtagDao implements HashtagDao {
  private final Sql2o sql2o;

  public Sql2oHashtagDao(Sql2o sql2o) {
    this.sql2o = sql2o;
  }

  @Override
  public Hashtag create(Hashtag hashtag) throws DaoException {
    String sql = "WITH inserted AS ("
        + "INSERT INTO hashtag(id, hashtag) "
        + "VALUES(:id, :hashtag) RETURNING *"
        + ") SELECT * FROM inserted;";

    try (Connection conn = this.sql2o.open()) {
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      query.setResultSetHandlerFactoryBuilder(new SfmResultSetHandlerFactoryBuilder());
      return query.bind(hashtag).executeAndFetchFirst(Hashtag.class);
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public Hashtag read(String id) throws DaoException {
    try (Connection conn = sql2o.open()) {
      Query query = conn.createQuery("SELECT * FROM hashtag WHERE id = :id;").setAutoDeriveColumnNames(true);
      query.setResultSetHandlerFactoryBuilder(new SfmResultSetHandlerFactoryBuilder());
      return query.addParameter("id", id).executeAndFetchFirst(Hashtag.class);
    } catch (Sql2oException ex) {
      throw new DaoException("Unable to read a post with id " + id, ex);
    }
  }

  @Override
  public List<Hashtag> readAll() throws DaoException {
    try (Connection conn = sql2o.open()) {
      Query query = conn.createQuery("SELECT * FROM hashtag;").setAutoDeriveColumnNames(true);
      query.setResultSetHandlerFactoryBuilder(new SfmResultSetHandlerFactoryBuilder());
      return query.executeAndFetch(Hashtag.class);
    } catch (Sql2oException ex) {
      throw new DaoException("Unable to read posts from the database", ex);
    }
  }

  @Override
  public List<Hashtag> readAll(String hashtagQuery) throws DaoException {
    try (Connection conn = sql2o.open()) {
      Query query = conn.createQuery("SELECT * FROM hashtag where hashtag.hashtag ILIKE :hashtagQuery;")
          .setAutoDeriveColumnNames(true);
      query.setResultSetHandlerFactoryBuilder(new SfmResultSetHandlerFactoryBuilder());
      return query.addParameter("hashtagQuery", hashtagQuery).executeAndFetch(Hashtag.class);
    } catch (Sql2oException ex) {
      throw new DaoException("Unable to read posts from the database", ex);
    }
  }

  @Override
  public Hashtag update(String id, Hashtag hashtag) throws DaoException {
    String sql = "WITH updated AS ("
        + "UPDATE hashtag SET hashtag = :hashtag WHERE id = :id RETURNING *"
        + ") SELECT * FROM updated;";
    try (Connection conn = sql2o.open()) {
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      query.setResultSetHandlerFactoryBuilder(new SfmResultSetHandlerFactoryBuilder());
      return query.addParameter("hashtag", hashtag.getHashtag())
          .addParameter("id", id)
          .executeAndFetchFirst(Hashtag.class);
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException("Unable to update the hashtag: " + ex.getMessage(), ex);
    }
  }
}
