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
    List<String> images = new ArrayList<>();
    images.add("/somedir/somesubdir/furniture_image.jpg");
    samples.add(new Post("0", "001",
        "Dummy furniture", 30F,
        "Description of dummy furniture",
        images,
        "Location of dummy furniture"
        ));
    samples.add(new Post("1", "002",
        "Dummy TV", 40F,
        "Description of dummy TV",
        images,
        "Location of dummy TV"
        ));
    samples.add(new Post("3", "003",
        "Dummy bed", 50F,
        "Description of dummy bed",
        images,
        "Location of dummy bed"
        ));
    samples.add(new Post("4", "004",
        "Dummy desk", 29.99F,
        "Description of dummy desk",
        images,
        "Location of dummy desk"
        ));
    samples.add(new Post("5", "005",
        "Dummy lamp", 29.99F,
        "Description of dummy lamp",
        images,
        "Location of dummy lamp"
        ));
    samples.add(new Post("6","005",
        "Dummy cup", 29.99F,
        "Description of dummy cup",
        images,
        "Location of dummy cup"
        ));
    samples.add(new Post("7","005",
        "Dummy car", 29.99F,
        "Description of dummy car",
        images,
        "Location of dummy car"
        ));
    return samples;
  }
}
