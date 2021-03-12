package dao;

import dao.sql2oDao.Sql2oPostDao;
import exceptions.DaoException;
import model.Category;
import model.Post;
import org.junit.jupiter.api.*;
import util.DataStore;
import util.Database;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class Sql2oPostDaoTest {
  private static List<Post> samples;
  private PostDao postDao;

  @BeforeAll
  static void setSamplePosts() {
    samples = DataStore.samplePosts();
  }

  @BeforeEach
  void injectDependency() throws URISyntaxException {
    Database.USE_TEST_DATABASE = true; // use test dataset
    Database.main(null); // reset dataset and add samples
    postDao = new Sql2oPostDao(Database.getSql2o());
  }

  @AfterAll
  static void setUseProductionDatabase() {
    Database.USE_TEST_DATABASE = false; // use production dataset
  }

  @Test
  void doNothing() {

  }

  @Test
  @DisplayName("create works for valid input")
  void createNewPost() throws DaoException {
    Post c1 = new Post(UUID.randomUUID().toString(), "001",
        "Dummy furniture", 30D,
        "Description of dummy furniture",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Location of dummy furniture"
    );
    Post c2 = postDao.create(c1);
    assertEquals(c1, c2);
  }

  @Test
  @DisplayName("create throws exception for duplicate post")
  void createThrowsExceptionDuplicateData() {
    Post c1 = new Post("0".repeat(36), "001",
        "Dummy furniture", 30D,
        "Description of dummy furniture",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Location of dummy furniture"
    );
    assertThrows(DaoException.class, () -> {
      postDao.create(c1);
    });
  }

  @Test
  @DisplayName("create throws exception for invalid input")
  void createThrowsExceptionIncompleteData() {
    Post c1 = new Post(null, "001",
        "Dummy furniture", 30D,
        "Description of dummy furniture",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Location of dummy furniture"
    );

    c1.setId("0" + " ".repeat(35));
    c1.setPrice(null);
    assertThrows(DaoException.class, () -> {
      postDao.create(c1);
    });
    c1.setPrice(33.4);
    c1.setLocation(null);
    assertThrows(DaoException.class, () -> {
      postDao.create(c1);
    });
    c1.setLocation("some location");
    c1.setTitle(null);
    assertThrows(DaoException.class, () -> {
      postDao.create(c1);
    });
    c1.setTitle("some title");
    c1.setCategory(null);
    assertThrows(DaoException.class, () -> {
      postDao.create(c1);
    });
  }

  @Test
  @DisplayName("read a post given its uuid")
  void readPostGivenUUID() {
    for (Post c2 : samples) {
      Post c1 = postDao.read(c2.getId());
      assertEquals(c2, c1);
    }
  }

  @Test
  @DisplayName("read returns null given invalid uuid")
  void readPostGivenInvalidId() {
    Post c1 = postDao.read("928123");
    assertNull(c1);
  }

  @Test
  @DisplayName("read all the posts")
  void readAll() {
    List<Post> posts = postDao.readAll();
    assertIterableEquals(samples, posts);
  }

  @Test
  @DisplayName("read all the posts that contain a query string in their title")
  void readAllGivenTitle() {
    String query = "Dummy";
    List<Post> posts = postDao.readAll(query);
    assertNotEquals(0, posts.size());
    for (Post post : posts) {
      assertTrue(post.getTitle().toLowerCase().contains(query.toLowerCase()));
    }
  }

  @Test
  @DisplayName("readAll(query) returns empty list when query not matches posts' title")
  void readAllGivenNonExistingTitle() {
    String query = "game";
    List<Post> posts = postDao.readAll(query);
    assertEquals(0, posts.size());
  }

  @Test
  @DisplayName("updating a post works")
  void updateWorks() {
    //create a post to send to the update method.
    Post ogPost = new Post(samples.get(0).getId(), "001",
        "Dummy furniture", 30D,
        "Description of dummy furniture",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Location of dummy furniture"
    );

    //get the post back, give the first item in samples uuid.
    Post post = postDao.update(samples.get(0).getId(), ogPost);
    assertEquals(ogPost, post);
  }

  @Test
  @DisplayName("Update returns null for an invalid uuid")
  void updateReturnsNullInvalidCode() {
    Post ogPost = new Post("100", "Updated Title!",100D, Category.CAR,
            "Baltimore");
    Post post = postDao.update("25", ogPost);
    assertNull(post);
  }

  @Test
  @DisplayName("Update throws exception for an invalid title")
  void updateThrowsExceptionInvalidPost() {
    assertThrows(DaoException.class, () -> {
      postDao.update(samples.get(0).getId(), null);
    });
  }

  @Test
  @DisplayName("delete works for valid input")
  void deleteExistingPost() {
    //TODO figure out weird error. Post is deleted, but return is not correct.
    Post postDeleted = postDao.delete(samples.get(0).getId());
    assertEquals(postDeleted, samples.get(0));
    //TODO uncomment this once read is implemented
    //assertNull(postDao.read(postDeleted.getUuid()));
  }

  @Test
  @DisplayName("delete returns null for non existing post")
  void deleteThrowsExceptionNoMatchData() {
    assertNull(postDao.delete("25"));
  }

  @Test
  @DisplayName("delete returns null for invalid input")
  void deleteThrowsExceptionIncompleteData() {
    assertNull(postDao.delete(null));
  }

}
