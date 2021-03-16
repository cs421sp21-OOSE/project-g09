package dao;

import dao.sql2oDao.Sql2oHashtagDao;
import exceptions.DaoException;
import model.Hashtag;
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

public class Sql2oHashtagDaoTest {
  private static final List<Post> samplePosts = DataStore.samplePosts();
  private static Sql2o sql2o;
  private HashtagDao hashtagDao;

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
    hashtagDao = new Sql2oHashtagDao(Database.getSql2o());
  }

  @AfterAll
  static void setUseProductionDatabase() {
    Database.USE_TEST_DATABASE = false; // use production dataset
  }

  @Test
  void doNothing() {
  }

  @Test
  void createNewHashtag() {
    Hashtag newHashtag = new Hashtag(UUID.randomUUID().toString(), "test");
    assertEquals(newHashtag, hashtagDao.create(newHashtag));
  }

  @Test
  void createDuplicateHashtagIdDaoException() {
    Hashtag newHashtag = new Hashtag(samplePosts.get(0).getHashtags().get(0).getId(), "test");
    assertThrows(DaoException.class, () -> {
      hashtagDao.create(newHashtag);
    });
  }

  @Test
  void createDuplicateHashtagContentDaoException() {
    Hashtag newHashtag = new Hashtag(UUID.randomUUID().toString(),
        samplePosts.get(0).getHashtags().get(0).getHashtag());
    assertThrows(DaoException.class, () -> {
      hashtagDao.create(newHashtag);
    });
  }

  @Test
  void createNullIdThrowsDaoException() {
    Hashtag newHashtag = new Hashtag(null, "test");
    assertThrows(DaoException.class, () -> {
      hashtagDao.create(newHashtag);
    });
  }

  @Test
  void createNullContentThrowsDaoException() {
    Hashtag newHashtag = new Hashtag(UUID.randomUUID().toString(), null);
    assertThrows(DaoException.class, () -> {
      hashtagDao.create(newHashtag);
    });
  }

  @Test
  void createNullHashtagThrowsDaoException() {
    assertThrows(DaoException.class, () -> {
      hashtagDao.create(null);
    });
  }

  @Test
  void readHashtagGivenId() {
    assertEquals(samplePosts.get(0).getHashtags().get(0),
        hashtagDao.read(samplePosts.get(0).getHashtags().get(0).getId()));
  }

  @Test
  void readHashtagReturnNullGivenNullId() {
    assertNull(hashtagDao.read(null));
  }

  @Test
  void readNonExistingHashtagReturnNull() {
    assertNull(hashtagDao.read("744789".repeat(6)));
  }

  @Test
  void readAllHashtag() {
    assertNotEquals(0, hashtagDao.readAll().size());
  }

  @Test
  void readAllHashtagGivenQuery() {
    assertNotEquals(0, hashtagDao.readAll(samplePosts.get(0).getHashtags().get(0).getHashtag()).size());
    assertEquals(1, hashtagDao.readAll(samplePosts.get(0).getHashtags().get(0).getHashtag()).size());
  }

  @Test
  void readAllQueryCaseInsensitive() {
    // query should be case insensitive
    String query = samplePosts.get(0).getHashtags().get(0).getHashtag();
    List<Hashtag> results = hashtagDao.readAll(query.toUpperCase());
    assertNotEquals(0, results.size());
    for (Hashtag hashtag : results) {
      assertTrue(hashtag.getHashtag().toUpperCase().contains(query.toUpperCase()));
    }
  }

  @Test
  void readAllQueryLike() {
    // support pattern matching
    String query = samplePosts.get(0).getHashtags().get(0).getHashtag();
    assertTrue(query.length() > 3);
    query = query.substring(1, 2);
    List<Hashtag> results = hashtagDao.readAll("%" + query + "%");
    assertNotEquals(0, results.size());
    for (Hashtag hashtag : results) {
      assertTrue(hashtag.getHashtag().toUpperCase().contains(query.toUpperCase()));
    }
  }

  @Test
  void updateHashtagWorks() {
    Post post = samplePosts.get(0);
    Hashtag newHashtag = new Hashtag(post.getHashtags().get(0).getId(), "test");
    Hashtag updatedHashtag = hashtagDao.update(newHashtag.getId(), newHashtag);
    assertEquals(updatedHashtag, newHashtag);
  }

  @Test
  void updateHashtagThrowsExceptionInvalidHashtag() {
    Post post = samplePosts.get(0);
    assertThrows(DaoException.class, () -> {
      hashtagDao.update(post.getHashtags().get(0).getId(), null);
    });
  }

  @Test
  void updateHashtagReturnNullInvalidID() {
    Post post = samplePosts.get(0);
    Hashtag newHashtag = new Hashtag(post.getHashtags().get(0).getId(), "test");
    assertNull(hashtagDao.update("756499".repeat(6), newHashtag));
  }

  @Test
  void getHashtagsGivenPostIdWork() {
    for (Post post: samplePosts)
    {
      assertEquals(post.getHashtags(), hashtagDao.getHashtagsOfPost(post.getId()));
    }
  }

  @Test
  void getHashtagsGivenInvalidPostIdReturnEmpty() {
    assertEquals(0,hashtagDao.getHashtagsOfPost("999771".repeat(6)).size());
  }

  @Test
  void getHashtagsGivenNullPostIdReturnEmpty() {
    assertEquals(0,hashtagDao.getHashtagsOfPost(null).size());
  }
}
