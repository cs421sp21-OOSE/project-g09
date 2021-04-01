package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dao.PostDao;
import dao.UserDao;
import dao.jdbiDao.JdbiPostDao;
import dao.jdbiDao.JdbiUserDao;
import exceptions.ApiError;
import exceptions.DaoException;
import model.Post;
import model.User;
import spark.Spark;
import util.database.Database;

import java.net.URISyntaxException;
import java.util.*;

import static spark.Spark.*;

public class ApiServer {
  // Admissible query parameters for sorting
  private static final Set<String> COLUMN_KEYS = Set.of("title", "price",
          "create_time", "update_time", "location");
  // Admissible sort types
  private static final Set<String> ORDER_KEYS = Set.of("asc", "desc");
  private static final Set<String> CATEGORY_KEYS = Set.of("furniture", "desk", "car", "tv");

  private static int getHerokuAssignedPort() {
    // Heroku stores port number as an environment variable
    String herokuPort = System.getenv("PORT");
    if (herokuPort != null) {
      return Integer.parseInt(herokuPort);
    }
    //return default port if heroku-port isn't set (i.e. on localhost)
    return 8080;
  }

  private static PostDao getPostDao() throws URISyntaxException {
    return new JdbiPostDao(Database.getJdbi());
  }

  private static UserDao getUserDao() throws URISyntaxException {
    return new JdbiUserDao(Database.getJdbi());
  }

  /**
   * set access control request headers
   */
  public static void setAccessControlRequestHeaders(){
    options("/*",
        (request, response) -> {

          String accessControlRequestHeaders = request
              .headers("Access-Control-Request-Headers");
          if (accessControlRequestHeaders != null) {
            response.header("Access-Control-Allow-Headers",
                accessControlRequestHeaders);
          }

          String accessControlRequestMethod = request
              .headers("Access-Control-Request-Method");
          if (accessControlRequestMethod != null) {
            response.header("Access-Control-Allow-Methods",
                accessControlRequestMethod);
          }

          return "OK";
        });

    before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
  }
  /**
   * Stop the server.
   */
  public static void stop() {
    Spark.stop();
  }

  public static void main(String[] args) throws URISyntaxException {
    port(getHerokuAssignedPort());
    setAccessControlRequestHeaders();
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    PostDao postDao = getPostDao();
    UserDao userDao = getUserDao();
    exception(ApiError.class, (ex, req, res) -> {
      // Handle the exception here
      Map<String, String> map = Map.of("status", ex.getStatus() + "",
          "error", ex.getMessage());
      res.body(gson.toJson(map));
      res.status(ex.getStatus());
      res.type("application/json");
    });

    get("/", (req, res) -> {
      Map<String, String> message = Map.of("message", "Hello World");
      res.type("application/json");
      return gson.toJson(message);
    });

    // Read all posts matching the query parameters if they exist
    // Handle category match, keyword search, and sort
    get("/api/posts", (req, res) -> {
      try {
        String categoryString = req.queryParams("category");
        if (categoryString != null && !CATEGORY_KEYS.contains(categoryString.toLowerCase())) {
          throw new ApiError("Invalid category parameter", 400);
        }

        String keyword = req.queryParams("keyword"); // use keyword for search
        String sort = req.queryParams("sort");
        System.out.println(sort);
        Map<String, String> sortParams = new LinkedHashMap<>(); // need to preserve parameter order
        if (sort != null ) {
          // Remove spaces and break into multiple sort queries
          String[] sortQuery = sort.replaceAll("\\s", "").split(",");

          for (String query : sortQuery) {
            String[] sortItem = query.split(":"); // split column name and order key

            // HTTP request check: sort key must match sortable column names; order key must match available orders
            if (sortItem.length != 2 || !COLUMN_KEYS.contains(sortItem[0].toLowerCase()) ||
                    !ORDER_KEYS.contains(sortItem[1].toLowerCase())) {
              throw new ApiError("Invalid sort parameter", 400);
            }
            sortParams.put(sortItem[0].toLowerCase(), sortItem[1].toUpperCase());
          }
        }

        List<Post> posts = postDao.readAllAdvanced(categoryString, keyword, sortParams);
        return gson.toJson(posts);

      } catch (DaoException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });

    get("/api/posts/:postUuid", (req, res) -> {
      try {
        String postUuid = req.params("postUuid");
        Post post = postDao.read(postUuid);
        if (post == null) {
          throw new ApiError("Resource not found", 404); // Bad request
        }
        return gson.toJson(post);
      } catch (DaoException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });

    post("/api/posts", (req, res) -> {
      try {
        Post post = gson.fromJson(req.body(), Post.class);
        postDao.create(post);
        res.status(201);
        return gson.toJson(post);
      } catch (DaoException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });

    put("/api/posts/:postUuid", (req, res) -> {
      try {
        String postUuid = req.params("postUuid");
        Post post = gson.fromJson(req.body(), Post.class);
        if (post.getId() == null) {
          throw new ApiError("Incomplete data", 500);
        }
        if (!post.getId().equals(postUuid)) {
          throw new ApiError("postUuid does not match the resource identifier", 400);
        }
        post = postDao.update(post.getId(), post);
        if (post == null) {
          throw new ApiError("Resource not found", 404);
        }
        return gson.toJson(post);
      } catch (DaoException | JsonSyntaxException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });

    delete("/api/posts/:postUuid", (req, res) -> {
      try {
        String postUuid = req.params("postUuid");
        Post post = postDao.delete(postUuid);
        if (post == null) {
          throw new ApiError("Resource not found", 404);   // No matching post
        }
        return gson.toJson(post);
      } catch (DaoException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });


    get("/api/users/:userId", (req, res) -> {
      try {
        String userId = req.params("userId");
        User user = userDao.read(userId);
        if (user == null) {
          throw new ApiError("Resource not found", 404); // Bad request
        }
        return gson.toJson(user);
      } catch (DaoException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });

    post("/api/users", (req, res) -> {
      try {
        User user = gson.fromJson(req.body(), User.class);
        userDao.create(user);
        res.status(201);
        return gson.toJson(user);
      } catch (DaoException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });

    put("/api/users/:userId", (req, res) -> {
      try {
        String userId = req.params("userId");
        User user = gson.fromJson(req.body(), User.class);
        if (user.getId() == null) {
          throw new ApiError("Incomplete data", 500);
        }
        if (!user.getId().equals(userId)) {
          throw new ApiError("userId does not match the resource identifier", 400);
        }
        user = userDao.update(user.getId(), user);
        if (user == null) {
          throw new ApiError("Resource not found", 404);
        }
        return gson.toJson(user);
      } catch (DaoException | JsonSyntaxException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });

    delete("/api/users/:userId", (req, res) -> {
      try {
        String userId = req.params("userId");
        User user = userDao.delete(userId);
        if (user == null) {
          throw new ApiError("Resource not found", 404);   // No matching user
        }
        return gson.toJson(user);
      } catch (DaoException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });

    after((req, res) -> res.type("application/json"));
  }
}