package model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;


@Data
public class Post {
  String id;
  String posterId;
  String title;
  Float price;
  String description;
  List<String> images;
  String location;

  public Post() {
    this.images = new ArrayList<>();
  }

  public Post(String id, String title) {
    this.id = id;
    this.title = title;
    this.images = new ArrayList<>();
  }

  public Post(String id, String posterId, String title, Float price, String description, List<String> images,
      String location) {
    this.id = id;
    this.posterId = posterId;
    this.title = title;
    this.price = price;
    this.description = description;
    this.images = images;
    this.location = location;
  }
//  No need to add getter and setter function as lombok automated these
}
