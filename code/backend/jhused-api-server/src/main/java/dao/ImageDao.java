package dao;

import exceptions.DaoException;
import model.Image;

import java.util.List;

/**
 * Data Access Object for Image.
 */
public interface ImageDao {

  /**
   * Create a Image.
   *
   * @param image The Image item to be created
   * @return The Image object created.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Image create(Image image) throws DaoException;

  /**
   * Create a list of Image.
   *
   * @param images A list of Image items to be created
   * @return A list of Image objects created.
   * @throws DaoException A generic exception for CRUD operations.
   */
  List<Image> create(List<Image> images) throws DaoException;

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
  Image update(String id, Image image) throws DaoException;

  /**
   * Delete a Image provided its id.
   * This should never be called, as deleting post will automatically
   * delete its images.
   *
   * @param id The Image id.
   * @return The Image object deleted from the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Image delete(String id) throws DaoException;

  /**
   * Delete a a list of Images provided their ids.
   *
   * @param ids a list of image ids.
   * @return The Image object deleted from the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
  List<Image> delete(List<String> ids) throws DaoException;

  /**
   * Get all images belonging to the post with the given postId
   * @param postId of post
   * @return list of images from the post
   * @throws DaoException A generic exception for CRUD operations.
   */
  List<Image> getImagesOfPost(String postId) throws DaoException;

  /**
   * Create or Update the Images provided its id.
   *
   * @param image The Image.
   * @return The updated Image object.
   * @throws DaoException A generic exception for CRUD operations.
   */
  Image createOrUpdate(String id, Image image) throws DaoException;
}
