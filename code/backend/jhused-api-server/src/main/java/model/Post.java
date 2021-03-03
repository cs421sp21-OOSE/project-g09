package model;

import lombok.Data;

import java.util.List;


// frontend has form, it receive user's post information convert it to a json, sends the json to backend using post
// we receive the post request, use gson to convert the json to Post.

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

  public Post(String uuid, String title, Double price, Category category, String location) {
    this.uuid = uuid;
    this.title = title;
    this.price = price;
    this.category = category;
    this.location = location;
  }

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
}
