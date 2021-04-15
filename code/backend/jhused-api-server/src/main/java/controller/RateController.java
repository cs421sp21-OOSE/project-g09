package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.RateDao;
import dao.jdbiDao.JdbiRateDao;
import exceptions.ApiError;
import exceptions.DaoException;
import model.Rate;
import org.jdbi.v3.core.Jdbi;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class RateController {
  private static RateDao rateDao;
  private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  public RateController(Jdbi jdbi) {
    rateDao = new JdbiRateDao(jdbi);
  }

  public Route getAvgRateOfASeller = (Request req, Response res) -> {
    try {
      String sellerId = req.params("sellerId");
      Double avg = rateDao.readAvgRateOfASeller(sellerId);
      if (avg == null)
        throw new ApiError("This seller does not have any rates", 404);
      return gson.toJson(Map.of("averageRate", avg));
    } catch (DaoException | NullPointerException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  public Route getARateOfARaterToSeller = (Request req, Response res) -> {
    try {
      String raterId = req.params("raterId");
      String sellerId = req.params("sellerId");
      Rate rate = rateDao.read(raterId, sellerId);
      if (rate == null)
        throw new ApiError("This rater has not rated this seller", 404);
      return gson.toJson(rate);
    } catch (DaoException | NullPointerException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  public Route createARate = (Request req, Response res) -> {
    try {
      Rate rate = gson.fromJson(req.body(), Rate.class);
      Rate createdRate = rateDao.create(rate);
      if (createdRate == null)
        throw new ApiError("Could not create this rate", 400);
      res.status(201);
      return gson.toJson(createdRate);
    } catch (DaoException | NullPointerException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  public Route updateARate = (Request req, Response res) -> {
    try {
      String raterId = req.params("raterId");
      String sellerId = req.params("sellerId");
      Rate rate = gson.fromJson(req.body(), Rate.class);
      Rate updatedRate = rateDao.update(raterId, sellerId, rate);
      if (updatedRate == null)
        throw new ApiError("Could not update this rate", 400);
      return gson.toJson(updatedRate);
    } catch (DaoException | NullPointerException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  public Route deleteARate = (Request req, Response res) -> {
    try {
      String raterId = req.params("raterId");
      String sellerId = req.params("sellerId");
      Rate deletedRate = rateDao.delete(raterId, sellerId);
      if (deletedRate == null)
        throw new ApiError("Could not delete this rate", 400);
      return gson.toJson(deletedRate);
    } catch (DaoException | NullPointerException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };
}
