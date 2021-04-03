package model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
public class Message {
  private String id;
  private String senderId;
  private String receiverId;
  private String message;
  private Boolean read; // has the message been read or not.
  @EqualsAndHashCode.Exclude
  private Instant sentTime;

  public Message() {
  }

  public Message(String id, String senderId, String receiverId, String message, Boolean read, Instant sentTime) {
    this(id, senderId, receiverId, message, read);
    this.sentTime = sentTime;
  }

  public Message(String id, String senderId, String receiverId, String message, Boolean read) {
    this(id, senderId, receiverId, message);
    this.read = read;
  }

  public Message(String id, String senderId, String receiverId, String message) {
    this.id = id;
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.message = message;
    this.read = false;
  }
}
