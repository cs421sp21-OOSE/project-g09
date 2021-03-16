package model;
import lombok.Data;
import org.simpleflatmapper.map.annotation.Key;

@Data
public class Hashtag {
  @Key
  private String id;
  private String hashtag;

  public Hashtag(String id, String hashtag) {
    this.id = id;
    this.hashtag = hashtag;
  }
}
