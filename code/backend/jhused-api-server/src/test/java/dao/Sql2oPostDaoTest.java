package dao;

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
  void createNewPost() {
    Post c1 = new Post(UUID.randomUUID().toString(), "001",
        "Dummy furniture", 30D,
        "Description of dummy furniture",
        DataStore.sampleImageUrls(),
        DataStore.sampleHashtags(),
        Category.FURNITURE,
        "Location of dummy furniture"
    );
    Post c2 = postDao.create(c1);
    assertEquals(c1, c2);
  }

  @Test
  @DisplayName("create throws exception for duplicate post")
  void createThrowsExceptionDuplicateData() {
    Post c1 = new Post("0" + " ".repeat(35), "001",
        "Dummy furniture", 30D,
        "Description of dummy furniture",
        DataStore.sampleImageUrls(),
        DataStore.sampleHashtags(),
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
        DataStore.sampleImageUrls(),
        DataStore.sampleHashtags(),
        Category.FURNITURE,
        "Location of dummy furniture"
    );
    assertThrows(DaoException.class, () -> {
      postDao.create(c1);
    });
    c1.setUserId("0" + " ".repeat(35));
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
  void readPostGivenOfferingName() {
    for (Post c2 : samples) {
      Post c1 = postDao.read(c2.getUuid());
      assertEquals(c2, c1);
    }
  }

//  @Test
//  @DisplayName("read returns null given invalid offering name")
//  void readPostGivenInvalidOfferingName() {
//    Post c1 = postDao.read("EN.00.999");
//    assertNull(c1);
//  }
//
//  @Test
//  @DisplayName("read all the posts")
//  void readAll() {
//    List<Post> posts = postDao.readAll();
//    assertIterableEquals(samples, posts);
//  }
//
//  @Test
//  @DisplayName("read all the posts that contain a query string in their title")
//  void readAllGivenTitle() {
//    String query = "data";
//    List<Post> posts = postDao.readAll(query);
//    assertNotEquals(0, posts.size());
//    for (Post post : posts) {
//      assertTrue(post.getTitle().toLowerCase().contains(query.toLowerCase()));
//    }
//  }
//
//  @Test
//  @DisplayName("readAll(query) returns empty list when query not matches posts' title")
//  void readAllGivenNonExistingTitle() {
//    String query = "game";
//    List<Post> posts = postDao.readAll(query);
//    assertEquals(0, posts.size());
//  }
//
//  @Test
//  @DisplayName("updating a post works")
//  void updateWorks() {
//    String title = "Updated Title!";
//    Post post = postDao.update(samples.get(0).getOfferingName(), title);
//    assertEquals(title, post.getTitle());
//    assertEquals(samples.get(0).getOfferingName(), post.getOfferingName());
//  }
//
//  @Test
//  @DisplayName("Update returns null for an invalid offeringCode")
//  void updateReturnsNullInvalidCode() {
//    Post post = postDao.update("EN.000.999", "UpdatedTitle");
//    assertNull(post);
//  }
//
//  @Test
//  @DisplayName("Update throws exception for an invalid title")
//  void updateThrowsExceptionInvalidTitle() {
//    assertThrows(DaoException.class, () -> {
//      postDao.update(samples.get(0).getOfferingName(), null);
//    });
//  }
//
//  @Test
//  @DisplayName("delete works for valid input")
//  void deleteExistingPost() {
//    Post postDeleted = postDao.delete(samples.get(0).getOfferingName());
//    assertEquals(postDeleted, samples.get(0));
//    assertNull(postDao.read(postDeleted.getOfferingName()));
//  }
//
//  @Test
//  @DisplayName("delete returns null for non existing post")
//  void deleteThrowsExceptionNoMatchData() {
//    assertNull(postDao.delete("EN.000.999"));
//  }
//
//  @Test
//  @DisplayName("delete returns null for invalid input")
//  void deleteThrowsExceptionIncompleteData() {
//    assertNull(postDao.delete(null));
//  }
//
}
