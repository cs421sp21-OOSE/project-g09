package model;

import lombok.Data;

@Data
public class Message {
  private String id;
  private String senderId;
  private String receiverId;
  private String message;
  private boolean read; // has the message been read or not.

  public Message() {
  }

  public Message(String id, String senderId, String receiverId, String message, boolean read) {
    this.id = id;
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.message = message;
    this.read = read;
  }
}
