package dao;

import exceptions.DaoException;

import java.util.List;
import java.util.Map;

public interface PostHashtagDao {

  /**
   * Create a post_hashtag item in database.
   * @param postId Id of the post
   * @param hashtagId Id of the hashtag
   * @return
   * @throws DaoException
   */
  Map<String, String> create(String postId, String hashtagId) throws DaoException;

  /**
   * Create post_hashtag items in database.
   * @param postIds Ids of the post
   * @param hashtagIds Ids of the hashtag
   * @return
   * @throws DaoException
   */
  List<Map<String, String>> create(List<String> postIds, List<String> hashtagIds) throws DaoException;

  /**
   * Create post_hashtag items in database.
   * @param postId Id of the post
   * @param hashtagIds Ids of the hashtag
   * @return
   * @throws DaoException
   */
  List<Map<String, String>> create(String postId, List<String> hashtagIds) throws DaoException;

  /**
   * Delete a list of post's relationship with a list of hashtags.
   * @param postIds  a list of post id
   * @param hashtagIds a list of hashtag ids
   * @return deleted pairs
   * @throws DaoException
   */
  List<Map<String, String>> delete(List<String> postIds, List<String> hashtagIds) throws DaoException;

  /**
   * Delete a post's relationship with a list of hashtags.
   * @param postId post id
   * @param hashtagIds a list of hashtag ids
   * @return deleted pairs
   * @throws DaoException
   */
  List<Map<String, String>> delete(String postId, List<String> hashtagIds) throws DaoException;
}
