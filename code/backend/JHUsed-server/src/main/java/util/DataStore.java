package util;

import model.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class with methods to create sample data.
 */
public final class DataStore {

  private DataStore() {
    // This class should not be instantiated.
  }

  /**
   * Create a list of sample CS courses.
   *
   * @return a list of sample CS courses.
   */
  public static List<Post> samplePosts() {
    List<Post> samples = new ArrayList<>();
    samples.add(new Post(0,
        "Dummy furniture",
        "Description of dummy furniture",
        "Location of dummy furniture",
        "/somedir/somesubdir/furniture_image.jpg"));
    samples.add(new Post(1,
        "Dummy TV",
        "Description of dummy TV",
        "Location of dummy TV",
        "/somedir/somesubdir/TV_image.jpg"));
    samples.add(new Post(2,
        "Dummy bed",
        "Description of dummy bed",
        "Location of dummy bed",
        "/somedir/somesubdir/bed_image.jpg"));
    samples.add(new Post(3,
        "Dummy desk",
        "Description of dummy desk",
        "Location of dummy desk",
        "/somedir/somesubdir/desk_image.jpg"));
    samples.add(new Post(4,
        "Dummy lamp",
        "Description of dummy lamp",
        "Location of dummy lamp",
        "/somedir/somesubdir/lamp_image.jpg"));
    samples.add(new Post(5,
        "Dummy cup",
        "Description of dummy cup",
        "Location of dummy cup",
        "/somedir/somesubdir/cup_image.jpg"));
    samples.add(new Post(6,
        "Dummy car",
        "Description of dummy car",
        "Location of dummy car",
        "/somedir/somesubdir/car_image.jpg"));
    return samples;
  }
}
