package model;

import lombok.Data;
import org.simpleflatmapper.map.annotation.Key;

import java.time.Instant;
import java.util.List;
import java.util.Objects;


// frontend has form, it receive user's post information convert it to a json, sends the json to backend using post
// we receive the post request, use gson to convert the json to Post.

/**
 * Model Post
 */
@Data
public class Post {
  @Key
  private String id;  // must have
  private String userId;  // don't need to have
  private String title;   //must have
  private Double price;   //must have
  private String description;   // don't need to have
  private List<Image> images;   // don't need to have
  private List<Hashtag> hashtags;    // don't need to have
  private Category category;  // must have
  private String location;  // must have
  private Instant createTime;
  private Instant updateTime;

  /**
   * Constructor for Post.
   * This constructor is made basically for testing.
   *
   * @param title
   */
  public Post(String title) {
    this.title = title;
  }

  /**
   * Constructor for Post.
   * All parameters are NOT NULL fields.
   *
   * @param id     UUID should be generated, it should always be in length 36. It is the primary key for Post.
   * @param title    title of the post.
   * @param price    price of the post, it is stored as Numeric(12, 2) in PostgreSQL.
   *                 Meaning, it should have 12 valid digits and 2 digit precision after decimal point.
   * @param category enum, represent category.
   * @param location location of the post.
   */
  public Post(String id, String title, Double price, Category category, String location) {
    this.id = id;
    this.title = title;
    this.price = price;
    this.category = category;
    this.location = location;
  }

  /**
   * Constructor for Post.
   * This one has parameter for all fields
   *
   * @param id        UUID should be generated, it should always be in length 36. It is the primary key for Post.
   * @param title       title of the post.
   * @param price       price of the post, it is stored as Numeric(12, 2) in PostgreSQL.
   *                    Meaning, it should have 12 valid digits and 2 digit precision after decimal point.
   * @param description description may not exceeds 1000 characters.
   * @param images      a list of image urls.
   * @param hashtags    a list of hashtags.
   * @param category    enum, represents category.
   * @param location    location of the post.
   */
  public Post(String id, String userId, String title, Double price, String description, List<Image> images,
              List<Hashtag> hashtags, Category category, String location) {
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.price = price;
    this.description = description;
    this.images = images;
    if(images!=null)
    {
      for (Image image : images) {
        image.setPostId(this.id);
      }
    }
    this.hashtags = hashtags;
    this.category = category;
    this.location = location;
  }
//  No need to add getter and setter function as lombok automated these

  public void setImages(List<Image> images) {
    for (Image image : images) {
      image.setPostId(this.id);
    }
    this.images = images;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Post)) {
      return false;
    }
    Post post = (Post) o;
    return Objects.equals(userId, post.userId) && Objects
        .equals(title, post.title) && Objects.equals(price, post.price) && Objects
        .equals(description, post.description) && Objects.equals(images, post.images)
        && Objects.equals(hashtags, post.hashtags) && category == post.category
        && Objects.equals(location, post.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, title, price, description, images, hashtags, category, location);
  }
}
