package model;
import lombok.Data;

@Data
public class Image {
  String imgId;
  String postId;
  String url;

  public Image(String imgId, String postId, String url) {
    this.imgId = imgId;
    this.postId = postId;
    this.url = url;
  }
}

