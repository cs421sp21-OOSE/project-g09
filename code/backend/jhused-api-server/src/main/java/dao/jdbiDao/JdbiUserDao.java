package dao.jdbiDao;

import dao.PostDao;
import dao.UserDao;
import dao.WishlistPostSkeletonDao;
import email.Welcome.WelcomeEmails;
import exceptions.DaoException;
import model.Post;
import model.User;
import model.WishlistPostSkeleton;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementException;
import util.jdbiResultSetHandler.ResultSetLinkedHashMapAccumulatorProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class JdbiUserDao implements UserDao {
  private final Jdbi jdbi;
  private final PostDao postDao;
  private final WishlistPostSkeletonDao wishlistPostSkeletonDao;
  private final ResultSetLinkedHashMapAccumulatorProvider<User> userAccumulator;
  private final String SELECT_USER_BASIC =
      "SElECT jhused_user.*,"
          + "user_post.id as posts_id, "
          + "user_post.user_id as posts_user_id, "
          + "user_post.title as posts_title, "
          + "user_post.price as posts_price, "
          + "user_post.sale_state as posts_sale_state, "
          + "user_post.description as posts_description, "
          + "user_post.category as posts_category, "
          + "user_post.location as posts_location, "
          + "user_post.create_time as posts_create_time, "
          + "user_post.update_time as posts_update_time, "
          + "user_post_image.id as posts_images_id, "
          + "user_post_image.url as posts_images_url,"
          + "user_post_image.post_id as posts_images_post_id, "
          + "user_hashtag.id as posts_hashtags_id, "
          + "user_hashtag.hashtag as posts_hashtags_hashtag, "

          + "wishlist_post.id as wishlist_id, "
          + "wishlist_post.user_id as wishlist_user_id, "
          + "wishlist_post.title as wishlist_title, "
          + "wishlist_post.price as wishlist_price, "
          + "wishlist_post.sale_state as wishlist_sale_state, "
          + "wishlist_post.description as wishlist_description, "
          + "wishlist_post.category as wishlist_category, "
          + "wishlist_post.location as wishlist_location, "
          + "wishlist_post.create_time as wishlist_create_time, "
          + "wishlist_post.update_time as wishlist_update_time, "
          + "wishlist_post_image.id as wishlist_images_id, "
          + "wishlist_post_image.url as wishlist_images_url,"
          + "wishlist_post_image.post_id as wishlist_images_post_id, "
          + "wishlist_hashtag.id as wishlist_hashtags_id, "
          + "wishlist_hashtag.hashtag as wishlist_hashtags_hashtag "

          + "FROM jhused_user "
          + "LEFT JOIN post user_post ON jhused_user.id = user_post.user_id "
          + "LEFT JOIN image user_post_image ON user_post.id = user_post_image.post_id "
          + "LEFT JOIN post_hashtag user_post_hashtag ON user_post.id = user_post_hashtag.post_id "
          + "LEFT JOIN hashtag user_hashtag ON user_post_hashtag.hashtag_id = user_hashtag.id "

          + "LEFT JOIN wishlist_post wishlist_post_relation ON jhused_user.id = wishlist_post_relation.user_id "
          + "LEFT JOIN post wishlist_post ON wishlist_post_relation.post_id = wishlist_post.id "
          + "LEFT JOIN image wishlist_post_image ON wishlist_post.id = wishlist_post_image.post_id "
          + "LEFT JOIN post_hashtag wishlist_post_hashtag ON wishlist_post.id = wishlist_post_hashtag.post_id "
          + "LEFT JOIN hashtag wishlist_hashtag ON wishlist_post_hashtag.hashtag_id = wishlist_hashtag.id ";

  private final String SELECT_USER_GIVEN_ID = SELECT_USER_BASIC
      + "WHERE jhused_user.id = :userId;";

  private final String SELECT_ALL_USERS = SELECT_USER_BASIC;

  private final String defaultProfileImage = "https://i.redd.it/v2h2px8w5piz.png";

  public JdbiUserDao(Jdbi jdbi) {
    this.jdbi = jdbi;
    this.postDao = new JdbiPostDao(jdbi);
    this.wishlistPostSkeletonDao = new JdbiWishlistPostSkeletonDao(jdbi);
    this.userAccumulator = new ResultSetLinkedHashMapAccumulatorProvider<>(User.class);
  }

  @Override
  public User create(User user) throws DaoException {

    String sql = "INSERT INTO jhused_user(id, name, email, profile_image, location) "
        + "VALUES(:id, :name, :email, :profileImage, :location);";
    if (user.getProfileImage() == null || user.getProfileImage().isEmpty()) {
      user.setProfileImage(defaultProfileImage);
    }
    if (user.getPosts() == null) {
      user.setPosts(new ArrayList<>());
    }
    if (user.getWishlist() == null) {
      user.setWishlist(new ArrayList<>());
    }
    try {
      return jdbi.inTransaction(handle -> {
        handle.createUpdate(sql).bindBean(user).execute();
        if (!user.getPosts().isEmpty()) {
          for (Post post : user.getPosts())
            postDao.create(post);
        }
        if (!user.getWishlist().isEmpty()) {
          for (Post post: user.getWishlist()) {
            wishlistPostSkeletonDao.createWishListEntry(post.getId(), user.getId());
          }
        }

        //send welcome email!
        WelcomeEmails.basicWelcomeEmail(user.getEmail());

        return new ArrayList<>(handle.createQuery(SELECT_USER_GIVEN_ID).bind("userId", user.getId())
            .reduceResultSet(new LinkedHashMap<>(),
                userAccumulator).values()).get(0);
      });
    } catch (IllegalStateException | NullPointerException | StatementException | IOException ex) {
      throw new DaoException("Unable to create the image: " + ex.getMessage(), ex);
    }
  }

  @Override
  public User read(String userId) throws DaoException {
    String sql = SELECT_USER_GIVEN_ID;
    try {
      return jdbi.inTransaction(handle -> new ArrayList<>(handle.
          createQuery(sql)
          .bind("userId", userId)
          .reduceResultSet(new LinkedHashMap<>(), userAccumulator)
          .values())
          .stream().findFirst().orElse(null));
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to read user given userId from the database: " + ex.getMessage(), ex);
    }
  }

  @Override
  public User update(String userId, User user) throws DaoException {
    String sql = "WITH updated AS (UPDATE jhused_user SET "
        + "name = :name, "
        + "email = :email, "
        + "profile_image = :profileImage, "
        + "location = :location "
        + "WHERE id = :userId RETURNING *) "
        + "SELECT * FROM updated;";
    try {
      if (user.getPosts() == null) {
        user.setPosts(new ArrayList<>());
      }
      if (user.getWishlist() == null) {
        user.setWishlist(new ArrayList<>());
      }
      return jdbi.inTransaction(handle -> {
        User updatedUser = handle.createQuery(sql)
            .bind("userId", userId)
            .bind("name", user.getName())
            .bind("email", user.getEmail())
            .bind("profileImage", user.getProfileImage())
            .bind("location", user.getLocation())
            .mapToBean(User.class).findOne().orElse(null);
        if (updatedUser != null) {
          List<Post> toDeletePost = postDao.readAllFromUser(user.getId());
          List<String> toDeletePostIds = new ArrayList<>();
          toDeletePost.forEach(p -> toDeletePostIds.add(p.getId()));
          List<Post> toAddPost = new ArrayList<>();
          for (Post post : user.getPosts()) {
            if (toDeletePost.contains(post)) {
              toDeletePostIds.remove(post.getId());
            } else {
              toAddPost.add(post);
            }
          }
          for (String postId : toDeletePostIds) {
            postDao.delete(postId);
          }
          for (Post post : toAddPost) {
            postDao.create(post);
          }

          List<WishlistPostSkeleton> toDeleteWishlistPost = wishlistPostSkeletonDao.readAll(userId);
          List<String> toDeleteWishlistPostIds = new ArrayList<>();
          toDeleteWishlistPost.forEach(p -> toDeleteWishlistPostIds.add(p.getPostId()));
          List<WishlistPostSkeleton> toAddWishlist = new ArrayList<>();
          for (Post wishlistPost: user.getWishlist()) {
            if (toDeleteWishlistPostIds.contains(wishlistPost.getId())) {
              toDeleteWishlistPost.remove(new WishlistPostSkeleton(wishlistPost.getId(), userId));
            }
            else {
              toAddWishlist.add(new WishlistPostSkeleton(wishlistPost.getId(), userId));
            }
          }

          for (WishlistPostSkeleton postSkeleton: toDeleteWishlistPost) {
            wishlistPostSkeletonDao.deleteWishlistEntry(postSkeleton.getPostId(), postSkeleton.getUserId());
          }

          for (WishlistPostSkeleton postSkeleton: toAddWishlist) {
            wishlistPostSkeletonDao.createWishListEntry(postSkeleton.getPostId(), postSkeleton.getUserId());
          }
        }
        return new ArrayList<>(handle.createQuery(SELECT_USER_GIVEN_ID).bind("userId", userId)
            .reduceResultSet(new LinkedHashMap<>(),
                userAccumulator).values()).get(0);
      });

    } catch (IllegalStateException | StatementException | NullPointerException ex) {
      throw new DaoException("Unable to update the hashtag: " + ex.getMessage(), ex);
    }
  }

  @Override
  public User delete(String id) throws DaoException {
    String sql = "WITH deleted AS ("
        + "DELETE FROM jhused_user WHERE id = :id RETURNING *"
        + ") SELECT * FROM deleted;";

    try {
      List<Post> posts = postDao.readAllFromUser(id);
      List<Post> wishlist = wishlistPostSkeletonDao.readAllWishlistEntries(id);
      return jdbi.inTransaction(handle -> {
        User user = handle.createQuery(sql).bind("id", id).mapToBean(User.class).findOne().orElse(null);
        if (user != null) {
          user.setPosts(posts);
          user.setWishlist(wishlist);
        }
        return user;
      });
    } catch (StatementException | IllegalStateException ex) { //otherwise, fail
      throw new DaoException("Unable to delete this user!", ex);
    }
  }

  @Override
  public List<User> readAll() throws DaoException {
    try {
      return jdbi.inTransaction(handle -> (List<User>) new ArrayList<User>(handle.createQuery(SELECT_ALL_USERS).reduceResultSet(new LinkedHashMap<>()
          , userAccumulator).values()));
    } catch (StatementException | IllegalStateException ex) {
      throw new DaoException("Unable to read all users", ex);
    }
  }
}
