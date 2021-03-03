package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.PostDao;
import dao.Sql2oPostDao;
import exceptions.ApiError;
import exceptions.DaoException;
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

    // TODO: implement requests

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
  }
}