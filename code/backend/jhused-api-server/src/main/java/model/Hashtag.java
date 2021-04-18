package model;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Hashtag {
  @EqualsAndHashCode.Exclude
  private String id;
  private String hashtag;

  public Hashtag(String id, String hashtag) {
    this.id = id;
    this.hashtag = hashtag;
  }
  public Hashtag(){}
}
