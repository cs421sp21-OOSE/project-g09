package model;

import java.util.Objects;

/**
 * Represent a post
 */
public class Post {

  private int id;
  private String description;
  private String title;
  private String location;
  private String image;

  /**
   * Full constructor
   *
   * @param id       id of the post
   * @param title    title of the post
   * @param location location of the post, description at the moment
   * @param image    image of the post, could be the path stored in server
   */
  public Post(int id, String title, String location, String image) {
    this.id = id;
    this.title = title;
    this.location = location;
    this.image = image;
  }

  /**
   * getter for description
   *
   * @return description
   */
  public String getDescription() {
    return description;
  }

  /**
   * getter for title
   *
   * @return title
   */
  public String getTitle() {
    return title;
  }

  /**
   * getter for location
   *
   * @return location
   */
  public String getLocation() {
    return location;
  }

  /**
   * getter for image paths
   *
   * @return image
   */
  public String getImage() {
    return image;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Post post = (Post) o;
    return id == post.id;
  }

  @Override
  public String toString() {
    return id + " " + title;
  }
}
