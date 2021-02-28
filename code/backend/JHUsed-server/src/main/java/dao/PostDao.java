package dao;

import exception.DaoException;
import model.Post;

import java.util.List;

/**
 * Data Access Object for model.Post.
 */
public interface PostDao {

  /**
   * Create a post.
   *
   * @param id The post id.
   * @param title The post Title.
   * @return The post object created.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Post create(int id, String title, String description, String location, String image) throws DaoException;

  /**
   * Read a post provided its offeringName.
   *
   * @param id The post id.
   * @return The post object read from the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Post read(int id) throws DaoException;

  /**
   * Read all posts from the database.
   *
   * @return All the posts in the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
  List<Post> readAll() throws DaoException;

  /**
   * Read all posts from the database with title containing titleQuery.
   *
   * @param idQuery A search term.
   * @return All posts retrieved.
   * @throws DaoException A generic exception for CRUD operations.
   */
  List<Post> readAll(int idQuery) throws DaoException;

  /**
   * Update the title of a posts provided its offeringName.
   *
   * @param id The post id.
   * @param title The post Title.
   * @return The updated post object.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Post update(int id, String title, String description, String location, String image) throws DaoException;

  /**
   * Delete a posts provided its offeringName.
   *
   * @param id The post alphanumeric code.
   * @return The post object deleted from the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Post delete(int id) throws DaoException;
}
