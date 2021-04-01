package dao.jdbiDao;

import dao.PostDao;
import dao.UserDao;
import exceptions.DaoException;
import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementException;
import util.jdbiResultSetHandler.ResultSetLinkedHashMapAccumulatorProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class JdbiUserDao implements UserDao {
  private final Jdbi jdbi;
  private final PostDao postDao;
  private final ResultSetLinkedHashMapAccumulatorProvider<User> userAccumulator;
  private final String SELECT_USER_BASIC =
      "SElECT jhused_user.*,"
          + "post.id as posts_id, "
          + "post.user_id as posts_user_id, "
          + "post.title as posts_title, "
          + "post.price as posts_price, "
          + "post.sale_state as posts_sale_state, "
          + "post.description as posts_description, "
          + "post.category as posts_category, "
          + "post.location as posts_location, "
          + "post.create_time as posts_create_time, "
          + "post.update_time as posts_update_time, "
          + "image.id as posts_images_id, "
          + "image.url as posts_images_url,"
          + "image.post_id as posts_images_post_id, "
          + "hashtag.id as posts_hashtags_id, "
          + "hashtag.hashtag as posts_hashtags_hashtag "
          + "FROM jhused_user "
          + "LEFT JOIN post ON jhused_user.id = post.user_id "
          + "LEFT JOIN image ON image.post_id = post.id "
          + "LEFT JOIN post_hashtag ON post_hashtag.post_id = post.id "
          + "LEFT JOIN hashtag ON hashtag.id = post_hashtag.hashtag_id ";

  private final String SELECT_USER_GIVEN_ID = SELECT_USER_BASIC
      + "WHERE jhused_user.id = :userId;";

  private final String SELECT_ALL_USERS = SELECT_USER_BASIC;

  private final String defaultProfileImage = "https://i.redd.it/v2h2px8w5piz.png";

  public JdbiUserDao(Jdbi jdbi) {
    this.jdbi = jdbi;
    this.postDao = new JdbiPostDao(jdbi);
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
    try {
      return jdbi.inTransaction(handle -> {
        handle.createUpdate(sql).bindBean(user).execute();
        if (!user.getPosts().isEmpty()) {
          for (Post post : user.getPosts())
            postDao.create(post);
        }
        return new ArrayList<>(handle.createQuery(SELECT_USER_GIVEN_ID).bind("userId", user.getId())
            .reduceResultSet(new LinkedHashMap<>(),
                userAccumulator).values()).get(0);
      });
    } catch (IllegalStateException | NullPointerException | StatementException ex) {
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
            if (toDeletePost.contains(post.getId())) {
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
      return jdbi.inTransaction(handle -> {
        User user = handle.createQuery(sql).bind("id", id).mapToBean(User.class).findOne().orElse(null);
        if (user != null) {
          user.setPosts(posts);
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
