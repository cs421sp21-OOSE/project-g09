package util;

import model.Category;
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
   * NEED REFACTOR
   * Create a list of sample CS courses.
   *
   * @return a list of sample CS courses.
   */
  public static List<Post> samplePosts() {
    List<Post> samples = new ArrayList<>();
    samples.add(new Post("0".repeat(36), "001",
        "Dummy furniture", 30D,
        "Description of dummy furniture",
        sampleImageUrls(),
        sampleHashtags(),
        Category.FURNITURE,
        "Location of dummy furniture"
    ));
    samples.add(new Post("1".repeat(36), "002",
        "Dummy TV", 40D,
        "Description of dummy TV",
        sampleImageUrls(),
        sampleHashtags(),
        Category.TV,
        "Location of dummy TV"
    ));
    samples.add(new Post("3".repeat(36), "003",
        "Dummy bed", 50D,
        "Description of dummy bed",
        sampleImageUrls(),
        sampleHashtags(),
        Category.CAR,
        "Location of dummy bed"
    ));
    samples.add(new Post("4".repeat(36), "004",
        "Dummy desk", 29.99D,
        "Description of dummy desk",
        sampleImageUrls(),
        sampleHashtags(),
        Category.DESK,
        "Location of dummy desk"
    ));
    samples.add(new Post("5".repeat(36), "005",
        "Dummy lamp", 29.99D,
        "Description of dummy lamp",
        sampleImageUrls(),
        sampleHashtags(),
        Category.TV,
        "Location of dummy lamp"
    ));
    samples.add(new Post("6".repeat(36), "005",
        "Dummy cup", 29.99D,
        "Description of dummy cup",
        sampleImageUrls(),
        sampleHashtags(),
        Category.DESK,
        "Location of dummy cup"
    ));
    samples.add(new Post("7".repeat(36), "005",
        "Dummy car", 29.99D,
        "Description of dummy car",
        sampleImageUrls(),
        sampleHashtags(),
        Category.FURNITURE,
        "Location of dummy car"
    ));
    return samples;
  }

  /**
   * return a list internet image urls
   * could use some more work to generate reasonable image urls.
   * @return a list internet image urls
   */
  public static List<String> sampleImageUrls() {
    List<String> imageUrls = new ArrayList<>();
    imageUrls.add("https://www.runoob.com/wp-content/uploads/2014/03/postgresql-11-1175122.png");
    imageUrls.add("https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/amazon-rivet-furniture-1533048038.jpg");
    return imageUrls;
  }

  /**
   * return some hashtags stored in a list
   * @return a list of hashtags
   */
  public static List<String> sampleHashtags() {
    List<String> hashtags = new ArrayList<>();
    hashtags.add("something");
    hashtags.add("something too");
    return hashtags;
  }
}
