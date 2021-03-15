package dao;

import exceptions.DaoException;
import java.util.List;
import model.Image;

/**
 * Data Access Object for Image.
 */
public interface ImageDao {

  /**
   * Create a Image.
   * @param image The Image item to be created
   * @return The Image object created.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Image create(Image image) throws DaoException;

  /**
   * No need to implement yet.
   * Read a Image provided its id.
   *
   * @param id The Image id
   * @return The Image object read from the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
//  Image read(String id) throws DaoException;

  /**
   * No need to implement yet.
   * Read all Images from the database.
   *
   * @return All the Images in the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
//  List<Image> readAll() throws DaoException;

  /**
   * No need to implement yet.
   * Read all Images from the database with title containing titleQuery.
   *
   * @param titleQuery A search term.
   * @return All Images retrieved.
   * @throws DaoException A generic exception for CRUD operations.
   */
//  List<Image> readAll(String titleQuery) throws DaoException;

  /**
   * Update the title of a Images provided its id.
   *
   * @param image The Image.
   * @return The updated Image object.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Image update(Image image) throws DaoException;

  /**
   * Delete a Image provided its id.
   * This should never be called, as deleting post will automatically
   * delete its images.
   *
   * @param id The Image id.
   * @return The Image object deleted from the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
//  Image delete(String id) throws DaoException;
}
