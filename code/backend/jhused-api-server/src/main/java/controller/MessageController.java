package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dao.MessageDao;
import dao.jdbiDao.JdbiMessageDao;
import exceptions.ApiError;
import exceptions.DaoException;
import model.Message;
import org.jdbi.v3.core.Jdbi;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.List;

public class MessageController {
  private static MessageDao messageDao;
  private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  public MessageController(Jdbi jdbi) {
    messageDao = new JdbiMessageDao(jdbi);
  }

  public Route getAllMessages = (Request req, Response res) -> {
    try {
      return gson.toJson(messageDao.readAll());
    } catch (DaoException ex) {
      throw new ApiError("Can't read all messages" + ex.getMessage(), 500);
    }
  };

  public Route getAllMessagesOfAUser = (Request req, Response res) -> {
    try {
      String userId = req.params("userId");
      List<Message> messages = messageDao.readAllGivenSenderOrReceiverId(userId);
      return gson.toJson(messages);
    } catch (DaoException ex) {
      throw new ApiError("Can't read all messages given user id" + ex.getMessage(), 500);
    }
  };

  public Route updateAMessage = (Request req, Response res) -> {
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
  };

  public Route createAOrAListOfMessage = (Request req, Response res) -> {
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
  };

  public Route deleteAMessage = (Request req, Response res) -> {
    try {
      Message message = messageDao.delete(req.params("messageId"));
      if (message == null) {
        throw new ApiError("Resource not found", 404); // Bad request
      }
      return gson.toJson(message);
    } catch (DaoException | NullPointerException | JsonSyntaxException ex) {
      throw new ApiError("Message can't be created" + ex.getMessage(), 500);
    }
  };

  public Route deleteAListOfMessages = (Request req, Response res) -> {
    try {
      List<String> ids = gson.fromJson(req.body(), new TypeToken<ArrayList<String>>() {
      }.getType());
      List<Message> messages = messageDao.delete(ids);
      if (messages == null || ids == null || messages.size() != ids.size())
        throw new ApiError("Unable to delete all messages, contain invalid ids, rolled back, none is deleted.", 400);
      return gson.toJson(messages);
    } catch (DaoException | NullPointerException | JsonSyntaxException ex) {
      throw new ApiError("Message can't be created" + ex.getMessage(), 500);
    }
  };
}
