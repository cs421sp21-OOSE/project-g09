package dao.jdbiDao;

import dao.ImageDao;
import exceptions.DaoException;
import model.Image;
import org.jdbi.v3.core.Jdbi;

import java.util.List;
import java.util.UUID;

public class jdbiImageDao implements ImageDao {
  private final Jdbi jdbi;

  public jdbiImageDao(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Image create(Image image) throws DaoException {
    String sql = "WITH inserted AS ("
        + "INSERT INTO image(id, post_id, url) "
        + "VALUES(:id, :postId, :url) RETURNING *"
        + ") SELECT * FROM inserted;";

    if (image != null && (image.getId() == null || image.getId() == "" || image.getId().length() != 36)) {
      image.setId(UUID.randomUUID().toString());
    }
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql).bindBean(image).mapToBean(Image.class).one()
      );
    } catch (IllegalStateException ex) {
      throw new DaoException("Unable to create the image: " + ex.getMessage(), ex);
    }
  }

  @Override
  public Image update(String id, Image image) throws DaoException {
    String sql = "WITH updated AS ("
        + "UPDATE image SET url = :url WHERE id = :id RETURNING *"
        + ") SELECT * FROM updated;";
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql)
              .bind("url", image.getUrl())
              .bind("id", id)
              .mapToBean(Image.class)
              .one());
    } catch (IllegalStateException ex) {
      throw new DaoException("Unable to update the image: " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Image> getImagesOfPost(String postId) throws DaoException {
    String sql = "SELECT * FROM image WHERE image.post_id=:postId;";
    try {
      return jdbi.inTransaction(handle -> handle.select(sql).mapToBean(Image.class).list());
    } catch (IllegalStateException ex) {
      throw new DaoException("Unable to read hashtags given postId from the database: " + ex.getMessage(), ex);
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
    if (image != null && (image.getId() == null || image.getId() == "" || image.getId().length() != 36)) {
      image.setId(UUID.randomUUID().toString());
    }
    try {
      return jdbi.inTransaction(handle ->
      {
        return handle.createQuery(sql)
            .bind("id", id)
            .bind("post_id", image.getPostId())
            .bind("url", image.getUrl())
            .mapToBean(Image.class).one();
      });
    } catch (IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }
}
