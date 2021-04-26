package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.PostVisitDao;
import dao.jdbiDao.JdbiPostVisitDao;
import exceptions.ApiError;
import exceptions.DaoException;
import model.PostVisit;
import org.jdbi.v3.core.Jdbi;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class PostVisitController {
  private static PostVisitDao postVisitDao;
  private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();


  public PostVisitController(Jdbi jdbi) {
    postVisitDao = new JdbiPostVisitDao(jdbi);
  }

  public Route postView = (Request req, Response res) -> {
    try {
      PostVisit postVisit = gson.fromJson(req.body(), PostVisit.class);
      PostVisit createdPostVisit = postVisitDao.create(postVisit);
      if (createdPostVisit != null && createdPostVisit.equals(postVisit)) {
        res.status(201);
      } else {
        throw new ApiError("Unable to mark view, something wrong with the request", 400);
      }
      return gson.toJson(createdPostVisit);
    } catch (DaoException | NullPointerException ex) {
      throw new ApiError("Unable to mark view" + ex.getMessage(), 500);
    }
  };

  public Route getViewCount = (Request req, Response res) -> {
    try {
      String postId = req.params("postId");
      int viewCnt = postVisitDao.visitCount(postId);
      return gson.toJson(Map.of("viewCount", viewCnt));
    } catch (DaoException | NullPointerException ex) {
      throw new ApiError("Unable to read view count" + ex.getMessage(), 500);
    }
  };

  public Route getPostVisit = (Request req, Response res)->{
    try {
      String postId = req.params("postId");
      String userId = req.params("userId");
      PostVisit postVisit = postVisitDao.read(postId,userId);
      if(postVisit==null)
        throw new ApiError("Not found",404);
      return gson.toJson(postVisit);
    } catch (DaoException | NullPointerException ex) {
      throw new ApiError("Unable to read the visit" + ex.getMessage(), 500);
    }
  };
}
