package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ApiError;

import java.util.Map;

import static spark.Spark.*;

public class Server {
  public static void main(String[] args) {
    port(getHerokuAssignedPort());
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();

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
}
