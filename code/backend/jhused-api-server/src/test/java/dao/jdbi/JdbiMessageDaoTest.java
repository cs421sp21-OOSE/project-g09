package dao.jdbi;

import dao.MessageDao;
import dao.jdbiDao.JdbiMessageDao;
import exceptions.DaoException;
import model.Message;
import model.User;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.database.DataStore;
import util.database.Database;

import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JdbiMessageDaoTest {
  private static final List<Message> samples = DataStore.sampleMessages();
  private static final List<User> sampleUsers = DataStore.sampleUsers();
  private static Jdbi jdbi;
  private MessageDao messageDao;

  @BeforeAll
  static void connectToDatabase() throws URISyntaxException {
    Database.USE_TEST_DATABASE = true; // use test dataset
    Database.main(null); // reset dataset and add samples
    jdbi = Database.getJdbi();
  }

  @BeforeEach
  void injectDependency() throws URISyntaxException {
    Database.truncateTable(jdbi, "message");
    Database.insertSampleMessages(jdbi, samples);
    messageDao = new JdbiMessageDao(jdbi);
  }

  @AfterAll
  static void setUseProductionDatabase() {
    Database.USE_TEST_DATABASE = false; // use production dataset
  }

  @Test
  void doNothing(){

  }

  @Test
  void createWorks() {
    Message message = new Message(UUID.randomUUID().toString(), sampleUsers.get(3).getId(), sampleUsers.get(2).getId(),
        "test message".repeat(333), true, Instant.now());
    assertEquals(message, messageDao.create(message));
    message = new Message(UUID.randomUUID().toString(), sampleUsers.get(3).getId(), sampleUsers.get(2).getId(),
        "test message".repeat(333), true);
    assertEquals(message, messageDao.create(message));
    message = new Message(UUID.randomUUID().toString(), sampleUsers.get(3).getId(), sampleUsers.get(2).getId(),
        "test message".repeat(333));
    assertEquals(message, messageDao.create(message));
    message = new Message(null, sampleUsers.get(3).getId(), sampleUsers.get(2).getId(),
        "test message".repeat(333));
    Message res = messageDao.create(message);
    res.setId(message.getId());
    assertEquals(message, res);
  }

  @Test
  void createAListOfMessageWorks() {
    Database.truncateTable(jdbi, "message");
    assertEquals(samples, messageDao.create(samples));
  }

  @Test
  void createMessageWithNonExistingUserIdThrowsDaoException() {
    Message message = new Message(UUID.randomUUID().toString(), sampleUsers.get(3).getId(), "ksjdfkj",
        "test message".repeat(333), true, Instant.now());
    Message finalMessage = message;
    assertThrows(DaoException.class, () -> messageDao.create(finalMessage));
    message = new Message(UUID.randomUUID().toString(), "kjihwhh", sampleUsers.get(3).getId(),
        "test message".repeat(333), true, Instant.now());
    Message finalMessage1 = message;
    assertThrows(DaoException.class, () -> messageDao.create(finalMessage1));
    message = new Message(UUID.randomUUID().toString(), "kjihwhh", "kfiooo",
        "test message".repeat(333), true, Instant.now());
    Message finalMessage2 = message;
    assertThrows(DaoException.class, () -> messageDao.create(finalMessage2));
  }

  @Test
  void createDuplicateMessageThrowsDaoException() {
    assertThrows(DaoException.class, () -> messageDao.create(samples.get(0)));
  }

  @Test
  void createMessageWithNullMessageThrowsDaoException() {
    Message message = new Message(UUID.randomUUID().toString(), sampleUsers.get(3).getId(), samples.get(2).getId(),
        null, true, Instant.now());
    assertThrows(DaoException.class, () -> messageDao.create(message));
  }

  @Test
  void updateWorks() {
    Message message = new Message(null, samples.get(0).getSenderId(), samples.get(0).getReceiverId(),
        "jjjhhhsksisisjjij", true);
    Message res = messageDao.update(samples.get(0).getId(), message);
    message.setId(res.getId());
    message.setSenderId(res.getSenderId());
    message.setReceiverId(res.getReceiverId());
    assertEquals(message, res);
    message = new Message(null, null, null,
        "jjjhhhsksisisjjij", false);
    res = messageDao.update(samples.get(0).getId(), message);
    message.setId(res.getId());
    message.setSenderId(res.getSenderId());
    message.setReceiverId(res.getReceiverId());
    assertEquals(message, res);
    message = new Message(null, null, null,
        "j000jjhhhsksisisjjij", false);
    res = messageDao.update(samples.get(0).getId(), message);
    message.setId(res.getId());
    message.setSenderId(res.getSenderId());
    message.setReceiverId(res.getReceiverId());
    assertEquals(message, res);
  }

  @Test
  void updateReturnNullNonExistingId() {
    Message message = new Message(null, samples.get(0).getSenderId(), samples.get(0).getReceiverId(),
        "jjjhhhsksisisjjij", true);
    assertNull(messageDao.update(null, message));
    assertNull(messageDao.update("jjj", message));
  }

  @Test
  void readWorks() {
    for (Message message : samples) {
      assertEquals(message, messageDao.read(message.getId()));
    }
  }

  @Test
  void readNonExistingIdThrowsDaoException() {
    assertNull(messageDao.read(null));
    assertNull(messageDao.read("jjjdiiiosos"));
  }

  @Test
  void readAllWorks() {
    assertEquals(samples, messageDao.readAll());
  }

  @Test
  void readAllGivenSenderIdWorks() {
    Map<String, List<Message>> senderIds = new LinkedHashMap<>();
    for (Message message : samples) {
      if (senderIds.get(message.getSenderId()) == null) {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        senderIds.put(message.getSenderId(), messages);
      } else {
        senderIds.get(message.getSenderId()).add(message);
      }
    }
    for (Map.Entry<String, List<Message>> entry : senderIds.entrySet()) {
      assertEquals(entry.getValue(), messageDao.readAllGivenSenderId(entry.getKey()));
    }
  }

  @Test
  void readAllGivenReceiverIdWorks() {
    Map<String, List<Message>> receiverIds = new LinkedHashMap<>();
    for (Message message : samples) {
      if (receiverIds.get(message.getReceiverId()) == null) {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        receiverIds.put(message.getReceiverId(), messages);
      } else {
        receiverIds.get(message.getReceiverId()).add(message);
      }
    }
    for (Map.Entry<String, List<Message>> entry : receiverIds.entrySet()) {
      assertEquals(entry.getValue(), messageDao.readAllGivenReceiverId(entry.getKey()));
    }
  }

  @Test
  void readAllGivenUserIdWorks() {
    Map<String, List<Message>> userIds = new LinkedHashMap<>();
    for (Message message : samples) {
      if (userIds.get(message.getReceiverId()) == null) {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        userIds.put(message.getReceiverId(), messages);
      } else {
        userIds.get(message.getReceiverId()).add(message);
      }
      if (userIds.get(message.getSenderId()) == null) {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        userIds.put(message.getSenderId(), messages);
      } else if(!userIds.get(message.getSenderId()).contains(message)){
        userIds.get(message.getSenderId()).add(message);
      }
    }
    for (Map.Entry<String, List<Message>> entry : userIds.entrySet()) {
      assertEquals(entry.getValue(), messageDao.readAllGivenSenderOrReceiverId(entry.getKey()));
    }
  }

  @Test
  void deleteWorks() {
    for (Message message : samples) {
      assertEquals(message, messageDao.delete(message.getId()));
    }
  }

  @Test
  void deleteAListWorks() {
    List<String> ids = new ArrayList<>();
    for (Message message : samples) ids.add(message.getId());
    assertEquals(samples, messageDao.delete(ids));
  }
}
