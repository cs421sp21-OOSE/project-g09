package dao;

import exceptions.DaoException;
import java.util.List;

import model.Hashtag;

/**
 * Data Access Object for Hashtag.
 */
public interface HashtagDao {

  /**
   * Create a Hashtag.
   * @param hashtag The Hashtag item to be created
   * @return The Hashtag object created.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Hashtag create(Hashtag hashtag) throws DaoException;

  /**
   * Create a list of Hashtag items.
   * @param hashtags A list of Hashtag items to be created
   * @return A list of Hashtag objects created.
   * @throws DaoException A generic exception for CRUD operations.
   */
  List<Hashtag> create(List<Hashtag> hashtags) throws DaoException;

  /**
   * Read a Hashtag provided its id.
   *
   * @param id The Hashtag id
   * @return The Hashtag object read from the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Hashtag read(String id) throws DaoException;

  /**
   * Read all Hashtags from the database.
   *
   * @return All the Hashtags in the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
  List<Hashtag> readAll() throws DaoException;

  /**
   * No need to implement yet.
   * Read all Hashtags from the database with containing qery.
   *
   * @param hashtagQuery A search term.
   * @return All Hashtags retrieved.
   * @throws DaoException A generic exception for CRUD operations.
   */
  List<Hashtag> readAll(String hashtagQuery) throws DaoException;

  /**
   * Read all hashtags that match exactly but case insensitive
   * @param hashtagQuery
   * @return
   * @throws DaoException
   */
  List<Hashtag> readAllExactCaseInsensitive(String hashtagQuery) throws DaoException;

  /**
   * Update the title of a Hashtags provided its id.
   *
   * @param id The Hashtag id.
   * @param hashtag The Hashtag.
   * @return The updated Hashtag object.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Hashtag update(String id, Hashtag hashtag) throws DaoException;

  /**
   * No need to implement yet.
   * Delete a Hashtag provided its id.
   *
   * @param id The Hashtag id.
   * @return The Hashtag object deleted from the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
//  Hashtag delete(String id) throws DaoException;

  List<Hashtag> getHashtagsOfPost(String postId) throws DaoException;

  /**
   * Create or Update the Hashtag provided its id.
   *
   * @param hashtag The hashtag.
   * @return The created or updated hashtag object.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Hashtag createOrUpdate(String id, Hashtag hashtag) throws DaoException;

  /**
   * Create a Hashtag if not exist.
   * @param hashtag The Hashtag item to be created
   * @return The Hashtag object created.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Hashtag createIfNotExist(Hashtag hashtag) throws DaoException;
}
