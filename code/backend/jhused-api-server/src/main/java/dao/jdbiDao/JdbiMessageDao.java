package dao.jdbiDao;

import dao.MessageDao;
import exceptions.DaoException;
import model.Message;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.core.statement.StatementException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbiMessageDao implements MessageDao {
  private Jdbi jdbi;

  public JdbiMessageDao(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Message create(Message message) throws DaoException {
    String sql = "WITH inserted AS("
        + "INSERT INTO message(id, sender_id, receiver_id, message, read, sent_time) "
        + "VALUES(:id, :senderId, :receiverId, :message, :read, :sentTime) RETURNING *) "
        + "SELECT * FROM inserted;";
    try {
      if (message != null && (message.getId() == null || message.getId().length() != 36)) {
        message.setId(UUID.randomUUID().toString());
      }
      return jdbi.inTransaction(handle -> handle.createQuery(sql).bindBean(message).mapToBean(Message.class)
          .findOne()
          .orElse(null));
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to create the message: " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Message> create(List<Message> messages) throws DaoException {
    String sql = "INSERT INTO message(id, sender_id, receiver_id, message, read, sent_time) "
        + "VALUES(:id, :senderId, :receiverId, :message, :read, :sentTime);";
    try {
      List<Message> res;
      if (messages.isEmpty()) {
        res = new ArrayList<>();
      } else {
        res = jdbi.inTransaction(handle -> {
          PreparedBatch batch = handle.prepareBatch(sql);
          for (Message message : messages) {
            if (message != null && (message.getId() == null || message.getId().length() != 36)) {
              message.setId(UUID.randomUUID().toString());
            }
            batch.bindBean(message).add();
          }
          return batch.executeAndReturnGeneratedKeys().mapToBean(Message.class).list();
        });
      }
      return res;
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to create the messages: " + ex.getMessage(), ex);
    }
  }

  @Override
  public Message read(String id) throws DaoException {
    String sql = "SELECT * FROM message WHERE message.id=:id;";
    try {
      return jdbi.inTransaction(handle -> handle.createQuery(sql).bind("id", id).mapToBean(Message.class)
          .findOne().orElse(null));
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to read the message: " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Message> readAll() throws DaoException {
    String sql = "SELECT * FROM message;";
    try {
      return jdbi.inTransaction(handle -> handle.createQuery(sql).mapToBean(Message.class)
          .list());
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to read the messages: " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Message> readAllGivenSenderOrReceiverId(String userId) throws DaoException {
    String sql = "SELECT * FROM message WHERE message.sender_id = :userId OR message.receiver_id = :userId;";
    try {
      return jdbi.inTransaction(handle -> handle.createQuery(sql).bind("userId", userId)
          .mapToBean(Message.class).list());
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to read the messages given userId:" + userId + " : " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Message> readAllGivenSenderId(String senderId) throws DaoException {
    String sql = "SELECT * FROM message WHERE message.sender_id = :userId;";
    try {
      return jdbi.inTransaction(handle -> handle.createQuery(sql).bind("userId", senderId)
          .mapToBean(Message.class).list());
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to read the messages given senderId:" + senderId + " : " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Message> readAllGivenReceiverId(String receiverId) throws DaoException {
    String sql = "SELECT * FROM message WHERE message.receiver_id = :userId;";
    try {
      return jdbi.inTransaction(handle -> handle.createQuery(sql).bind("userId", receiverId)
          .mapToBean(Message.class).list());
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to read the messages given receiverId:" + receiverId + " : " + ex.getMessage(),
          ex);
    }
  }

  @Override
  public Message update(String id, Message message) throws DaoException {
    String sql = "WITH updated AS ("
        + "UPDATE message SET message = :message, read = :read WHERE message.id = :id RETURNING *)"
        + "SELECT * FROM updated;";
    try {
      return jdbi.inTransaction(handle -> handle.createQuery(sql).bind("message", message.getMessage())
          .bind("read", message.getRead()).bind("id", id)
          .mapToBean(Message.class).findOne().orElse(null));
    } catch (IllegalStateException | NullPointerException | StatementException ex) {
      throw new DaoException("Unable to update the message: " + ex.getMessage(), ex);
    }
  }

  @Override
  public Message delete(String id) throws DaoException {
    String sql = "WITH deleted AS ("
        + "DELETE FROM message WHERE message.id=:id RETURNING *)"
        + "SELECT * FROM deleted;";
    try {
      return jdbi.inTransaction(handle ->
          handle.createQuery(sql).bind("id", id).mapToBean(Message.class).findOne().orElse(null));
    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to delete the message with id: " + id
          + " error message: " + ex.getMessage(), ex);
    }
  }

  @Override
  public List<Message> delete(List<String> ids) throws DaoException {
    String sql = "DELETE FROM message WHERE message.id=:id;";
    try {
      return jdbi.inTransaction(handle -> {
        List<Message> res;
        if (ids.isEmpty()) {
          res = new ArrayList<>();
        } else {
          PreparedBatch batch = handle.prepareBatch(sql);
          for (String id : ids) {
            batch.bind("id", id).add();
          }
          res = batch.executeAndReturnGeneratedKeys().mapToBean(Message.class).list();
        }
        return res;
      });

    } catch (IllegalStateException | StatementException ex) {
      throw new DaoException("Unable to delete the messages: "
          + " error message: " + ex.getMessage(), ex);
    }
  }
}
