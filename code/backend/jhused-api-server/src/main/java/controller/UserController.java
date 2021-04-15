package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dao.UserDao;
import dao.jdbiDao.JdbiUserDao;
import exceptions.ApiError;
import exceptions.DaoException;
import model.User;
import org.jdbi.v3.core.Jdbi;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class UserController {
  private static UserDao userDao;
  private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  public UserController(Jdbi jdbi) {
    userDao = new JdbiUserDao(jdbi);
  }

  public Route getAllUsers = (Request req, Response res) -> {
    try {
      List<User> users = userDao.readAll();
      if (users == null)
        throw new ApiError("No user found", 404);
      return gson.toJson(users);
    } catch (DaoException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };
  public Route getAUserGivenId = (Request req, Response res)->{
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
  };

  public Route createUser = (Request req, Response res)->{
    try {
      User user = gson.fromJson(req.body(), User.class);
      User createdUser = userDao.create(user);
      if(createdUser==null)
        throw new ApiError("Could not create the user", 400);
      res.status(201);
      return gson.toJson(createdUser);
    } catch (DaoException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  public Route updateUser = (Request req, Response res)->{
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
  };

  public Route deleteUser = (Request req, Response res)->{
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
  };
}
