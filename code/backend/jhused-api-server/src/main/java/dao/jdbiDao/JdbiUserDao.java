package dao.jdbiDao;

import dao.PostDao;
import dao.UserDao;
import exceptions.DaoException;
import model.Hashtag;
import model.Image;
import model.Post;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class JdbiUserDao implements UserDao {
  private final Jdbi jdbi;
  private final PostDao postDao;
  private final String defaultProfileImage = "https://i.redd.it/v2h2px8w5piz.png";

  public JdbiUserDao(Jdbi jdbi) {
    this.jdbi = jdbi;
    this.postDao = new JdbiPostDao(jdbi);
  }

  @Override
  public User create(User user) throws DaoException {

    String sql = "WITH inserted AS ("
        + "INSERT INTO jhused_user(id, name, email, profile_image, location) "
        + "VALUES(:id, :name, :email, :profileImage, :location) RETURNING *"
        + ")SELECT * FROM inserted;";
    if (user.getProfileImage() == null || user.getProfileImage().isEmpty()) {
      user.setProfileImage(defaultProfileImage);
    }
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql).bindBean(user).mapToBean(User.class).findOne()).orElse(null);
    } catch (IllegalStateException | NullPointerException | StatementException ex) {
      throw new DaoException("Unable to create the image: " + ex.getMessage(), ex);
    }
  }

  @Override
  public User get(String userId) throws DaoException {
    String sql = "SElECT jhused_user.* FROM jhused_user "
        + "LEFT JOIN post on jhused_user.id = post.user_id "
        + "WHERE jhused_user.id = :userId";
    try {
      return jdbi.inTransaction(handle -> handle.select(sql).bind("userId", userId).mapToBean(User.class)
          .findOne()).orElse(null);
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to read user given postId from the database: " + ex.getMessage(), ex);
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
      if (user.getPostList() == null) {
        user.setPostList(new ArrayList<>());
      }
      return jdbi.inTransaction(handle -> {
        User updatedUser = handle.createQuery(sql)
            .bind("id", userId)
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
          for (Post post : user.getPostList()) {
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
        return get(userId);
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

    //attempt to open connection and perform sql string.
    try {
      List<Post> posts = postDao.readAllFromUser(id);
      return jdbi.inTransaction(handle -> {
        User user = handle.createQuery(sql).bind("id", id).mapToBean(User.class).findOne().orElse(null);
        if (user != null) {
          user.setPostList(posts);
        }
        return user;
      });
    } catch (StatementException | IllegalStateException ex) { //otherwise, fail
      throw new DaoException("Unable to delete this user!", ex);
    }
  }
}
