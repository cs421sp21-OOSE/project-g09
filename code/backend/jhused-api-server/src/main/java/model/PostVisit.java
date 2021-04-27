package model;

import lombok.Data;

@Data
public class PostVisit {
  private String userId;
  private String postId;

  public PostVisit() {
  }

  public PostVisit(String userId, String postId) {
    this.userId = userId;
    this.postId = postId;
  }
}