package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dao.PostDao;
import dao.Sql2oPostDao;
import exceptions.ApiError;
import exceptions.DaoException;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import model.Post;
import org.sql2o.Sql2o;
import spark.Spark;
import util.Database;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class ApiServer {
  private static int getHerokuAssignedPort() {
    // Heroku stores port number as an environment variable
    String herokuPort = System.getenv("PORT");
    if (herokuPort != null) {
      return Integer.parseInt(herokuPort);
    }
    //return default port if heroku-port isn't set (i.e. on localhost)
    return 4567;
  }

  private static PostDao getPostDao() throws URISyntaxException {
    Sql2o sql2o = Database.getSql2o();
    return new Sql2oPostDao(sql2o);
  }

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


    // return all posts
    get("/api/posts", (req, res) -> {
      try {
        String title = req.queryParams("title");
        List<Post> Posts;
        if (title != null) {
          Posts = postDao.readAll(title);
        } else {
          Posts = postDao.readAll();
        }
        return gson.toJson(Posts);
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
        if (post.getUuid() == null) {
          throw new ApiError("Incomplete data", 500);
        }
        if (!post.getUuid().equals(postUuid)) {
          throw new ApiError("postUuid does not match the resource identifier", 400);
        }
        post = postDao.update(post.getUuid(), post);
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

    after((req, res) -> res.type("application/json"));
  }
}