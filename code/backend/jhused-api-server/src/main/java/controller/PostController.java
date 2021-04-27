package controller;

import api.ApiServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import dao.PostDao;
import dao.jdbiDao.JdbiPostDao;
import exceptions.ApiError;
import exceptions.DaoException;
import model.Post;
import org.jdbi.v3.core.Jdbi;
import spark.Request;
import spark.Response;
import spark.Route;
import util.paginationSkeleton.PostPaginationSkeleton;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PostController {
  // Admissible query parameters for sorting
  private static final Set<String> COLUMN_KEYS = Set.of("title", "price",
      "create_time", "update_time", "location");
  // Admissible sort types
  private static final Set<String> ORDER_KEYS = Set.of("asc", "desc");
  private static final Set<String> CATEGORY_KEYS = Set.of("furniture", "car", "electronics", "property_rental", "sporting_goods", "apparel", "music_instrument", "home_goods", "office_supply", "free", "other");
  private static PostDao postDao;
  private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  public PostController(Jdbi jdbi) {
    postDao = new JdbiPostDao(jdbi);
  }

  public Route getPostsPagination = (Request req, Response res) -> {
    try {
      String categoryString = req.queryParams("category");
      if (categoryString != null && !CATEGORY_KEYS.contains(categoryString.toLowerCase())) {
        throw new ApiError("Invalid category parameter", 400);
      }

      String keyword = req.queryParams("keyword"); // use keyword for search
      String sort = req.queryParams("sort");
      Map<String, String> sortParams = handleSortParam(sort);

      String pageStr = req.queryParams("page");
      String limitStr = req.queryParams("limit");

      // default value of page and limit
      int page = 1;
      int limit = 50;
      if (!(pageStr == null || limitStr == null)) {
        page = Integer.parseInt(req.queryParams("page"));
        limit = Integer.parseInt(req.queryParams("limit"));
        if (page < 0 || limit < 0)
          throw new ApiError("Page or limit present but invalid.", 400);
      }
      int totalRow = postDao.getTotalRowNum(categoryString, keyword, sortParams,0,0);
      int totalPage = totalRow / limit + 1;

      PostPaginationSkeleton postPaginationSkeleton = new PostPaginationSkeleton();
      postPaginationSkeleton.getPagination().put("page", page);
      postPaginationSkeleton.getPagination().put("limit", limit);
      postPaginationSkeleton.getPagination().put("last", totalPage);
      postPaginationSkeleton.getPagination().put("total", totalRow);
      String request = (ApiServer.BACKEND_URL + "/api/v2/posts?page=%d&limit=" + limit);
      postPaginationSkeleton.getLinks().put("first", String.format(request, 1));
      postPaginationSkeleton.getLinks().put("last", String.format(request, totalPage));
      postPaginationSkeleton.getLinks().put("prev", String.format(request, page == 1 ? 1 : (page - 1)));
      postPaginationSkeleton.getLinks().put("next", String.format(request, totalPage > page ? (page + 1) : (page)));
      postPaginationSkeleton.setPosts(postDao.readAllAdvanced(categoryString, keyword, sortParams, page, limit));

      return gson.toJson(postPaginationSkeleton);

    } catch (DaoException | NullPointerException | NumberFormatException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  /**
   * Read all posts matching the query parameters if they exist
   * Handle category match, keyword search, and sort
   */
  public Route getPosts = (Request req, Response res) -> {
    try {
      String categoryString = req.queryParams("category");
      if (categoryString != null && !CATEGORY_KEYS.contains(categoryString.toLowerCase())) {
        throw new ApiError("Invalid category parameter", 400);
      }

      String keyword = req.queryParams("keyword"); // use keyword for search
      String sort = req.queryParams("sort");
      Map<String, String> sortParams = handleSortParam(sort);
      List<Post> posts = postDao.readAllAdvanced(categoryString, keyword, sortParams);
      return gson.toJson(posts);

    } catch (DaoException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  public Route getPostGivenId = (Request req, Response res) -> {
    try {
      String postId = req.params("postId");
      Post post = postDao.read(postId);
      if (post == null) {
        throw new ApiError("Resource not found", 404); // Bad request
      }
      return gson.toJson(post);
    } catch (DaoException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  public Route createPost = (Request req, Response res) -> {
    try {
      Post post = gson.fromJson(req.body(), Post.class);
      Post createdPost = postDao.create(post);
      if(createdPost!=null)
        res.status(201);
      else {
        throw new ApiError("Unable to create the post",400);
      }
      return gson.toJson(post);
    } catch (DaoException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  public Route updatePost = (Request req, Response res) -> {
    try {
      String postId = req.params("postId");
      Post post = gson.fromJson(req.body(), Post.class);
      if (post.getId() == null) {
        throw new ApiError("Incomplete data", 500);
      }
      if (!post.getId().equals(postId)) {
        throw new ApiError("postId does not match the resource identifier", 400);
      }
      post = postDao.update(post.getId(), post);
      if (post == null) {
        throw new ApiError("Resource not found", 404);
      }
      return gson.toJson(post);
    } catch (DaoException | JsonSyntaxException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  public Route deletePost = (Request req, Response res) -> {
    try {
      String postId = req.params("postId");
      Post post = postDao.delete(postId);
      if (post == null) {
        throw new ApiError("Resource not found", 404);   // No matching post
      }
      return gson.toJson(post);
    } catch (DaoException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  private Map<String, String> handleSortParam(String sort) throws ApiError {
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
    return sortParams;
  }
}
