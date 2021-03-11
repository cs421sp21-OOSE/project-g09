package model;
import lombok.Data;

@Data
public class Image {
  String imgId;
  String postId;
  String url;

  /**
   * Be sure that postId is consistent with the post that owns this image
   * @param imgId id of this image
   * @param postId id of the post that owns this image
   * @param url the image url
   */
  public Image(String imgId, String postId, String url) {
    this.imgId = imgId;
    this.postId = postId;
    this.url = url;
  }

  /**
   * postId is set by post when added to post
   * @param imgId id of the image
   * @param url url of the image
   */
  public Image(String imgId, String url) {
    this.imgId = imgId;
    this.url = url;
  }
}

