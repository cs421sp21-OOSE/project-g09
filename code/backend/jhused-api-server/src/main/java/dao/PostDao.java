package dao;

import exceptions.DaoException;
import java.util.List;
import model.Post;

/**
 * Data Access Object for Post.
 */
public interface PostDao {

  /**
   * Create a Post.
   * @param post The Post item to be created
   * @return The Post object created.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Post create(Post post) throws Exception;

  /**
   * Read a Post provided its id.
   *
   * @param id The Post alphanumeric code.
   * @return The Post object read from the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Post read(String id) throws DaoException;

  /**
   * Read all Posts from the database.
   *
   * @return All the Posts in the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
  List<Post> readAll() throws DaoException;

  /**
   * Read all Posts from the database with title containing titleQuery.
   *
   * @param titleQuery A search term.
   * @return All Posts retrieved.
   * @throws DaoException A generic exception for CRUD operations.
   */
  List<Post> readAll(String titleQuery) throws DaoException;

  /**
   * Update the title of a Posts provided its id.
   *
   * @param id The Post alphanumeric code.
   * @param post The Post.
   * @return The updated Post object.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Post update(String id, Post post) throws DaoException;

  /**
   * Delete a Posts provided its id.
   *
   * @param id The Post alphanumeric code.
   * @return The Post object deleted from the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Post delete(String id) throws DaoException;
}