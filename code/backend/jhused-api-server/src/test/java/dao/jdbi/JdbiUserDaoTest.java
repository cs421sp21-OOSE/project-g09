package dao.jdbi;

import dao.UserDao;
import dao.jdbiDao.JdbiUserDao;
import exceptions.DaoException;
import model.Category;
import model.Post;
import model.SaleState;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import util.database.DataStore;
import util.database.Database;

import java.net.URISyntaxException;
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
    Database.insertSamplePosts(jdbi, samplePosts);
    userDao = new JdbiUserDao(jdbi);
  }

  @AfterAll
  static void setUseProductionDatabase() {
    Database.USE_TEST_DATABASE = false; // use production dataset
  }

  @Test
  void doNothing() {}

  @Test
  void createNewUser() {
    User userNew = DataStore.getNewUserForTest();
    assertEquals(userNew, userDao.create(userNew));
  }

  @Test
  void createNewUserDuplicateException() {
    User user1 = sampleUsers.get(0);
    assertThrows(DaoException.class, ()-> userDao.create(user1));
  }

  @Test
  void createNewUserIncompleteData() {
    User noName = DataStore.getNewUserForTest();
    noName.setName(null);
    assertThrows(DaoException.class, ()-> userDao.create(noName));
    User noId = DataStore.getNewUserForTest();
    noId.setId(null);
    assertThrows(DaoException.class, ()-> userDao.create(noId));
    User noEmail = DataStore.getNewUserForTest();
    noEmail.setEmail(null);
    assertThrows(DaoException.class, ()-> userDao.create(noId));
  }



  @Test
  void read() {
    for (User user2: sampleUsers) {
      assertEquals(user2, userDao.read(user2.getId()));
    }
  }


  @Test
  void readInvalid() {
    assertNull(userDao.read("0"));
  }

//  @Test
//  void readAll() {
//    assertArrayEquals(sampleUsers, userDao.readAll());
//  }

  @Test
  void updateAddPost() {
    Post postNew = new Post("9".repeat(36), "",
        "2008 Toyota car", 7100D, SaleState.SOLD,
        "It still works",
        DataStore.sampleImages(Category.CAR),
        DataStore.sampleHashtags(Category.CAR),
        Category.CAR,
        "Towson");
    String cersiId = "005111111111111111111111111111111111";
    User userCersi = userDao.read(cersiId);
    userCersi.addPosts(postNew);
    assertEquals(userCersi, userDao.update(cersiId, userCersi));
  }

  @Test
  void updateDeletePost() {
    String cersiId = "005111111111111111111111111111111111";
    User userCersi = userDao.read(cersiId);
    List<Post> postList = userCersi.getPosts();
    postList.remove(0);
    assertEquals(userCersi, userDao.update(cersiId, userCersi));
  }


  @Test
  void updateEmailProfileNameLocation() {
    User user = sampleUsers.get(0);
    user.setEmail("testInvalid");
    user.setProfileImage("cat.img");
    user.setName("noman");
    user.setLocation("seatac");
    User ret = userDao.update(user.getId(), user);
    assertEquals(user, ret);
  }

  @Test
  void delete() {
    String cersiId = "005111111111111111111111111111111111";
    User cersi = userDao.read(cersiId);
    assertEquals(cersi , userDao.delete(cersiId));
    assertNull(userDao.read(cersiId));
  }

  @Test
  @DisplayName("delete returns null for non existing user")
  void deleteThrowsExceptionNoMatchData() {
    assertNull(userDao.delete("25"));
  }

  @Test
  @DisplayName("delete returns null for invalid input")
  void deleteThrowsExceptionIncompleteData() {
    assertNull(userDao.delete(null));
  }

}