package dao.jdbiDao;

import dao.HashtagDao;
import exceptions.DaoException;
import model.Hashtag;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.UUID;

public class jdbiHashtagDao implements HashtagDao {
  private final Jdbi jdbi;

  public jdbiHashtagDao(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Hashtag create(Hashtag hashtag) throws DaoException {
    String sql = "WITH inserted AS ("
        + "INSERT INTO hashtag(id, hashtag) "
        + "VALUES(:id, :hashtag) RETURNING *"
        + ") SELECT * FROM inserted;";
    if (hashtag != null && (hashtag.getId() == null || hashtag.getId() == "" || hashtag.getId().length() != 36)) {
      hashtag.setId(UUID.randomUUID().toString());
    }

    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql).bindBean(hashtag).mapToBean(Hashtag.class).one());
    } catch (IllegalStateException | NullPointerException ex) {
      throw new DaoException("Unable to create the hashtag: " + ex.getMessage(), ex);
    }
  }

  @Override
  public Hashtag read(String id) throws DaoException {
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery("SELECT * FROM hashtag WHERE id = :id;")
              .bind("id", id)
              .mapToBean(Hashtag.class)
              .one());
    } catch (IllegalStateException ex) {
      throw new DaoException("Unable to read a hashtags with id " + id
          + ": " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Hashtag> readAll() throws DaoException {
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery("SELECT * FROM hashtag;").mapToBean(Hashtag.class).list());
    } catch (IllegalStateException ex) {
      throw new DaoException("Unable to read hashtags from the database: " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Hashtag> readAllExactCaseInsensitive(String hashtagQuery) throws DaoException {
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery("SELECT * FROM hashtag WHERE hashtag.hashtag ILIKE :hashtagQuery;")
              .bind("hashtagQuery", hashtagQuery).mapToBean(Hashtag.class).list());
    } catch (IllegalStateException ex) {
      throw new DaoException("Unable to read hashtags from the database: " + ex.getMessage(), ex);
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
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql)
              .bind("id", id)
              .bind("hashtag", hashtag.getHashtag())
              .mapToBean(Hashtag.class).one());
    } catch (IllegalStateException | NullPointerException ex) {
      throw new DaoException("Unable to update the hashtag: " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Hashtag> getHashtagsOfPost(String postId) throws DaoException {
    String sql = "WITH ph AS (SELECT * FROM post_hashtag " +
        "WHERE post_hashtag.post_id = :postId) " +
        "SELECT hashtag.* FROM ph LEFT JOIN hashtag ON ph.hashtag_id = hashtag.id;";
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql)
              .bind("postId", postId)
              .mapToBean(Hashtag.class)
              .list());
    } catch (IllegalStateException ex) {
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
    if (hashtag != null && (hashtag.getId() == null || hashtag.getId() == "" || hashtag.getId().length() != 36)) {
      hashtag.setId(UUID.randomUUID().toString());
    }
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql)
              .bindBean(hashtag)
              .mapToBean(Hashtag.class)
              .one());
    } catch (IllegalStateException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }
}
