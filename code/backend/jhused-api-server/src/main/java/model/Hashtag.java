package model;
import lombok.Data;

@Data
public class Hashtag {
  private String id;
  private String hashtag;

  public Hashtag(String id, String hashtag) {
    this.id = id;
    this.hashtag = hashtag;
  }
  public Hashtag(){}
}
