package dao.jdbi;

import dao.PostDao;
import dao.jdbiDao.JdbiPostDao;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JdbiPostDaoTest {
  private static List<Post> samples;
  private static List<User> sampleUsers;
  private PostDao postDao;
  private static Jdbi jdbi;

  @BeforeAll
  static void setSamplePosts() {
    samples = DataStore.samplePosts();
    sampleUsers = DataStore.sampleUsers();
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
    Database.insertSamplePosts(jdbi, samples);
    postDao = new JdbiPostDao(jdbi);
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
    Post c1 = new Post(UUID.randomUUID().toString(), "001"+"1".repeat(33),
        "Dummy furniture", 30D, SaleState.SALE,
        "Description of dummy furniture",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Location of dummy furniture"
    );
    Post c2 = postDao.create(c1);
    c1.setCreateTime(c2.getCreateTime());
    c1.setUpdateTime(c2.getUpdateTime());
    assertEquals(c1, c2);
  }

  @Test
  @DisplayName("create throws exception for duplicate post")
  void createThrowsExceptionDuplicateData() {
    Post c1 = new Post("0".repeat(36), "001"+"1".repeat(33),
        "Dummy furniture", 30D, SaleState.SALE,
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
    Post c1 = new Post(null, "001"+"1".repeat(33),
        "Dummy furniture", 30D, SaleState.SALE,
        "Description of dummy furniture",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Location of dummy furniture"
    );

    c1.setId("0" + "1".repeat(35));
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
  // Change query "dummy" to "Table" because sample data are updated
  void readAllGivenTitle() {
    String query = "Table";
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
  void readAllSorted() {
    double THRESHOLD = 0.0001;

    Map<String, String> sortParams = new LinkedHashMap<>();
    sortParams.put("price", "desc");
    List<Post> posts = postDao.readAllAdvanced(null,null, sortParams);
    assertNotEquals(0, posts.size());
    assertEquals(true, Math.abs(posts.get(0).getPrice() - 20000D) < THRESHOLD);
  }

  @Test
  void readAllSortedMultiple() {
    Map<String, String> sortParams = new LinkedHashMap<>();
    sortParams.put("price", "asc");
    sortParams.put("update_time", "desc");
    List<Post> posts = postDao.readAllAdvanced(null,null, sortParams);

    assertEquals("Coffee cup", posts.get(0).getTitle());
  }

  @Test
  void readAllSearch() {
    String query = "minimalist";
    List<Post> posts = postDao.readAllAdvanced(null, query, null);
    assertEquals(1, posts.size());
  }

  @Test
  void readAllSearchNoMatch() {
    String query = "milan";
    List<Post> posts = postDao.readAllAdvanced(null, query, null);
    assertEquals(0, posts.size());
  }

  @Test
  // This test will break because this search cannot handle whole word search
  // The search will return a post with location Carlyle which has car keyword
  // Need to discuss if we need to do whole word search or get more order
  void readAllSearchAndSort() {
    String query = "coffee";
    Map<String, String> sortParams = new LinkedHashMap<>();
    sortParams.put("price", "asc");
    List<Post> posts = postDao.readAllAdvanced(null, query, sortParams);
    assertEquals("Coffee cup", posts.get(0).getTitle());
  }

  @Test
  void readAllWithCategory() {
    String category= "car";
    List<Post> posts = postDao.readAllAdvanced(category, null, null);
    assertNotEquals(0, posts.size());
    assertEquals("Dream car to sell", posts.get(0).getTitle());
  }

  @Test
  void readAllWithCategoryAndSort() {
    String category= "car";
    Map<String, String> sortParams = new LinkedHashMap<>();
    sortParams.put("price", "asc");
    List<Post> posts = postDao.readAllAdvanced(category, null, sortParams);
    assertNotEquals(0, posts.size());
    assertEquals("1998 Toyota car", posts.get(0).getTitle());
  }

  @Test
  void readAllWithCategoryAndKeyword() {
    String category= "furniture";
    String keyword = "lamp";
    List<Post> posts = postDao.readAllAdvanced(category, keyword, null);
    assertEquals(1, posts.size());
    assertEquals("Minimalist lamp", posts.get(0).getTitle());
  }

  @Test
  void readAllWithCategoryAndKeywordAndSort() {
    String category= "furniture";
    String keyword = "coffee";
    Map<String, String> sortParams = new LinkedHashMap<>();
    sortParams.put("price", "asc");
    List<Post> posts = postDao.readAllAdvanced(category, keyword, sortParams);
    assertEquals(2, posts.size());
    assertEquals("Coffee cup", posts.get(0).getTitle());

  }

  @Test
  @DisplayName("updating a post works")
  void updateWorks() {
    //create a post to send to the update method.
    Post ogPost = new Post(samples.get(0).getId(), "001"+"1".repeat(33),
        "Dummy furnitulre", 31.3, SaleState.SALE,
        "**Description ofa dummy furnitusdfsre",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.CAR,
        "Location ossf dummy furniture"
    );

    //get the post back, give the first item in samples uuid.
    Post post = postDao.update(samples.get(0).getId(), ogPost);
    ogPost.setCreateTime(post.getCreateTime());
    ogPost.setUpdateTime(post.getUpdateTime());
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
    Post postDeleted = postDao.delete(samples.get(0).getId());
    assertEquals(postDeleted, samples.get(0));
    assertNull(postDao.read(postDeleted.getId()));
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

  @Test
  @DisplayName("Get posts with somewhat matching titles, descriptions and " +
          "locations")
  void searchAllPosts() { //string below can be changed to anything.
    // TODO did not implement this
  }

  @Test
  @DisplayName("Search null returns no posts.")
  void searchAllPostsNull() { //string below can be changed to anything.
    assertNull(postDao.searchAll(null));
  }

  @Test
  @DisplayName("Search empty string returns all posts")
  void searchAllPostsEmptyString() { //string below can be changed to anything.
    // TODO did not implement this
  }

  @Test
  @DisplayName("Get posts with somewhat matching titles, descriptions and " +
          "locations from the specified category")
  void searchCategory() { //string and category below can be changed to anything.
    // TODO did not implement this
  }

  @Test
  @DisplayName("Get posts with somewhat matching titles, descriptions and " +
          "locations from the specified category")
  void searchCategoryNull() { //string below can be changed to anything.
    // TODO did not implement this

  }



  @Test
  @DisplayName("returns posts with specified category")
  void getPostsFromCategory() {
    List<Post> posts = postDao.getCategory(Category.DESK);

    for(Post thisPost: posts) {
      assertEquals(thisPost.getCategory(), Category.DESK);
    }

  }

  @Test
  @DisplayName("null category returns empty post list.")
  void getPostsFromCategoryNull() {
    assertTrue(postDao.getCategory(null).isEmpty());
  }


}
