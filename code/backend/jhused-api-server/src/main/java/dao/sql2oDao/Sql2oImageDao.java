package dao.sql2oDao;

import dao.ImageDao;
import exceptions.DaoException;
import model.Image;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;
import java.util.UUID;

public class Sql2oImageDao implements ImageDao {
  private final Sql2o sql2o;

  public Sql2oImageDao(Sql2o sql2o) {
    this.sql2o = sql2o;
  }

  @Override
  public Image create(Image image) throws DaoException {
    String sql = "WITH inserted AS ("
        + "INSERT INTO image(id, post_id, url) "
        + "VALUES(:id, :postId, :url) RETURNING *"
        + ") SELECT * FROM inserted;";

    try (Connection conn = this.sql2o.open()) {
      if (image != null && (image.getId() == null || image.getId() == "" || image.getId().length() != 36)) {
        image.setId(UUID.randomUUID().toString());
      }
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      return query.bind(image).executeAndFetchFirst(Image.class);
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public List<Image> create(List<Image> images) throws DaoException {
    // stud
    return null;
  }

  @Override
  public Image update(String id, Image image) throws DaoException {
    String sql = "WITH updated AS ("
        + "UPDATE image SET url = :url WHERE id = :id RETURNING *"
        + ") SELECT * FROM updated;";
    try (Connection conn = sql2o.open()) {
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      return query.addParameter("url", image.getUrl())
          .addParameter("id", id)
          .executeAndFetchFirst(Image.class);
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException("Unable to update the image: " + ex.getMessage(), ex);
    }
  }

  @Override
  public Image delete(String id) throws DaoException {
    // stud
    return null;
  }

  @Override
  public List<Image> delete(List<String> ids) throws DaoException {
    // stud
    return null;
  }

  @Override
  public List<Image> getImagesOfPost(String postId) throws DaoException {
    try (Connection conn = sql2o.open()) {
      String sql = "SELECT * FROM image WHERE image.post_id=:postId;";
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      return query.addParameter("postId", postId).executeAndFetch(Image.class);
    } catch (Sql2oException ex) {
      throw new DaoException("Unable to read hashtags given postId from the database", ex);
    }
  }

  @Override
  public Image createOrUpdate(String id, Image image) throws DaoException {
    String sql = "WITH inserted AS ("
        + "INSERT INTO image(id, post_id, url) "
        + "VALUES(:id, :postId, :url) "
        + "ON CONFLICT (id) DO UPDATE "
        + "SET url = :url RETURNING *"
        + ") SELECT * FROM inserted;";

    try (Connection conn = this.sql2o.open()) {
      if (image != null && (image.getId() == null || image.getId() == "" || image.getId().length() != 36)) {
        image.setId(UUID.randomUUID().toString());
      }
      Query query = conn.createQuery(sql).setAutoDeriveColumnNames(true);
      return query.bind(image).executeAndFetchFirst(Image.class);
    } catch (Sql2oException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }
}
