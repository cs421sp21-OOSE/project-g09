package dao;

import exceptions.DaoException;
import model.User;

public interface UserDao {

  /**
   * Create an User.
   *
   * @param user The User item to be created
   * @return The User object created.
   * @throws DaoException A generic exception for CRUD operations.
   */
  User create(User user) throws DaoException;

  /**
   * Get an User with the userId
   * @param userId
   * @return The user matching the userId
   * @throws DaoException
   */
  User get(String userId) throws DaoException;
  
  /**
   * Update the title of a Users provided its id.
   *
   * @param user The User.
   * @return The updated User object.
   * @throws DaoException A generic exception for CRUD operations.
   */
  User update(String userId, User user) throws DaoException;
  
  /**
   * Delete a User provided its id.
   * This should never be called, as deleting post will automatically
   * delete its users.
   *
   * @param id The User id.
   * @return The User object deleted from the data source.
   * @throws DaoException A generic exception for CRUD operations.
   */
  User delete(String id) throws DaoException;
}
