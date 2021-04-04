package dao;

import exceptions.DaoException;
import model.Message;

import java.util.List;

public interface MessageDao {
  /**
   * Add one message to database.
   * @param message a list of message objects.
   * @return  message object added.
   * @throws DaoException
   */
  Message create(Message message) throws DaoException;

  /**
   * Add a list of messages.
   * @param messages a list of message objects.
   * @return a list of message objects added.
   * @throws DaoException
   */
  List<Message> create(List<Message> messages) throws DaoException;

  /**
   * read a message given message's id
   * @param id message's id
   * @return the message of that id
   * @throws DaoException
   */
  Message read(String id) throws DaoException;

  /**
   * read all messages
   * @return all messages in database
   * @throws DaoException
   */
  List<Message> readAll() throws DaoException;

  /**
   * return any messages that is sent or received by a user given user id.
   * @param userId user's id
   * @return a list of messages of that user
   * @throws DaoException
   */
  List<Message> readAllGivenSenderOrReceiverId(String userId) throws DaoException;

  /**
   * return any messages that is sent by a user given user id.
   * @param senderId  the user id of the sender
   * @return a list of messages of that sender
   * @throws DaoException
   */
  List<Message> readAllGivenSenderId(String senderId) throws DaoException;

  /**
   * return any messages that is received by a user given user id.
   * @param receiverId the user id of the receiver
   * @return a list of messages of the receiver
   * @throws DaoException
   */
  List<Message> readAllGivenReceiverId(String receiverId) throws DaoException;

  /**
   * update a message
   * @param id the id of the message to be updated
   * @param message the message to be updated
   * @return message updated
   * @throws DaoException
   */
  Message update(String id, Message message) throws DaoException;

  /**
   * delete a message
   * @param  id id of the message to be deleted
   * @return the deleted message
   * @throws DaoException
   */
  Message delete(String id) throws DaoException;

  /**
   * delete a list of messages
   * @param messages a list of messages to be deleted
   * @return a list of deleted messages
   * @throws DaoException
   */
  List<Message> delete(List<String> ids) throws DaoException;
}
