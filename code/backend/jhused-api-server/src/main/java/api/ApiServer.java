package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.*;
import dao.MessageDao;
import dao.PostDao;
import dao.UserDao;
import dao.WishlistPostSkeletonDao;
import dao.jdbiDao.JdbiMessageDao;
import dao.jdbiDao.JdbiPostDao;
import dao.jdbiDao.JdbiUserDao;
import dao.jdbiDao.JdbiWishlistPostSkeletonDao;
import exceptions.ApiError;
import org.jdbi.v3.core.Jdbi;
import org.pac4j.core.config.Config;
import spark.Route;
import spark.Spark;
import spark.embeddedserver.EmbeddedServers;
import util.SSO.JHUSSOConfigFactory;
import util.SSO.SamlTestSSOConfigFactory;
import util.database.Database;
import util.server.CustomEmbeddedJettyFactory;

import java.net.URISyntaxException;
import java.util.Map;

import static spark.Spark.*;

public class ApiServer {


  public static String FRONTEND_URL = "https://jhused-ui.herokuapp.com";
  public static String BACKEND_URL = "https://jhused-api-server.herokuapp.com";

  private static Jdbi jdbi;

  public static boolean isDebug;

  private static void setSSLForLocalDev() {
    if (isDebug) {
      secure("src/main/resources/samlKeystore.jks",
          "k-d1bf-i4s7*sd5fj", null, null);
    }
  }

  private static void checkIfDebug() {
    String mode = System.getenv("MODE");
    isDebug = mode != null && mode.equals("DEBUG");
    FRONTEND_URL = !isDebug ? FRONTEND_URL : "http://localhost:3000";
    BACKEND_URL = !isDebug ? BACKEND_URL : "https://localhost:8080";
  }

  private static void setJdbi() throws URISyntaxException {
    if (isDebug) {
      Database.USE_TEST_DATABASE = true;
    }
    jdbi = Database.getJdbi();
  }

  private static int getHerokuAssignedPort() {
    // Heroku stores port number as an environment variable
    String herokuPort = System.getenv("PORT");
    if (herokuPort != null) {
      return Integer.parseInt(herokuPort);
    }
    //return default port if heroku-port isn't set (i.e. on localhost)
    return 8080;
  }

  private static PostDao getPostDao() {
    return new JdbiPostDao(jdbi);
  }

  private static UserDao getUserDao() {
    return new JdbiUserDao(jdbi);
  }

  private static MessageDao getMessageDao() {
    return new JdbiMessageDao(jdbi);
  }

  private static WishlistPostSkeletonDao getWishlistSkeletonDao() throws URISyntaxException {
    return new JdbiWishlistPostSkeletonDao(jdbi);
  }

  /**
   * set access control request headers
   */
  public static void setAccessControlRequestHeaders() {
    Route setAccess =
        (request, response) -> {

          String accessControlRequestHeaders = request
              .headers("Access-Control-Request-Headers");
          if (accessControlRequestHeaders != null) {
            response.header("Access-Control-Allow-Headers",
                accessControlRequestHeaders);
          }

          String methodRequestHeaders = request
              .headers("Access-Control-Request-Method");
          if (methodRequestHeaders != null) {
            response.header("Access-Control-Allow-Methods",
                methodRequestHeaders);
          }

          String originRequestHeaders = request.headers("Origin");
          if (originRequestHeaders != null) {
            switch (originRequestHeaders) {
              case "http://localhost:3000":
              case "https://localhost:3000":
              case "https://jhused-ui.herokuapp.com":
              case "http://jhused-ui.herokuapp.com":
              default:
                response.header("Access-Control-Allow-Origin", originRequestHeaders);
                response.header("Vary", "Origin");
            }
          }
          return "OK";
        };

    before(setAccess::handle);
    before((request, response) -> response.header("Access-Control-Allow-Credentials", "true"));
    options("/*", ((request, response) -> "OK"));
  }

  /**
   * Stop the server.
   */
  public static void stop() {
    Spark.stop();
  }

  public static void main(String[] args) throws URISyntaxException {
    EmbeddedServers.add(
        EmbeddedServers.Identifiers.JETTY,
        new CustomEmbeddedJettyFactory());

    checkIfDebug();
    setSSLForLocalDev();

    port(getHerokuAssignedPort());
    setAccessControlRequestHeaders();

    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    setJdbi();

    // Define controllers
    PostController postController = new PostController(jdbi);
    SSOController ssoController = new SSOController(getSSOConfig(), jdbi);
    UserController userController = new UserController(jdbi);
    WishlistController wishListController = new WishlistController(jdbi);
    MessageController messageController = new MessageController(jdbi);
    RateController rateController = new RateController(jdbi);

    exception(ApiError.class, new ExceptionController());

    get("/", (req, res) -> {
      Map<String, String> message = Map.of("message", "Hello World");
      res.type("application/json");
      return gson.toJson(message);
    });

    // BEGIN POST ROUTES
    get("/api/posts", postController.getPosts);
    get("/api/posts/:postId", postController.getPostGivenId);
    post("/api/posts", postController.createPost);
    put("/api/posts/:postId", postController.updatePost);
    delete("/api/posts/:postId", postController.deletePost);
    // END POST ROUTES

    // BEGIN SSO ROUTES
    before("/jhu/login", ssoController.securityFilter);
    get("/jhu/login", ssoController.login);
    get("/api/userProfile", ssoController.getUserProfile);
    get("/callback", ssoController.callback);
    post("/callback", ssoController.callback);
    get("/centralLogout", ssoController.centralLogout);
    get("/redirectToFrontend", ssoController.redirectToFrontend);
    // END SSO ROUTES

    // BEGIN USER ROUTES
    get("/api/users", userController.getAllUsers);
    get("/api/users/:userId", userController.getAUserGivenId);
    post("/api/users", userController.createUser);
    put("/api/users/:userId", userController.updateUser);
    delete("/api/users/:userId", userController.deleteUser);
    // END USER ROUTES

    //BEGIN WISHLIST ROUTES
    //get all wishlist posts for specified user
    get("/api/users/:userId/wishlist/all", wishListController.getWishlistOfAUser);
    //add the specified post to the specified user's wishlist.
    post("/api/users/:userId/wishlist/:postId", wishListController.addWishlistPost);
    //delete the specified wishlist entry
    delete("/api/users/:userId/wishlist/:postId", wishListController.deleteAPostFromWishlist);
    // END WISHLIST ROUTE

    // BEGIN MESSAGE ROUTES
    get("/api/messages", messageController.getAllMessages);
    get("/api/messages/:userId", messageController.getAllMessagesOfAUser);
    put("/api/messages/:messageId", messageController.updateAMessage);
    post("/api/messages", messageController.createAOrAListOfMessage);
    delete("/api/messages/:messageId", messageController.deleteAMessage);
    delete("/api/messages", messageController.deleteAListOfMessages);
    // END MESSAGE ROUTES

    // BEGIN RATE ROUTES
    get("/api/rates/avg/:sellerId", rateController.getAvgRateOfASeller);
    get("/api/rates/:sellerId/:raterId", rateController.getARateOfARaterToSeller);
    post("/api/rates", rateController.createARate);
    put("/api/rates/:sellerId/:raterId", rateController.updateARate);
    delete("/api/rates/:sellerId/:raterId", rateController.deleteARate);
    //END RATE ROUTES
    after((req, res) -> res.type("application/json"));
  }

  private static Config getSSOConfig() {
    if (isDebug)
      return new SamlTestSSOConfigFactory().build();
    else
      return new JHUSSOConfigFactory().build();
  }
}