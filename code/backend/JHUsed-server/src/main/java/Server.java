import kong.unirest.Unirest;

import static spark.Spark.*;

public class Server {
  public static void main(String[] args) {
    Unirest.config().defaultBaseUrl("http://localhost");

    port(getHerokuAssignedPort());
    staticFiles.location("/public");

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
}
