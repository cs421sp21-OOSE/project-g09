package dao.jdbiDao;

import dao.WishlistPostSkeletonDao;
import exceptions.DaoException;
import model.Post;
import model.WishlistPostSkeleton;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementException;
import util.jdbiResultSetHandler.ResultSetLinkedHashMapAccumulatorProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class JdbiWishlistPostSkeletonDao implements WishlistPostSkeletonDao {
  private final Jdbi jdbi;
  private final JdbiPostDao postDao;
  private final ResultSetLinkedHashMapAccumulatorProvider<Post> postAccumulator;

  public JdbiWishlistPostSkeletonDao(Jdbi jdbi) {
    this.jdbi = jdbi;
    postDao = new JdbiPostDao(jdbi);
    postAccumulator = new ResultSetLinkedHashMapAccumulatorProvider<>(Post.class);
  }

  @Override
  public WishlistPostSkeleton createWishListEntry(String postId, String userId) throws DaoException {
    String createWishlistEntrySql = "WITH inserted AS (INSERT INTO wishlist_post(post_id, user_id) VALUES(:post_id, "
        + ":user_id) RETURNING *) SELECT * FROM inserted;";

    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(createWishlistEntrySql)
              .bind("post_id", postId)
              .bind("user_id", userId)
              .mapToBean(WishlistPostSkeleton.class)
              .findOne()).orElse(null);
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public List<Post> readAllWishlistEntries(String userId) throws DaoException {
    final String SELECT_POST =
        "WITH wishlist AS (SELECT * FROM wishlist_post WHERE wishlist_post.user_id=:userId) " +
            "SELECT post.*, "
            + "image.id as images_id, "
            + "image.url as images_url,"
            + "image.post_id as images_post_id, "
            + "hashtag.id as hashtags_id, "
            + "hashtag.hashtag as hashtags_hashtag "
            + "FROM wishlist "
            + "LEFT JOIN post ON wishlist.post_id=post.id "
            + "LEFT JOIN image ON image.post_id = post.id "
            + "LEFT JOIN post_hashtag ON post_hashtag.post_id = post.id "
            + "LEFT JOIN hashtag ON hashtag.id = post_hashtag.hashtag_id ";
    try {
      return jdbi.inTransaction(handle -> new ArrayList<>(handle.createQuery(SELECT_POST).bind("userId", userId)
          .reduceResultSet(new LinkedHashMap<>(), postAccumulator).values()));
    } catch (StatementException | IllegalStateException ex) {
      throw new DaoException("Unable to read posts of wishlist for userId " + userId, ex);
    }
  }

  @Override
  public List<WishlistPostSkeleton> readAll(String userId) throws DaoException {
    String sql = "SELECT * FROM wishlist_post WHERE user_id = :user_id;";

    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql)
              .bind("user_id", userId)
              .mapToBean(WishlistPostSkeleton.class)
              .list());
    } catch (StatementException | IllegalStateException ex) {
      throw new DaoException("Unable to read wishlist for userId " + userId, ex);
    }

  }

  @Override
  public WishlistPostSkeleton deleteWishlistEntry(String post_id, String user_id) throws DaoException {
    String deleteWishlistEntrySql = "WITH deleted AS (DELETE FROM wishlist_post WHERE post_id=:post_id AND "
        + "user_id=:user_id RETURNING *) SELECT * FROM deleted;";

    try {
      return jdbi.inTransaction(handle -> handle.createQuery(deleteWishlistEntrySql).bind("post_id", post_id).bind(
          "user_id", user_id).mapToBean(WishlistPostSkeleton.class).findOne().orElse(null));
    } catch (StatementException | IllegalStateException | NullPointerException ex) {
      throw new DaoException(ex.getMessage(), ex);
    }
  }

  @Override
  public List<WishlistPostSkeleton> readAllFromPostId(String post_id) throws DaoException {
    String sql = "SELECT * FROM wishlist_post WHERE post_id = :post_id;";

    //for debugging
    /*System.out.println(post_id);*/

    try {
      return jdbi.inTransaction(handle ->
              handle.createQuery(sql)
                      .bind("post_id", post_id)
                      .mapToBean(WishlistPostSkeleton.class)
                      .list());
    } catch (StatementException | IllegalStateException ex) {
      throw new DaoException("Unable to read wishlist for post_id " + post_id, ex);
    }

  }

}
