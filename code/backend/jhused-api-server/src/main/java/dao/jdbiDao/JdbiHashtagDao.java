package dao.jdbiDao;

import dao.HashtagDao;
import exceptions.DaoException;
import model.Hashtag;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.core.statement.StatementException;

import java.util.List;
import java.util.UUID;

public class JdbiHashtagDao implements HashtagDao {
  private final Jdbi jdbi;

  public JdbiHashtagDao(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Hashtag create(Hashtag hashtag) throws DaoException {
    String sql = "WITH inserted AS ("
        + "INSERT INTO hashtag(id, hashtag) "
        + "VALUES(:id, :hashtag) RETURNING *"
        + ") SELECT * FROM inserted;";
    if (hashtag != null && (hashtag.getId() == null || hashtag.getId().length() != 36)) {
      hashtag.setId(UUID.randomUUID().toString());
    }

    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql).bindBean(hashtag).mapToBean(Hashtag.class).findOne()).orElse(null);
    } catch (IllegalStateException | NullPointerException | StatementException ex) {
      throw new DaoException("Unable to create the hashtag: " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Hashtag> create(List<Hashtag> hashtags) throws DaoException {
    String sql = "INSERT INTO hashtag(id, hashtag) VALUES(:id, :hashtag);";

    try {
      return jdbi.inTransaction(handle -> {
        PreparedBatch batch = handle.prepareBatch(sql);
        for (Hashtag hashtag : hashtags) {
          if (hashtag != null && (hashtag.getId() == null || hashtag.getId().length() != 36)) {
            hashtag.setId(UUID.randomUUID().toString());
          }
          batch.bindBean(hashtag).add();
        }
        return batch.executeAndReturnGeneratedKeys().mapToBean(Hashtag.class).list();
      });
    } catch (IllegalStateException | StatementException | NullPointerException ex) {
      throw new DaoException("Unable to create the hashtags: " + ex.getMessage(), ex);
    }
  }

  @Override
  public Hashtag read(String id) throws DaoException {
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery("SELECT * FROM hashtag WHERE id = :id;")
              .bind("id", id)
              .mapToBean(Hashtag.class)
              .findOne()).orElse(null);
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to read a hashtags with id " + id
          + ": " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Hashtag> readAll() throws DaoException {
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery("SELECT * FROM hashtag;").mapToBean(Hashtag.class).list());
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to read hashtags from the database: " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Hashtag> readAllExactCaseInsensitive(String hashtagQuery) throws DaoException {
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery("SELECT * FROM hashtag WHERE hashtag.hashtag ILIKE :hashtagQuery;")
              .bind("hashtagQuery", hashtagQuery).mapToBean(Hashtag.class).list());
    } catch (IllegalStateException | StatementException ex) {
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
              .mapToBean(Hashtag.class).findOne()).orElse(null);
    } catch (IllegalStateException | StatementException | NullPointerException ex) {
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
    } catch (IllegalStateException | StatementException ex) {
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
              .findOne()).orElse(null);
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public Hashtag createIfNotExist(Hashtag hashtag) throws DaoException {
    String sql = "WITH inserted AS ("
        + "INSERT INTO hashtag(id, hashtag) "
        + "VALUES(:id, :hashtag) RETURNING *"
        + ") SELECT * FROM inserted;";
    if (hashtag != null && (hashtag.getId() == null || hashtag.getId().length() != 36)) {
      hashtag.setId(UUID.randomUUID().toString());
    }

    try {
      return jdbi.inTransaction(handle -> {
        List<Hashtag> existingHashtags = handle.createQuery("SELECT * FROM hashtag WHERE hashtag.hashtag ILIKE "
            + ":hashtagQuery;")
            .bind("hashtagQuery", hashtag.getHashtag()).mapToBean(Hashtag.class).list();
        if (existingHashtags.isEmpty()) {
          return handle.createQuery(sql).bindBean(hashtag).mapToBean(Hashtag.class).findOne().orElse(null);
        } else {
          return existingHashtags.get(0);
        }
      });
    } catch (IllegalStateException | NullPointerException | StatementException ex) {
      throw new DaoException("Unable to create the hashtag: " + ex.getMessage(), ex);
    }
  }
}
