package dao.sql2oDao;

import dao.HashtagDao;
import exceptions.DaoException;
import model.Hashtag;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;
import java.util.UUID;

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
      if (hashtag != null && (hashtag.getId() == null || hashtag.getId() == "" || hashtag.getId().length() != 36)) {
        hashtag.setId(UUID.randomUUID().toString());
      }
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      return query.bind(hashtag).executeAndFetchFirst(Hashtag.class);
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public Hashtag read(String id) throws DaoException {
    try (Connection conn = sql2o.open()) {
      Query query = conn.createQuery("SELECT * FROM hashtag WHERE id = :id;").setAutoDeriveColumnNames(true);
      return query.addParameter("id", id).executeAndFetchFirst(Hashtag.class);
    } catch (Sql2oException ex) {
      throw new DaoException("Unable to read a post with id " + id, ex);
    }
  }

  @Override
  public List<Hashtag> readAll() throws DaoException {
    try (Connection conn = sql2o.open()) {
      Query query = conn.createQuery("SELECT * FROM hashtag;").setAutoDeriveColumnNames(true);
      return query.executeAndFetch(Hashtag.class);
    } catch (Sql2oException ex) {
      throw new DaoException("Unable to read posts from the database", ex);
    }
  }

  @Override
  public List<Hashtag> readAllExactCaseInsensitive(String hashtagQuery) throws DaoException {
    try (Connection conn = sql2o.open()) {
      Query query = conn.createQuery("SELECT * FROM hashtag where hashtag.hashtag ILIKE :hashtagQuery;")
          .setAutoDeriveColumnNames(true);
      return query.addParameter("hashtagQuery", hashtagQuery).executeAndFetch(Hashtag.class);
    } catch (Sql2oException ex) {
      throw new DaoException("Unable to read posts from the database", ex);
    }
  }

  @Override
  public List<Hashtag> readAll(String hashtagQuery) throws DaoException {
    return readAllExactCaseInsensitive("%" + hashtagQuery + "%");
  }

  @Override
  public Hashtag update(String id, Hashtag hashtag) throws DaoException {
    String sql = "WITH updated AS ("
        + "UPDATE hashtag SET hashtag = :hashtag WHERE id = :id RETURNING *"
        + ") SELECT * FROM updated;";
    try (Connection conn = sql2o.open()) {
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      return query.addParameter("hashtag", hashtag.getHashtag())
          .addParameter("id", id)
          .executeAndFetchFirst(Hashtag.class);
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException("Unable to update the hashtag: " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Hashtag> getHashtagsOfPost(String postId) throws DaoException {
    try (Connection conn = sql2o.open()) {
      String sql = "WITH ph AS (SELECT * FROM post_hashtag " +
          "WHERE post_hashtag.post_id = :postId) " +
          "SELECT hashtag.* FROM ph LEFT JOIN hashtag ON ph.hashtag_id = hashtag.id;";
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      return query.addParameter("postId", postId).executeAndFetch(Hashtag.class);
    } catch (Sql2oException ex) {
      throw new DaoException("Unable to read hashtags given postId from the database", ex);
    }
  }

  @Override
  public Hashtag createOrUpdate(String id, Hashtag hashtag) throws DaoException {
    String sql = "WITH inserted AS ("
        + "INSERT INTO hashtag(id, hashtag) "
        + "VALUES(:id, :hashtag) "
        + "ON CONFLICT (id) DO UPDATE "
        + "SET hashtag = :hashtag RETURNING *"
        + ") SELECT * FROM inserted;";

    try (Connection conn = this.sql2o.open()) {
      if (hashtag != null && (hashtag.getId() == null || hashtag.getId() == "" || hashtag.getId().length() != 36)) {
        hashtag.setId(UUID.randomUUID().toString());
      }
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      return query.bind(hashtag).executeAndFetchFirst(Hashtag.class);
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }
}
