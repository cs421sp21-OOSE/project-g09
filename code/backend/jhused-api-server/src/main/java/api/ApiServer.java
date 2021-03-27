package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dao.PostDao;
import dao.jdbiDao.JdbiPostDao;
import dao.sql2oDao.Sql2oPostDao;
import exceptions.ApiError;
import exceptions.DaoException;
import model.Post;
import org.pac4j.core.client.Client;
import org.pac4j.core.config.Config;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.sparkjava.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;
import util.SSO.SSOConfigFactory;
import util.SSO.SSOInterface;
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

  //Begin SSO stuff
  private final static String JWT_SALT = "12345678901234567890123456789012";
  private final static MustacheTemplateEngine templateEngine = new MustacheTemplateEngine();
  //end SSO stuff


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
    return new JdbiPostDao(Database.getJdbi());
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

  /**
   * Main Method
   * @param args
   * @throws URISyntaxException
   */
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

    //TODO SSO redirection and session baton pass.

    //SSO filter
    final Config config =
            new SSOConfigFactory(JWT_SALT, templateEngine).build();

    before("/jhu/login", new SecurityFilter(config, "SAML2Client"));

    //for SSO Login
    get("/jhu/login",(req, res) -> {
      //redirect to SSO login.
      throw new ApiError("Resource not found", 404);
    });

    // account logout. Redirects to homepage with all posts.
    // TODO Not sure what the default url for line 232 should be.
    final LogoutRoute localLogout = new LogoutRoute(config, "/");
    localLogout.setDestroySession(true);
    get("/logout", localLogout);

    //centralLogout. Not sure what the differences are so i put them both
    // here just in case.
    //TODO figure out defaultURLs
    final LogoutRoute centralLogout = new LogoutRoute(config);
    centralLogout.setDefaultUrl("https://jhused-ui.herokuapp.com/");
    centralLogout.setLogoutUrlPattern("https://jhused-ui.herokuapp.com.*");
    centralLogout.setLocalLogout(false);
    centralLogout.setCentralLogout(true);
    centralLogout.setDestroySession(true);
    get("/centralLogout", centralLogout);


    //TODO update this GET method and the callback to interact with DB
    get("/", ApiServer::index, templateEngine);

    final CallbackRoute callback = new CallbackRoute(config, null, true);
    //callback.setRenewSession(false);
    get("/callback", callback);
    post("/callback", callback);

    exception(Exception.class, (e, request, response) -> {
      response.body(templateEngine.render(new ModelAndView(new HashMap<>(), "error500.mustache")));
    });

    after((req, res) -> res.type("application/json"));
  }


  //Can be moved into SSOInterface inside of util.SSO if you want.

  private static ModelAndView index(final Request request, final Response response) {
    final Map map = new HashMap();
    map.put("profiles", getProfiles(request, response));
    final SparkWebContext ctx = new SparkWebContext(request, response);
    map.put("sessionId", ctx.getSessionStore().getOrCreateSessionId(ctx));
    return new ModelAndView(map, "index.mustache");
  }

  private static List<CommonProfile> getProfiles(final Request request, final Response response) {
    final SparkWebContext context = new SparkWebContext(request, response);
    final ProfileManager manager = new ProfileManager(context);
    return manager.getAll(true);
  }




}