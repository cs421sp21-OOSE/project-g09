package dao.jdbi;

import dao.ImageDao;
import dao.jdbiDao.JdbiImageDao;
import exceptions.DaoException;
import model.Image;
import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.database.DataStore;
import util.database.Database;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JdbiImageDaoTest {
  private static final List<Post> samplePosts = DataStore.samplePosts();
  private static final List<User> sampleUsers = DataStore.sampleUsers();
  private static Jdbi jdbi;
  private ImageDao imageDao;

  @BeforeAll
  static void connectToDatabase() throws URISyntaxException {
    Database.USE_TEST_DATABASE = true; // use test dataset
    Database.main(null); // reset dataset and add samples
    jdbi = Database.getJdbi();
  }

  @BeforeEach
  void injectDependency() throws URISyntaxException {
    Database.truncateTables(jdbi);
    Database.insertSampleUsers(jdbi, sampleUsers);
    Database.insertSamplePosts(jdbi, samplePosts);
    imageDao = new JdbiImageDao(jdbi);
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
      imageDao.create((Image)null);
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

  @Test
  void getImagesGivenPostIdWork() {
    for (Post post: samplePosts)
    {
      assertEquals(post.getImages(), imageDao.getImagesOfPost(post.getId()));
    }
  }

  @Test
  void getImagesGivenInvalidPostIdReturnEmpty() {
    assertEquals(0, imageDao.getImagesOfPost("999771".repeat(6)).size());
  }

  @Test
  void getImagesGivenNullPostIdReturnEmpty() {
    assertEquals(0, imageDao.getImagesOfPost(null).size());
  }
}
