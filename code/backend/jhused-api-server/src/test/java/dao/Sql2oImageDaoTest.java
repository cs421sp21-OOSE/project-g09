package dao;

import dao.sql2oDao.Sql2oImageDao;
import exceptions.DaoException;
import model.Image;
import model.Post;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import util.DataStore;
import util.Database;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class Sql2oImageDaoTest {
  private static final List<Post> samplePosts = DataStore.samplePosts();
  private static Sql2o sql2o;
  private ImageDao imageDao;

  @BeforeAll
  static void connectToDatabase() throws URISyntaxException {
    Database.USE_TEST_DATABASE = true; // use test dataset
    Database.main(null); // reset dataset and add samples
    sql2o = Database.getSql2o();
  }

  @BeforeEach
  void injectDependency() throws URISyntaxException {
    Database.truncateTables(sql2o);
    Database.insertSampleData(sql2o, samplePosts);
    imageDao = new Sql2oImageDao(Database.getSql2o());
  }

  @AfterAll
  static void setUseProductionDatabase() {
    Database.USE_TEST_DATABASE = false; // use production dataset
  }

  @Test
  void doNothing() {
  }

  @Test
  void createNewImage() {
    Post post = samplePosts.get(0);
    Image newImage = new Image(UUID.randomUUID().toString(), post.getId(), "https://1i9wu42vzknf1h4zwf2to5aq-wpengine" +
        ".netdna-ssl.com/wp-content/uploads/2019/02/x_AN-1981_YthDeskPROF_o_s_.jpg");
    Image createdImage = imageDao.create(newImage);
    assertEquals(createdImage, newImage);
  }

  @Test
  void createImageWithoutPostIdThrowsException() {
    Image newImage = new Image(UUID.randomUUID().toString(), null, "https://1i9wu42vzknf1h4zwf2to5aq-wpengine" +
        ".netdna-ssl.com/wp-content/uploads/2019/02/x_AN-1981_YthDeskPROF_o_s_.jpg");
    assertThrows(DaoException.class, () -> {
      imageDao.create(newImage);
    });
  }

  @Test
  void createImageWithNonExistingPostIDThrowsException() {
    Image newImage = new Image(UUID.randomUUID().toString(), "574839".repeat(6), "https://1i9wu42vzknf1h4zwf2to5aq" +
        "-wpengine" +
        ".netdna-ssl.com/wp-content/uploads/2019/02/x_AN-1981_YthDeskPROF_o_s_.jpg");
    assertThrows(DaoException.class, () -> {
      imageDao.create(newImage);
    });
  }

  @Test
  void createDuplicateImage() {
    Image newImage = new Image(samplePosts.get(0).getImages().get(0).getId(), "574839".repeat(6), "https" +
        "://1i9wu42vzknf1h4zwf2to5aq-wpengine" +
        ".netdna-ssl.com/wp-content/uploads/2019/02/x_AN-1981_YthDeskPROF_o_s_.jpg");
    assertThrows(DaoException.class, () -> {
      imageDao.create(newImage);
    });
  }

  @Test
  void createImageWithNullImage() {
    assertThrows(DaoException.class, () -> {
      imageDao.create(null);
    });
  }

  @Test
  void updateImageWorks() {
    Post post = samplePosts.get(0);
    Image newImage = new Image(post.getImages().get(0).getId(), post.getId(), "https://1i9wu42vzknf1h4zwf2to5aq" +
        "-wpengine" +
        ".netdna-ssl.com/wp-content/uploads/2019/02/x_AN-1981_YthDeskPROF_o_s_.jpg");
    Image updatedImage = imageDao.update(newImage.getId(), newImage);
    assertEquals(updatedImage, newImage);
  }

  @Test
  void updateImageThrowsExceptionInvalidImage() {
    Post post = samplePosts.get(0);
    Image newImage = new Image(post.getImages().get(0).getId(), post.getId(), "https://1i9wu42vzknf1h4zwf2to5aq-wpengine" +
        ".netdna-ssl.com/wp-content/uploads/2019/02/x_AN-1981_YthDeskPROF_o_s_.jpg");
    assertThrows(DaoException.class, () -> {
      imageDao.update(newImage.getId(), null);
    });
  }

  @Test
  void updateImageReturnNullInvalidID() {
    Post post = samplePosts.get(0);
    Image newImage = new Image(post.getImages().get(0).getId(), post.getId(), "https://1i9wu42vzknf1h4zwf2to5aq" +
        "-wpengine" +
        ".netdna-ssl.com/wp-content/uploads/2019/02/x_AN-1981_YthDeskPROF_o_s_.jpg");
    assertNull(imageDao.update("756499".repeat(6), newImage));
  }
}
