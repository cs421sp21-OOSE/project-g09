package model;
import lombok.Data;

@Data
public class HashTag {
  String hashTagId;
  String postId;
  String hashTag;

  public HashTag(String hashTagId, String postId, String hashTag) {
    this.hashTagId = hashTagId;
    this.postId = postId;
    this.hashTag = hashTag;
  }
}
