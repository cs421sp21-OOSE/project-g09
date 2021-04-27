package dao;

import exceptions.DaoException;
import model.PostVisit;

public interface PostVisitDao {

  /**
   * create a PostVisit entry in database
   * @param postVisit the PostVisit
   * @return the created PostVisit
   * @throws DaoException
   */
  PostVisit create(PostVisit postVisit) throws DaoException;

  /**
   * read a PostVisit given keys
   * @param postId the post's id
   * @param userId the user's id
   * @return the PostVisit, return null if has not visited
   * @throws DaoException
   */
  PostVisit read(String postId, String userId) throws DaoException;

  /**
   * count visits of a post
   * @param postId the post's id
   * @return the number of visits of unique users to this post
   * @throws DaoException
   */
  int visitCount(String postId) throws DaoException;
}