package api;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import model.Category;
import model.Post;
import model.SaleState;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.database.DataStore;
import util.database.Database;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

  private static final List<Post> samplePosts = DataStore.samplePosts();
  private final static String BASE_URL = "http://localhost:8080";
  private static final Gson gson = new Gson();
  private static Jdbi jdbi;

  @BeforeAll
  static void runApiServer() throws URISyntaxException {
    Database.USE_TEST_DATABASE = true; //use test dataset
    ApiServer.main(null); // run the server
    Database.main(null); // reset dataset and add samples
    jdbi = Database.getJdbi();
  }

  @BeforeEach
  void injectDependency() throws URISyntaxException {
    Database.USE_TEST_DATABASE = true; // make sure using test dataset
    Database.truncateTables(jdbi);
    Database.insertSampleData(jdbi, samplePosts);
  }

  @AfterAll
  static void stopApiServer() {
    ApiServer.stop();
    Database.USE_TEST_DATABASE = false; //use production dataset
  }

  @Test
  public void doNothing () throws UnirestException {
  }


  @Test
  public void getPostsWorks() throws UnirestException {
    final String URL = BASE_URL + "/api/posts";
    HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).asJson();
    assertEquals(200, jsonResponse.getStatus());
    assertNotEquals(0, jsonResponse.getBody().getArray().length());
  }

  @Test
  public void getPostsGivenPostUuid() throws UnirestException {
    final String UUID = "0".repeat(36);
    final String URL = BASE_URL + "/api/posts/" + UUID;
    HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).asJson();
    assertEquals(200, jsonResponse.getStatus());
    assertNotEquals(0, jsonResponse.getBody().getArray().length());
  }

  @Test
  public void getPostsGivenUuidNotInDatabase() throws UnirestException {
    final String UUID = "79388574";
    final String URL = BASE_URL + "/api/posts/" + UUID;
    HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).asJson();
    assertEquals(404, jsonResponse.getStatus());
  }

  @Test
  public void getPostsSorted() throws UnirestException {
    final String sortQuery = "?sort=price:asc";
    final String URL = BASE_URL + "/api/posts" + sortQuery;
    HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).asJson();
    assertEquals(200, jsonResponse.getStatus());
    assertNotEquals(0, jsonResponse.getBody().getArray().length());
  }

  @Test
  public void getPostsSortedMultiple() throws UnirestException {
    final String sortQuery = "?sort=price:asc,create_time:desc";
    final String URL = BASE_URL + "/api/posts" + sortQuery;
    HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).asJson();
    assertEquals(200, jsonResponse.getStatus());
    assertNotEquals(0, jsonResponse.getBody().getArray().length());
  }

  @Test
  public void getPostsSortedInvalidSortKey() throws UnirestException {
    final String sortQuery = "?sort=name:asc";
    final String URL = BASE_URL + "/api/posts" + sortQuery;
    HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).asJson();
    assertEquals(400, jsonResponse.getStatus());
  }

  @Test
  public void getPostSortedInvalidOrderKey() throws UnirestException {
    final String sortQuery = "?sort=price:ascend";
    final String URL = BASE_URL + "/api/posts" + sortQuery;
    HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).asJson();
    assertEquals(400, jsonResponse.getStatus());
  }

  @Test
  public void getPostSearchSorted() throws UnirestException {
    final String sortQuery = "?keyword=lamp&sort=price:asc";
    final String URL = BASE_URL + "/api/posts" + sortQuery;
    HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).asJson();
    assertEquals(200, jsonResponse.getStatus());
    assertNotEquals(0, jsonResponse.getBody().getArray().length());
  }

  @Test
  public void getPostCategoryWrong() throws UnirestException {
    final String sortQuery = "?category=coupon";
    final String URL = BASE_URL + "/api/posts" + sortQuery;
    HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).asJson();
    assertEquals(400, jsonResponse.getStatus());
  }

  @Test
  public void getPostCategorySearchSorted() throws UnirestException {
    final String sortQuery = "?category=furniture&keyword=coffee&sort=price:asc";
    final String URL = BASE_URL + "/api/posts" + sortQuery;
    HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).asJson();
    assertEquals(200, jsonResponse.getStatus());
    assertNotEquals(0, jsonResponse.getBody().getArray().length());
  }

  @Test
  public void postPostWorks() throws UnirestException {
    // This test will break if this post is already in database
    Post post = new Post(UUID.randomUUID().toString(), "001",
        "Dummy furniture", 30D, SaleState.SALE,
        "Description of dummy furniture",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Location of dummy furniture"
    );
    final String URL = BASE_URL + "/api/posts";
    HttpResponse<JsonNode> jsonResponse = Unirest.post(URL)
        .body(gson.toJson(post)).asJson();
    assertEquals(201, jsonResponse.getStatus());
    assertNotEquals(0, jsonResponse.getBody().getArray().length());
  }

  @Test
  public void postPostWithIncompleteData() throws UnirestException {
    Map<String, String> post = Map.of("title", "Made-up Post");
    final String URL = BASE_URL + "/api/posts";
    HttpResponse<JsonNode> jsonResponse = Unirest.post(URL)
        .body(gson.toJson(post)).asJson();
    assertEquals(500, jsonResponse.getStatus());
  }

  @Test
  public void postPostThatAlreadyExist() throws UnirestException {
    // This test will break if "0    " is not in the database
    Post post = new Post("0".repeat(36), "001",
        "Dummy furniture", 30D, SaleState.SALE,
        "Description of dummy furniture",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Location of dummy furniture"
    );
    final String URL = BASE_URL + "/api/posts";
    HttpResponse<JsonNode> jsonResponse = Unirest.post(URL)
        .body(gson.toJson(post)).asJson();
    assertEquals(500, jsonResponse.getStatus());
  }

  @Test
  public void putPostWorks() throws UnirestException {
    // This test will break if "0 " is not in the database
    Post post = new Post("0".repeat(36), "001",
        "Dummy furniture", 30D, SaleState.SALE,
        "Description of dummy furniture",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Location of dummy furniture"
    );
    final String URL = BASE_URL + "/api/posts/" + post.getId();
    HttpResponse<Post> jsonResponse = Unirest.put(URL)
        .body(gson.toJson(post)).asObject(Post.class);
    assertEquals(200, jsonResponse.getStatus());
    assertNotNull(jsonResponse.getBody());
    assertEquals(post.getTitle(), jsonResponse.getBody().getTitle());
  }

  @Test
  public void putPostNotInDataset() throws UnirestException {
    // This test will break if "0374  " is in the database
    Post post = new Post("0374".repeat(9), "001",
        "Dummy furniture", 30D, SaleState.SALE,
        "Description of dummy furniture",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Location of dummy furniture"
    );
    final String URL = BASE_URL + "/api/posts/" + post.getId();
    HttpResponse<JsonNode> jsonResponse = Unirest.put(URL)
        .body(gson.toJson(post)).asJson();
    assertEquals(404, jsonResponse.getStatus());
  }

  @Test
  public void putPostWithIncorrectUuid() throws UnirestException {
    Post post = new Post("7562".repeat(9), "001",
        "Dummy furniture", 30D, SaleState.SALE,
        "Description of dummy furniture",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Location of dummy furniture"
    );
    final String URL = BASE_URL + "/api/posts/" + "332";
    HttpResponse<JsonNode> jsonResponse = Unirest.put(URL)
        .body(gson.toJson(post)).asJson();
    assertEquals(400, jsonResponse.getStatus());
  }

  @Test
  public void putPostWithIncompleteData() throws UnirestException {
    Map<String, String> post = Map.of("title", "SSS DUMMY TITLE");
    final String URL = BASE_URL + "/api/posts/" + "0".repeat(36);
    HttpResponse<JsonNode> jsonResponse = Unirest.put(URL)
        .body(gson.toJson(post)).asJson();
    assertEquals(500, jsonResponse.getStatus());
  }

  @Test
  public void putPostAsArray() throws UnirestException {
    Post post = new Post("8572".repeat(9), "001",
        "Dummy furniture", 30D, SaleState.SALE,
        "Description of dummy furniture",
        DataStore.sampleImages(Category.FURNITURE),
        DataStore.sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Location of dummy furniture"
    );
    ArrayList<Post> posts = new ArrayList<Post>();
    posts.add(post);
    final String URL = BASE_URL + "/api/posts/" + posts.get(0).getId();
    HttpResponse<JsonNode> jsonResponse = Unirest.put(URL)
        .body(gson.toJson(posts)).asJson();
    assertEquals(500, jsonResponse.getStatus());
  }

  @Test
  public void deleteCourseWorks() throws UnirestException {
    // This test will break if "000..." does not exists in database
    final String UUID = "0".repeat(36);
    final String URL = BASE_URL + "/api/posts/" + UUID;
    HttpResponse<JsonNode> jsonResponse = Unirest.delete(URL).asJson();
    assertEquals(200, jsonResponse.getStatus());
    assertNotEquals(0, jsonResponse.getBody().getArray().length());
  }

  @Test
  public void deleteCourseNotInDatabase() throws UnirestException {
    // This test will break if "090984375" exists in database
    final String UUID = "090984375";
    final String URL = BASE_URL + "/api/posts/" + UUID;
    HttpResponse<JsonNode> jsonResponse = Unirest.delete(URL).asJson();
    assertEquals(404, jsonResponse.getStatus());
  }
}
