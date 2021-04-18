package controller;

import api.ApiServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.UserDao;
import dao.jdbiDao.JdbiUserDao;
import exceptions.ApiError;
import exceptions.DaoException;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.sparkjava.CallbackRoute;
import org.pac4j.sparkjava.LogoutRoute;
import org.pac4j.sparkjava.SecurityFilter;
import org.pac4j.sparkjava.SparkWebContext;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SSOController {

  public final Config config;
  public final CallbackRoute callback;
  public final SecurityFilter securityFilter;
  public final LogoutRoute centralLogout;
  private static UserDao userDao;
  private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  public SSOController(Config config, Jdbi jdbi) {
    this.config = config;
    callback = new CallbackRoute(config, null, true);
    securityFilter = new SecurityFilter(config, "SAML2Client");
    userDao = new JdbiUserDao(jdbi);
    centralLogout = new LogoutRoute(config);
    configCentralLogout();
  }

  private void configCentralLogout() {
    centralLogout.setDefaultUrl(ApiServer.BACKEND_URL + "/redirectToFrontend");
    centralLogout.setLogoutUrlPattern(ApiServer.BACKEND_URL + "/*");
    centralLogout.setLocalLogout(true);
    centralLogout.setCentralLogout(true);
    centralLogout.setDestroySession(true);
  }

  /**
   * Frontend should redirect user to backend, to this address
   * When the user tries to visit this address, security filter will
   * kick in and check if the user has already signed in.
   * If signed in,
   * we should redirect the user to frontend homepage.
   * If not signed in,
   * security filter will redirect user to SSO, where user signs in, then
   * SSO will route to the callbackurl with user's profile. In this call back route,
   * we should interact with the database to check if the user is a first time user.
   * If so, create user in database.
   * To do this, I think we need to write a
   * Callback logic class (not so sure, about to look for doc).
   * Then, the user signed in, he will be redirect again back to this address, where we
   * redirect him to the ui frontend homepage.
   * It seems that session stuff is handled by pac4j (the default callbacklogic)
   */
  public Route login = (Request req, Response res) -> {
    List<CommonProfile> userProfiles = getProfiles(req, res);
    try {
      if (userProfiles.size() != 1) {
        throw new ApiError("Got multiple user profiles, unexpected.", 500);
      }
      CommonProfile userProfile = userProfiles.get(0);
      String key = ApiServer.isDebug ? userProfile.getAttribute("userid").toString() : userProfile.getUsername();
      User user = userDao.read(key); // user name  is JHED ID
      if (user == null) {
        if (key == null) {
          throw new ApiError("Empty user name, unexpected, should be JHED", 500);
        }
        if (!ApiServer.isDebug) {
          user = new User(key, key, key + "@jh.edu", "", "");
        } else {
          user = new User(key, key, key, "", "");
        }
        if (userDao.create(user) == null) {
          throw new ApiError("Unable to create user: " + userProfile.toString(), 500);
        }
        res.redirect(ApiServer.FRONTEND_URL + "/user/settings/" + userProfile.getUsername(), 302);
      } else {
        res.redirect(ApiServer.FRONTEND_URL, 302);
      }
    } catch (DaoException | NullPointerException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
    return null;
  };

  public Route redirectToFrontend = (Request req, Response res) -> {
    res.redirect(ApiServer.FRONTEND_URL, 302);
    return null;
  };

  /**
   * returns the user's profile (it's a SSO thing, not the one in our database)
   * returns empty [] if user is not signed in.
   */
  public Route getUserProfile = (Request req, Response res) -> {
    final Map map = new HashMap();
    map.put("profiles", getProfiles(req, res));
    return gson.toJson(map);
  };

  private static List<CommonProfile> getProfiles(final Request request, final Response response) {
    final SparkWebContext context = new SparkWebContext(request, response);
    final ProfileManager manager = new ProfileManager(context);
    List<CommonProfile> profiles = manager.getAll(true);
    if (ApiServer.isDebug && profiles != null && profiles.size() != 0) {
      profiles.get(0).addAttribute("userid", ((ArrayList) (profiles.get(0).getAttribute("mail"))).get(0));
    }
    return profiles;
  }
}
