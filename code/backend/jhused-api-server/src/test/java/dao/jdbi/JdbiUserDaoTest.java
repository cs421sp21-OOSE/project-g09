package dao.jdbi;

import dao.UserDao;
import dao.jdbiDao.JdbiUserDao;
import model.Category;
import model.Post;
import model.SaleState;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.database.DataStore;
import util.database.Database;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbiUserDaoTest {
  private static List<User> sampleUsers;
  private static List<Post> samplePosts;
  private UserDao userDao;
  private static Jdbi jdbi;

  @BeforeAll
  static void setSamples() {
    sampleUsers = DataStore.sampleUsers();
    samplePosts = DataStore.samplePosts();
  }

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
    Database.insertSampleData(jdbi, samplePosts);
    userDao = new JdbiUserDao(jdbi);
  }

  @AfterAll
  static void setUseProductionDatabase() {
    Database.USE_TEST_DATABASE = false; // use production dataset
  }

//  @Test
//  void doNothing() {}

//  @Test
//  void createNewUser() {
//    Post postNew = new Post("9".repeat(36), "008"+"1".repeat(33),
//        "2008 Toyota car", 7100D, SaleState.SOLD,
//        "It still works",
//        DataStore.sampleImages(Category.CAR),
//        DataStore.sampleHashtags(Category.CAR),
//        Category.CAR,
//        "Towson");
//    User userNew = new User("008"+"1".repeat(33),  "Ed", "abc8@yahoo.com",  "https://images6.fanpop.com/image/photos/33700000/Arya-Stark-arya-stark-33779443-1600-1200.jpg", "keyser Quad", new ArrayList<>(Arrays.asList(postNew)));
//    assertEquals(userNew, userDao.create(userNew));
//  }
//
//  @Test
//  void read() {
//    for (User user2: sampleUsers) {
//      assertEquals(user2, userDao.read(user2.getId()));
//    }
//  }

//  @Test
//  void updateAddPost() {
//    Post postNew = new Post("9".repeat(36), "",
//        "2008 Toyota car", 7100D, SaleState.SOLD,
//        "It still works",
//        DataStore.sampleImages(Category.CAR),
//        DataStore.sampleHashtags(Category.CAR),
//        Category.CAR,
//        "Towson");
//    String cersiId = "005111111111111111111111111111111111";
//    User userCersi = userDao.read(cersiId);
//    userCersi.addPosts(postNew);
//    assertEquals(userCersi, userDao.update(cersiId, userCersi));
//  }

  @Test
  void delete() {
    String cersiId = "005111111111111111111111111111111111";
    User cersi = userDao.read(cersiId);
    assertEquals(cersi , userDao.delete(cersiId));
    assertNull(userDao.read(cersiId));
  }
}