package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.WishlistPostSkeletonDao;
import dao.jdbiDao.JdbiWishlistPostSkeletonDao;
import exceptions.ApiError;
import exceptions.DaoException;
import model.Post;
import model.WishlistPostSkeleton;
import org.jdbi.v3.core.Jdbi;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class WishlistController {
  private static WishlistPostSkeletonDao wishlistPostSkeletonDao;
  private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

  public WishlistController(Jdbi jdbi) {
    wishlistPostSkeletonDao = new JdbiWishlistPostSkeletonDao(jdbi);
  }

  /**
   * get all wishlist posts for specified user
   */
  public Route getWishlistOfAUser = (Request req, Response res) -> {
    try {
      String userId = req.params("userId");
      List<Post> wishlist = wishlistPostSkeletonDao.readAllWishlistEntries(userId);
      if (wishlist.size() == 0) {
        throw new ApiError("Resource not found", 404); // Bad request
      }
      return gson.toJson(wishlist);
    } catch (DaoException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  /**
   * add the specified post to the specified user's wishlist.
   */
  public Route addWishlistPost = (Request req, Response res) -> {
    try {
      String userId = req.params("userId");
      String postId = req.params("postId");
      WishlistPostSkeleton addedWishlistEntry = wishlistPostSkeletonDao.createWishListEntry(postId, userId);
      if (addedWishlistEntry == null) {
        throw new ApiError("Resource not found", 404); // Bad request
      }
      return gson.toJson(addedWishlistEntry);
    } catch (DaoException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };

  /**
   * delete the specified wishlist entry
   */
  public Route deleteAPostFromWishlist = (Request req, Response res) -> {
    try {
      String userId = req.params("userId");
      String postId = req.params("postId");
      WishlistPostSkeleton deletedWishlistEntry = wishlistPostSkeletonDao.deleteWishlistEntry(postId, userId);
      if (deletedWishlistEntry == null) {
        throw new ApiError("Resource not found", 404); // Bad request
      }
      return gson.toJson(deletedWishlistEntry);
    } catch (DaoException ex) {
      throw new ApiError(ex.getMessage(), 500);
    }
  };
}
