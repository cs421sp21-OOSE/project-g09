package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.ApiError;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

import java.util.Map;

public class ExceptionController implements ExceptionHandler<ApiError> {
  private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  @Override
  public void handle(ApiError exception, Request request, Response response) {
    // Handle the exception here
    Map<String, String> map = Map.of("status", exception.getStatus() + "",
        "error", exception.getMessage());
    response.body(gson.toJson(map));
    response.status(exception.getStatus());
    response.type("application/json");
  }
}
