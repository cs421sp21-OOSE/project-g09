package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dao.MessageDao;
import dao.PostDao;
import dao.UserDao;
import dao.WishlistPostSkeletonDao;
import dao.jdbiDao.JdbiMessageDao;
import dao.jdbiDao.JdbiPostDao;
import dao.jdbiDao.JdbiUserDao;
import dao.jdbiDao.JdbiWishlistPostSkeletonDao;
import exceptions.ApiError;
import exceptions.DaoException;
import model.Message;
import model.Post;
import model.User;
import model.WishlistPostSkeleton;
import org.jdbi.v3.core.Jdbi;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.SecurityFilter;
import org.pac4j.sparkjava.SparkWebContext;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import util.SSO.JHUSSOConfigFactory;
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
  private static final String FRONTEND_URL = "https://jhused-ui.herokuapp.com";

  private static Jdbi jdbi;

  private static void setJdbi() throws URISyntaxException {
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
    port(getHerokuAssignedPort());
    setAccessControlRequestHeaders();
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    setJdbi();
    PostDao postDao = getPostDao();
    UserDao userDao = getUserDao();
    MessageDao messageDao = getMessageDao();
    WishlistPostSkeletonDao wishlistPostSkeletonDao = getWishlistSkeletonDao();

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
        if (sort != null) {
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

    final Config config = new JHUSSOConfigFactory().build();

    //SSO filter
    before("/jhu/login", new SecurityFilter(config, "SAML2Client"));

    /*
      Frontend should redirect user to backend, to this address
      When the user tries to visit this address, security filter will
      kick in and check if the user has already signed in.
         If signed in,
             we should redirect the user to frontend homepage.
         If not signed in,
             security filter will redirect user to SSO, where user signs in, then
             SSO will route to the callbackurl with user's profile. In this call back route,
             we should interact with the database to check if the user is a first time user.
             If so, create user in database.
             To do this, I think we need to write a
             Callback logic class (not so sure, about to look for doc).
             Then, the user signed in, he will be redirect again back to this address, where we
             redirect him to the ui frontend homepage.
      It seems that session stuff is handled by pac4j (the default callbacklogic)
     */
    get("/jhu/login", (req, res) -> {
      List<CommonProfile> userProfiles = getProfiles(req, res);
      try {
        if (userProfiles.size() != 1) {
          throw new ApiError("Got multiple user profiles, unexpected.", 500);
        }
        CommonProfile userProfile = userProfiles.get(0);
        User user = userDao.read(userProfile.getUsername()); // user name  is JHED ID
        if (user == null) {
          if (userProfile.getUsername() == null) {
            throw new ApiError("Empty user name, unexpected, should be JHED", 500);
          }
          user = new User(userProfile.getUsername(), userProfile.getUsername(),
              userProfile.getUsername() + "@jh.edu", "", "");
          if (userDao.create(user) == null) {
            throw new ApiError("Unable to create user: " + userProfile.toString(), 500);
          }
          res.redirect(FRONTEND_URL + "/user/settings/" + userProfile.getUsername(), 302);
        } else {
          res.redirect(FRONTEND_URL, 302);
        }
      } catch (DaoException | NullPointerException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
      return null;
    });

    final CallbackRoute callback = new CallbackRoute(config, null, true);
    //callback.setRenewSession(false);
    get("/callback", callback);
    post("/callback", callback);

    /*
      returns the user's profile (it's a SSO thing, not the one in our database)
      returns empty [] if user is not signed in.
     */
    get("/api/userProfile", (req, res) -> {
      final Map map = new HashMap();
      map.put("profiles", getProfiles(req, res));
      return gson.toJson(map);
    });

    get("/api/users", (req, res) -> {
      try {
        return gson.toJson(userDao.readAll());
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

    //BEGIN WISHLIST ROUTES

    //get all wishlist posts for specified user
    get("/api/users/:userId/wishlist/all", (req, res) -> {
      try {
        String userId = req.params("userId");
        List<Post> wishlist = getWishlistSkeletonDao().readAllWishlistEntries(userId);
        if (wishlist.size() == 0) {
          throw new ApiError("Resource not found", 404); // Bad request
        }
        return gson.toJson(wishlist);
      } catch (DaoException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });


    //add the specified post to the specified user's wishlist.
    post("/api/users/:userId/wishlist/:postId", (req, res) -> {
      try {
        String userId = req.params("userId");
        String postId = req.params("postId");
        WishlistPostSkeleton addedWishlistEntry = getWishlistSkeletonDao().createWishListEntry(postId, userId);
        if (addedWishlistEntry == null) {
          throw new ApiError("Resource not found", 404); // Bad request
        }
        return gson.toJson(addedWishlistEntry);
      } catch (DaoException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });

    //delete the specified wishlist entry
    delete("/api/users/:userId/wishlist/:postId", (req, res) -> {
      try {
        String userId = req.params("userId");
        String postId = req.params("postId");
        WishlistPostSkeleton deletedWishlistEntry = getWishlistSkeletonDao().deleteWishlistEntry(postId, userId);
        if (deletedWishlistEntry == null) {
          throw new ApiError("Resource not found", 404); // Bad request
        }
        return gson.toJson(deletedWishlistEntry);
      } catch (DaoException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });

    // END WISHLIST ROUTE

    get("/api/messages", (req, res) -> {
      try {
        return gson.toJson(messageDao.readAll());
      } catch (DaoException ex) {
        throw new ApiError("Can't read all messages" + ex.getMessage(), 500);
      }
    });

    get("/api/messages/:userId", (req, res) -> {
      try {
        String userId = req.params("userId");
        return gson.toJson(messageDao.readAllGivenSenderOrReceiverId(userId));
      } catch (DaoException ex) {
        throw new ApiError("Can't read all messages given user id" + ex.getMessage(), 500);
      }
    });

    put("/api/messages/:messageId", (req, res) -> {
      try {
        String messageId = req.params("messageId");
        Message message = gson.fromJson(req.body(), Message.class);
        if (message.getId() == null) {
          throw new ApiError("Incomplete data", 400);
        }
        if (!message.getId().equals(messageId)) {
          throw new ApiError("messageId does not match the resource identifier", 400);
        }
        message = messageDao.update(messageId, message);
        if (message == null) {
          throw new ApiError("Update failure", 500);
        }
        return gson.toJson(message);
      } catch (DaoException | NullPointerException | JsonSyntaxException ex) {
        throw new ApiError(ex.getMessage(), 500);
      }
    });

    post("/api/messages", (req, res) -> {
      try {
        boolean isList = Boolean.parseBoolean(req.queryParams("isList"));
        if (!isList) {
          Message message = gson.fromJson(req.body(), Message.class);
          if (message == null) {
            throw new ApiError("Message sent is null", 400);
          }
          Message resMessage = messageDao.create(message);
          if (resMessage != null) {
            res.status(201);
          } else {
            throw new ApiError("Can't create the message: ", 400);
          }
          return gson.toJson(resMessage);
        } else {
          List<Message> message = gson.fromJson(req.body(), new TypeToken<ArrayList<Message>>() {
          }.getType());
          if (message == null) {
            throw new ApiError("Messages sent is null", 400);
          }
          List<Message> resMessage = messageDao.create(message);
          if (resMessage != null) {
            res.status(201);
          } else {
            throw new ApiError("Can't create the message: ", 400);
          }
          return gson.toJson(resMessage);
        }
      } catch (DaoException | NullPointerException | JsonSyntaxException ex) {
        throw new ApiError("Message can't be created" + ex.getMessage(), 500);
      }
    });

    delete("/api/messages/:messageId", (req, res) -> {
      try {
        Message message = messageDao.delete(req.params("messageId"));
        if (message == null) {
          throw new ApiError("Resource not found", 404); // Bad request
        }
        return gson.toJson(message);
      } catch (DaoException | NullPointerException | JsonSyntaxException ex) {
        throw new ApiError("Message can't be created" + ex.getMessage(), 500);
      }
    });

    delete("/api/messages", (req, res) -> {
      try {
        List<String> ids = gson.fromJson(req.body(), new TypeToken<ArrayList<String>>() {
        }.getType());
        List<Message> messages = messageDao.delete(ids);
        if(messages==null||ids==null||messages.size()!=ids.size())
          throw new ApiError("Unable to delete all messages, contain invalid ids, rolled back, none is deleted.", 400);
        return gson.toJson(messages);
      } catch (DaoException | NullPointerException | JsonSyntaxException ex) {
        throw new ApiError("Message can't be created" + ex.getMessage(), 500);
      }
    });

    after((req, res) -> res.type("application/json"));
  }

  private static List<CommonProfile> getProfiles(final Request request, final Response response) {
    final SparkWebContext context = new SparkWebContext(request, response);
    final ProfileManager manager = new ProfileManager(context);
    return manager.getAll(true);
  }
}