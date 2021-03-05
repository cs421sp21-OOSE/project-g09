package model;

import java.util.Objects;
import lombok.Data;

import java.util.List;


// frontend has form, it receive user's post information convert it to a json, sends the json to backend using post
// we receive the post request, use gson to convert the json to Post.

/**
 * Model Post
 *
 */
@Data
public class Post {
  private String uuid;  // must have
  private String userId;  // don't need to have
  private String title;   //must have
  private Double price;   //must have
  private String description;   // don't need to have
  private List<String> imageUrls;   // don't need to have
  private List<String> hashtags;    // don't need to have
  private Category category;  // must have
  private String location;  // must have

  /**
   * Constructor for Post.
   * This constructor is made basically for testing.
   * @param title
   */
  public Post(String title)
  {
    this.title=title;
  }

  /**
   * Constructor for Post.
   * All parameters are NOT NULL fields.
   * @param uuid UUID should be generated, it should always be in length 36. It is the primary key for Post.
   * @param title title of the post.
   * @param price price of the post, it is stored as Numeric(12, 2) in PostgreSQL.
   *              Meaning, it should have 12 valid digits and 2 digit precision after decimal point.
   * @param category enum, represent category.
   * @param location location of the post.
   */
  public Post(String uuid, String title, Double price, Category category, String location) {
    this.uuid = uuid;
    this.title = title;
    this.price = price;
    this.category = category;
    this.location = location;
  }

  /**
   * Constructor for Post.
   * This one has parameter for all fields
   * @param uuid UUID should be generated, it should always be in length 36. It is the primary key for Post.
   * @param title title of the post.
   * @param price price of the post, it is stored as Numeric(12, 2) in PostgreSQL.
   *              Meaning, it should have 12 valid digits and 2 digit precision after decimal point.
   * @param description description may not exceeds 1000 characters.
   * @param imageUrls a list of image urls.
   * @param hashTags a list of hashtags.
   * @param category enum, represents category.
   * @param location location of the post.
   */
  public Post(String uuid, String userId, String title, Double price, String description, List<String> imageUrls,
              List<String> hashTags, Category category, String location) {
    this.uuid = uuid;
    this.userId = userId;
    this.title = title;
    this.price = price;
    this.description = description;
    this.imageUrls = imageUrls;
    this.hashtags = hashTags;
    this.category = category;
    this.location = location;
  }
//  No need to add getter and setter function as lombok automated these

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
        .equals(description, post.description) && Objects.equals(imageUrls, post.imageUrls)
        && Objects.equals(hashtags, post.hashtags) && category == post.category
        && Objects.equals(location, post.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, title, price, description, imageUrls, hashtags, category, location);
  }
}
