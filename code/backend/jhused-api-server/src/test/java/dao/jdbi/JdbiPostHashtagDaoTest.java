package dao.jdbi;

import dao.PostHashtagDao;
import dao.jdbiDao.JdbiPostHashtagDao;
import exceptions.DaoException;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JdbiPostHashtagDaoTest {
  private static final List<Post> samplePosts = DataStore.samplePosts();
  private static final List<User> sampleUsers = DataStore.sampleUsers();
  private static Jdbi jdbi;
  private PostHashtagDao posthashtagDao;

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
    posthashtagDao = new JdbiPostHashtagDao(jdbi);
  }

  @AfterAll
  static void setUseProductionDatabase() {
    Database.USE_TEST_DATABASE = false; // use production dataset
  }

  @Test
  void doNothing() {
  }

  @Test
  void createPostHashtag() {
    Map<String, String> postHashtag = Map.of("postId", "0".repeat(36), "hashtagId", "1".repeat(36));
    Map<String, String> resultPostHashtag = posthashtagDao.create(postHashtag.get("postId"), postHashtag.get("hashtagId"));
    assertEquals(postHashtag.get("postId"), resultPostHashtag.get("postid"));
    assertEquals(postHashtag.get("hashtagId"), resultPostHashtag.get("hashtagid"));
  }

  @Test
  void createPostHashtagNonExistingPostThrowsException() {
    Map<String, String> postHashtag = Map.of("postId", "364991".repeat(6), "hashtagId", "1".repeat(36));
    assertThrows(DaoException.class, () -> {
      posthashtagDao.create(postHashtag.get("postId"), postHashtag.get("hashtagId"));
    });
  }

  @Test
  void createPostHashtagNonExistingHashtagThrowsException() {
    Map<String, String> postHashtag = Map.of("postId", "0".repeat(36), "hashtagId", "3649941".repeat(6));
    assertThrows(DaoException.class, () -> {
      posthashtagDao.create(postHashtag.get("postId"), postHashtag.get("hashtagId"));
    });
  }

  @Test
  void createPostHashtagNullPostThrowsException() {
    Map<String, String> postHashtag = Map.of("hashtagId", "1".repeat(36));
    assertThrows(DaoException.class, () -> {
      posthashtagDao.create(null, postHashtag.get("hashtagId"));
    });
  }

  @Test
  void createPostHashtagNullHashtagThrowsException() {
    Map<String, String> postHashtag = Map.of("postId", "0".repeat(36));
    assertThrows(DaoException.class, () -> {
      posthashtagDao.create(postHashtag.get("postId"), (String) null);
    });
  }
}
