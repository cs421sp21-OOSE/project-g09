package dao;

import exceptions.DaoException;

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
}
