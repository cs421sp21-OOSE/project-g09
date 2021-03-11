package model;
import lombok.Data;

@Data
public class Hashtag {
  String hashtagId;
  String hashtag;

  public Hashtag(String hashtagId, String hashtag) {
    this.hashtagId = hashtagId;
    this.hashtag = hashtag;
  }
}
