package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.RateDao;
import dao.jdbiDao.JdbiRateDao;
import exceptions.ApiError;
import exceptions.DaoException;
import org.jdbi.v3.core.Jdbi;
import spark.Request;
import spark.Response;
import spark.Route;

public class RateController {
  private static RateDao rateDao;
  private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  public RateController(Jdbi jdbi) {
    rateDao = new JdbiRateDao(jdbi);
  }

  public Route getRateOfASeller = (Request req, Response res) -> {
    try {
      String sellerId = req.params("sellerId");
      return null;
    } catch (DaoException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };
}
