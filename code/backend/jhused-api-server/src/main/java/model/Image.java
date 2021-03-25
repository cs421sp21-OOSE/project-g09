package model;
import lombok.Data;

@Data
public class Image {
  private String id;
  private String postId;
  private String url;

  /**
   * Be sure that postId is consistent with the post that owns this image
   * @param id id of this image
   * @param postId id of the post that owns this image
   * @param url the image url
   */
  public Image(String id, String postId, String url) {
    this.id = id;
    this.postId = postId;
    this.url = url;
  }

  /**
   * postId is set by post when added to post
   * @param id id of the image
   * @param url url of the image
   */
  public Image(String id, String url) {
    this.id = id;
    this.url = url;
  }

  public Image(){}
}

