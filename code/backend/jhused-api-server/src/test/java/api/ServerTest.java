package api;

import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;
import model.*;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.database.DataStore;
import util.database.Database;
import util.paginationSkeleton.PostPaginationSkeleton;

import java.net.URISyntaxException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

  private static final List<Post> samplePosts = DataStore.samplePosts();
  private static final List<User> sampleUsers = DataStore.sampleUsers();
  private static final List<Message> sampleMessages = DataStore.sampleMessages();
  private static final List<Rate> sampleRates = DataStore.sampleRates();
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
    Database.insertSampleUsers(jdbi, sampleUsers);
    Database.insertSamplePosts(jdbi, samplePosts);
    Database.insertSampleMessages(jdbi, sampleMessages);
    Database.insertSampleRates(jdbi, sampleRates);
  }

  @AfterAll
  static void stopApiServer() {
    ApiServer.stop();
    Database.USE_TEST_DATABASE = false; //use production dataset
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
    Post post = new Post(UUID.randomUUID().toString(), "004" + "1".repeat(33),
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
    Post post = new Post("0".repeat(36), "003" + "1".repeat(33),
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

  @Test
  public void getMessagesWork() throws UnirestException {
    final String URL = BASE_URL + "/api/messages";
    HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).asJson();
    assertEquals(200, jsonResponse.getStatus());
    assertEquals(sampleMessages.size(), jsonResponse.getBody().getArray().length());
  }

  @Test
  public void getMessageGivenUserIdWorks() throws UnirestException {
    final String URL = BASE_URL + "/api/messages/{userId}";
    Map<String, List<Message>> userIds = new LinkedHashMap<>();
    for (Message message : sampleMessages) {
      if (userIds.get(message.getReceiverId()) == null) {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        userIds.put(message.getReceiverId(), messages);
      } else {
        userIds.get(message.getReceiverId()).add(message);
      }
      if (userIds.get(message.getSenderId()) == null) {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        userIds.put(message.getSenderId(), messages);
      } else if (!userIds.get(message.getSenderId()).contains(message)) {
        userIds.get(message.getSenderId()).add(message);
      }
    }
    for (Map.Entry<String, List<Message>> entry : userIds.entrySet()) {
      HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).routeParam("userId", entry.getKey()).asJson();
      assertEquals(200, jsonResponse.getStatus());
      for (int i = 0; i < entry.getValue().size(); ++i) {
        Message message = entry.getValue().get(i);
        assertEquals(message.getMessage(), jsonResponse.getBody().getArray().getJSONObject(i).get("message"));
        assertEquals(message.getRead(), jsonResponse.getBody().getArray().getJSONObject(i).get("read"));
        assertEquals(message.getId(), jsonResponse.getBody().getArray().getJSONObject(i).get("id"));
        assertEquals(message.getSenderId(), jsonResponse.getBody().getArray().getJSONObject(i).get("senderId"));
        assertEquals(message.getReceiverId(), jsonResponse.getBody().getArray().getJSONObject(i).get("receiverId"));
      }
    }
  }

  @Test
  public void postAMessagesWork() throws UnirestException {
    final String URL = BASE_URL + "/api/messages";
    Message message = new Message(null, "JHUsedAdmin", "002" + "1".repeat(33), "11 test message", false);
    HttpResponse<JsonNode> jsonResponse = Unirest.post(URL).body(gson.toJson(message)).asJson();
    assertEquals(201, jsonResponse.getStatus());
    assertEquals(message.getMessage(), jsonResponse.getBody().getObject().get("message"));
    Map<String, String> mapMessage = Map.of("senderId", "JHUsedAdmin", "receiverId", "004" + "1".repeat(33),
        "message", "jjj");
    jsonResponse = Unirest.post(URL).body(gson.toJson(mapMessage)).asJson();
    assertEquals(201, jsonResponse.getStatus());
    assertEquals(mapMessage.get("message"), jsonResponse.getBody().getObject().get("message"));
  }

  @Test
  public void postAListOfMessagesWork() throws UnirestException {
    final String URL = BASE_URL + "/api/messages";
    Database.truncateTable(jdbi, "message");
    HttpResponse<JsonNode> jsonResponse =
        Unirest.post(URL).queryString("isList", true).body(gson.toJson(sampleMessages)).asJson();
    assertEquals(201, jsonResponse.getStatus());
    for (int i = 0; i < sampleMessages.size(); ++i) {
      JSONObject res = jsonResponse.getBody().getArray().getJSONObject(i);
      assertEquals(sampleMessages.get(i).getMessage(), res.get("message"));
      assertEquals(sampleMessages.get(i).getId(), res.get("id"));
      assertEquals(sampleMessages.get(i).getSenderId(), res.get("senderId"));
      assertEquals(sampleMessages.get(i).getReceiverId(), res.get("receiverId"));
      assertEquals(sampleMessages.get(i).getRead(), res.get("read"));
    }
    List<Message> messages = new ArrayList<>();
    messages.add(new Message(null, "JHUsedAdmin", "002" + "1".repeat(33), "11 test message", false));
    messages.add(new Message(null, "003" + "1".repeat(33), "002" + "1".repeat(33), "11 test ssmessage", false));
    jsonResponse = Unirest.post(URL).queryString("isList", true).body(gson.toJson(messages)).asJson();
    assertEquals(201, jsonResponse.getStatus());
    for (int i = 0; i < messages.size(); ++i) {
      JSONObject res = jsonResponse.getBody().getArray().getJSONObject(i);
      assertEquals(messages.get(i).getMessage(), res.get("message"));
      assertEquals(messages.get(i).getSenderId(), res.get("senderId"));
      assertEquals(messages.get(i).getReceiverId(), res.get("receiverId"));
      assertEquals(messages.get(i).getRead(), res.get("read"));
    }
  }

  @Test
  void postAInvalidMessageReturn500() {
    final String URL = BASE_URL + "/api/messages";
    Message message = new Message(null, "fff", "002" + "1".repeat(33), "11 test message", false);
    HttpResponse<JsonNode> jsonResponse = Unirest.post(URL).body(gson.toJson(message)).asJson();
    assertEquals(500, jsonResponse.getStatus());
    message = new Message(null, "JHUsedAdmin", "112" + "1".repeat(33), "11 test message", false);
    jsonResponse = Unirest.post(URL).body(gson.toJson(message)).asJson();
    assertEquals(500, jsonResponse.getStatus());
    message = new Message(null, "JHUsedAdmin", "002" + "1".repeat(33), null, false);
    jsonResponse = Unirest.post(URL).body(gson.toJson(message)).asJson();
    assertEquals(500, jsonResponse.getStatus());
  }

  @Test
  void putAMessageWorks() {
    final String URL = BASE_URL + "/api/messages/{messageId}";
    Message message = new Message(sampleMessages.get(0).getId(), null, null, "updated 11 "
        + "test message", true);
    HttpResponse<JsonNode> jsonResponse =
        Unirest.put(URL).routeParam("messageId", message.getId()).body(gson.toJson(message)).asJson();
    assertEquals(200, jsonResponse.getStatus());
    assertEquals(message.getMessage(), jsonResponse.getBody().getObject().get("message"));
    assertEquals(message.getRead(), jsonResponse.getBody().getObject().get("read"));
  }

  @Test
  void putAnInvalidMessageWorks() {
    final String URL = BASE_URL + "/api/messages/{messageId}";
    Message message = new Message(null, null, null, "updated 11 "
        + "test message", true);
    HttpResponse<JsonNode> jsonResponse =
        Unirest.put(URL).routeParam("messageId", sampleMessages.get(0).getId()).body(gson.toJson(message)).asJson();
    assertEquals(400, jsonResponse.getStatus());
    message.setId("jjj");
    jsonResponse =
        Unirest.put(URL).routeParam("messageId", sampleMessages.get(0).getId()).body(gson.toJson(message)).asJson();
    assertEquals(400, jsonResponse.getStatus());
  }

  @Test
  void deleteAMessageWorks() {
    final String URL = BASE_URL + "/api/messages/{messageId}";
    HttpResponse<JsonNode> jsonResponse =
        Unirest.delete(URL).routeParam("messageId", sampleMessages.get(0).getId()).asJson();
    assertEquals(200, jsonResponse.getStatus());
    Message message = sampleMessages.get(0);
    assertEquals(message.getMessage(), jsonResponse.getBody().getObject().get("message"));
    assertEquals(message.getSenderId(), jsonResponse.getBody().getObject().get("senderId"));
    assertEquals(message.getReceiverId(), jsonResponse.getBody().getObject().get("receiverId"));
    assertEquals(message.getRead(), jsonResponse.getBody().getObject().get("read"));
  }

  @Test
  void deleteNonExistingMessageThrow404() {
    final String URL = BASE_URL + "/api/messages/{messageId}";
    HttpResponse<JsonNode> jsonResponse =
        Unirest.delete(URL).routeParam("messageId", "jlaksdjflais").asJson();
    assertEquals(404, jsonResponse.getStatus());
  }

  @Test
  void deleteAListOfMessagesWorks() {
    final String URL = BASE_URL + "/api/messages";
    List<String> ids = new ArrayList<>();
    for (Message message : sampleMessages) {
      ids.add(message.getId());
    }
    HttpResponse<JsonNode> jsonResponse =
        Unirest.delete(URL).body(gson.toJson(ids)).asJson();
    assertEquals(200, jsonResponse.getStatus());
    List<Message> messages = sampleMessages;
    for (int i = 0; i < messages.size(); ++i) {
      JSONObject res = jsonResponse.getBody().getArray().getJSONObject(i);
      assertEquals(messages.get(i).getMessage(), res.get("message"));
      assertEquals(messages.get(i).getSenderId(), res.get("senderId"));
      assertEquals(messages.get(i).getReceiverId(), res.get("receiverId"));
      assertEquals(messages.get(i).getRead(), res.get("read"));
    }
  }

  @Test
  void deleteAListOfMessagesWithInvalidIdsReturn400() {
    final String URL = BASE_URL + "/api/messages";
    List<String> ids = new ArrayList<>();
    for (Message message : sampleMessages) {
      ids.add(message.getId());
    }
    ids.set(0, "jjhhs");
    HttpResponse<JsonNode> jsonResponse =
        Unirest.delete(URL).body(gson.toJson(ids)).asJson();
    assertEquals(400, jsonResponse.getStatus());
  }

  @Test
  void getSellerAvgRateWorks() {
    Map<String, Double> avg = new LinkedHashMap<>();
    Map<String, Integer> cnt = new LinkedHashMap<>();
    for (Rate rate : sampleRates) {
      Double current = avg.get(rate.getSellerId());
      if (current == null) {
        current = (double) rate.getRate();
        avg.put(rate.getSellerId(), current);
        cnt.put(rate.getSellerId(), 1);
      } else {
        int n = cnt.get(rate.getSellerId());
        current = (current * n + rate.getRate()) / (n + 1);
        avg.put(rate.getSellerId(), current);
        cnt.put(rate.getSellerId(), n + 1);
      }
    }
    avg.forEach((key, value) -> {
      final String URL = BASE_URL + "/api/rates/avg/" + key;
      HttpResponse<JsonNode> jsonResponse =
          Unirest.get(URL).asJson();
      assertEquals(jsonResponse.getStatus(), 200);
      assertEquals(((double) Math.round(value * 100)) / 100, jsonResponse.getBody().getObject().getDouble(
          "averageRate"));
    });
  }

  @Test
  void getNonExistingSellerRateAvgReturn404() {
    final String URL = BASE_URL + "/api/rates/avg/" + "lsjfilejlifsj";
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(URL).asJson();
    assertEquals(jsonResponse.getStatus(), 404);
  }

  @Test
  void getARateWorks() {
    final String URL = BASE_URL + "/api/rates/{sellerId}/{raterId}";
    for (Rate rate : sampleRates) {
      HttpResponse<JsonNode> jsonResponse =
          Unirest.get(URL)
              .routeParam("sellerId", rate.getSellerId())
              .routeParam("raterId", rate.getRaterId())
              .asJson();
      assertEquals(jsonResponse.getStatus(), 200);
      assertEquals(rate, gson.fromJson(jsonResponse.getBody().getObject().toString(), Rate.class));
    }
  }

  @Test
  void getARateReturn404NoneExistingRate() {
    final String URL = BASE_URL + "/api/rates/{sellerId}/{raterId}";
    HttpResponse<JsonNode> jsonResponse =
        Unirest.get(URL)
            .routeParam("sellerId", "lijljijijiijj")
            .routeParam("raterId", sampleRates.get(0).getRaterId())
            .asJson();
    assertEquals(jsonResponse.getStatus(), 404);
  }

  @Test
  void postARateWorks() {
    final String URL = BASE_URL + "/api/rates";
    Rate rate = new Rate(sampleUsers.get(0).getId(), sampleUsers.get(1).getId(), 3);
    HttpResponse<JsonNode> jsonResponse = Unirest.post(URL)
        .body(gson.toJson(rate)).asJson();
    assertEquals(201, jsonResponse.getStatus());
    assertEquals(rate, gson.fromJson(jsonResponse.getBody().getObject().toString(), Rate.class));
  }

  @Test
  void postDuplicateRateReturn500() {
    final String URL = BASE_URL + "/api/rates";
    Rate rate = sampleRates.get(0);
    HttpResponse<JsonNode> jsonResponse = Unirest.post(URL)
        .body(gson.toJson(rate)).asJson();
    assertEquals(500, jsonResponse.getStatus());
  }

  @Test
  void postInvalidRateReturn500() {
    final String URL = BASE_URL + "/api/rates";
    Rate rate = new Rate(sampleRates.get(0).getRaterId(), sampleRates.get(0).getSellerId(),
        sampleRates.get(0).getRate());
    rate.setRate(-1);
    HttpResponse<JsonNode> jsonResponse = Unirest.post(URL)
        .body(gson.toJson(rate)).asJson();
    assertEquals(500, jsonResponse.getStatus());
    rate = new Rate(sampleRates.get(0).getRaterId(), sampleRates.get(0).getSellerId(), sampleRates.get(0).getRate());
    rate.setSellerId(null);
    jsonResponse = Unirest.post(URL)
        .body(gson.toJson(rate)).asJson();
    assertEquals(500, jsonResponse.getStatus());
    jsonResponse = Unirest.post(URL)
        .body(gson.toJson(Map.of("sellerId", "lsiejfiesjf"))).asJson();
    assertEquals(500, jsonResponse.getStatus());
  }

  @Test
  void putRateWorks() {
    final String URL = BASE_URL + "/api/rates/{sellerId}/{raterId}";
    for (Rate rate : sampleRates) {
      rate = new Rate(rate.getRaterId(), rate.getSellerId(), rate.getRate());
      rate.setRate((rate.getRate() + 1) % 6);
      HttpResponse<JsonNode> jsonResponse = Unirest.put(URL)
          .routeParam("raterId", rate.getRaterId())
          .routeParam("sellerId", rate.getSellerId())
          .body(gson.toJson(rate)).asJson();
      assertEquals(rate, gson.fromJson(jsonResponse.getBody().getObject().toString(), Rate.class));
    }
  }

  @Test
  void deleteRateWorks() {
    final String URL = BASE_URL + "/api/rates/{sellerId}/{raterId}";
    for (Rate rate : sampleRates) {
      HttpResponse<JsonNode> jsonResponse = Unirest.delete(URL)
          .routeParam("raterId", rate.getRaterId())
          .routeParam("sellerId", rate.getSellerId())
          .asJson();
      assertEquals(rate, gson.fromJson(jsonResponse.getBody().getObject().toString(), Rate.class));
    }
  }

  @Test
  void getPostPaginationWorks() {
    final String URL = BASE_URL + "/api/v2/posts";
    int totalRow = samplePosts.size();
    int limit = 3;
    int page = 2;
    int totalPage = totalRow / limit+1;
    PostPaginationSkeleton postPaginationSkeleton = new PostPaginationSkeleton();
    postPaginationSkeleton.getPagination().put("page", 1);
    postPaginationSkeleton.getPagination().put("limit", 50);
    postPaginationSkeleton.getPagination().put("last", 1);
    postPaginationSkeleton.getPagination().put("total", totalRow);
    HttpResponse<JsonNode> jsonResponse = Unirest.get(URL).asJson();
    assertEquals(200, jsonResponse.getStatus());
    assertEquals(postPaginationSkeleton.getPagination(), gson.fromJson(jsonResponse.getBody().getObject().toString(),
        PostPaginationSkeleton.class).getPagination());
    assertNotEquals(0, jsonResponse.getBody().getArray().length());
    final String sortQuery = "?keyword=lamp&sort=price:asc";
    jsonResponse = Unirest.get(URL + sortQuery).asJson();
    assertEquals(200, jsonResponse.getStatus());
    assertEquals(postPaginationSkeleton.getPagination(), gson.fromJson(jsonResponse.getBody().getObject().toString(),
        PostPaginationSkeleton.class).getPagination());
    assertNotEquals(0, jsonResponse.getBody().getArray().length());
    postPaginationSkeleton.getPagination().put("page", page);
    postPaginationSkeleton.getPagination().put("limit", limit);
    postPaginationSkeleton.getPagination().put("last", totalPage);
    postPaginationSkeleton.getPagination().put("total", totalRow);
    jsonResponse = Unirest.get(URL + sortQuery + "&page=" + page + "&limit=" + limit).asJson();
    assertEquals(200, jsonResponse.getStatus());
    assertEquals(postPaginationSkeleton.getPagination(), gson.fromJson(jsonResponse.getBody().getObject().toString(),
        PostPaginationSkeleton.class).getPagination());
    assertNotEquals(0, jsonResponse.getBody().getArray().length());
  }
}
