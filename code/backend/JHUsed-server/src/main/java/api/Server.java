package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.PostDao;
import controller.Sql2oPostDao;
import exceptions.ApiError;
import java.net.URISyntaxException;
import java.util.Map;
import kong.unirest.Unirest;
import model.Post;
import org.sql2o.Sql2o;
import spark.Spark;
import util.Database;

import static spark.Spark.*;

public class Server {
  public static void main(String[] args) throws URISyntaxException {
    Unirest.config().defaultBaseUrl("http://localhost");

    port(getHerokuAssignedPort());

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
        
    staticFiles.location("/public");
    
    
    // TODO: implement requests
    get("/", (req, res) -> {
      return "Hello World";
    });
  }

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

  /**
   * Stop the server.
   */
  public static void stop() {
    Spark.stop();
  }

}
