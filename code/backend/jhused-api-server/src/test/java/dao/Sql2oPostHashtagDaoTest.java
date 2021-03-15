package dao;

import dao.sql2oDao.Sql2oPostHashtagDao;
import exceptions.DaoException;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Sql2oPostHashtagDaoTest {
  private static final List<Post> samplePosts = DataStore.samplePosts();
  private static Sql2o sql2o;
  private PostHashtagDao posthashtagDao;

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
    posthashtagDao = new Sql2oPostHashtagDao(Database.getSql2o());
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
    assertEquals(postHashtag, posthashtagDao.create(postHashtag.get("postId"), postHashtag.get("hashtagId")));
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
      posthashtagDao.create(postHashtag.get("postId"), null);
    });
  }
}
