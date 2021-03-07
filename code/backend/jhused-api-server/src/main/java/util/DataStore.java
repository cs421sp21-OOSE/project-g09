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
        sampleImageUrls(Category.FURNITURE),
        sampleHashtags(),
        Category.FURNITURE,
        "Location of dummy furniture"
    ));
    samples.add(new Post("1".repeat(36), "002",
        "Dummy TV", 40D,
        "Description of dummy TV",
        sampleImageUrls(Category.TV),
        sampleHashtags(),
        Category.TV,
        "Location of dummy TV"
    ));
    samples.add(new Post("3".repeat(36), "003",
        "Dummy car", 50D,
        "Description of dummy car",
        sampleImageUrls(Category.CAR),
        sampleHashtags(),
        Category.CAR,
        "Location of dummy bed"
    ));
    samples.add(new Post("4".repeat(36), "004",
        "Dummy desk", 29.99D,
        "Description of dummy desk",
        sampleImageUrls(Category.DESK),
        sampleHashtags(),
        Category.DESK,
        "Location of dummy desk"
    ));
    samples.add(new Post("5".repeat(36), "005",
        "Dummy lamp", 29.99D,
        "Description of dummy lamp",
        sampleImageUrls(Category.FURNITURE),
        sampleHashtags(),
        Category.FURNITURE,
        "Location of dummy lamp"
    ));
    samples.add(new Post("6".repeat(36), "005",
        "Dummy cup", 29.99D,
        "Description of dummy cup",
        sampleImageUrls(Category.FURNITURE),
        sampleHashtags(),
        Category.FURNITURE,
        "Location of dummy cup"
    ));
    samples.add(new Post("7".repeat(36), "005",
        "Dummy car", 29.99D,
        "Description of dummy car",
        sampleImageUrls(Category.CAR),
        sampleHashtags(),
        Category.CAR,
        "Location of dummy car"
    ));
    return samples;
  }

  /**
   * return a list internet image urls
   * could use some more work to generate reasonable image urls.
   *
   * @return a list internet image urls
   */
  public static List<String> sampleImageUrls() {
    List<String> imageUrls = new ArrayList<>();
    imageUrls.add("https://images.samsung.com/is/image/samsung/levant-uhd-tu8500-ua55tu8500uxtw-frontblack-229855928?$720_576_PNG$");
    imageUrls.add("https://images.samsung.com/is/image/samsung/ca-uhdtv-nu7090-un55nu6900fxzc-frontblack-115122587?$720_576_PNG$");
    return imageUrls;
  }

  /**
   * return a list internet image urls given category
   *
   * @return a list internet image urls
   */
  public static List<String> sampleImageUrls(Category category) {
    List<String> imageUrls = new ArrayList<>();
    switch (category) {
      case TV:
        imageUrls.add("https://images.samsung.com/is/image/samsung/levant-uhd-tu8500-ua55tu8500uxtw-frontblack-229855928?$720_576_PNG$");
        imageUrls.add("https://images.samsung.com/is/image/samsung/ca-uhdtv-nu7090-un55nu6900fxzc-frontblack-115122587?$720_576_PNG$");
        break;
      case CAR:
        imageUrls.add("https://d32c3oe4bky4k6.cloudfront.net/-" +
            "/media/usdirect/images/insurance/classic-car-insurance/car_billboard-image.ashx");
        imageUrls.add("https://hips.hearstapps.com/amv-prod-gp.s3.amazonaws.com/gearpatrol/" +
            "wp-content/uploads/2019/10/Buy-a-Kia-Telluride-Instead-gear-patrol-slide-1.jpg");
        break;
      case DESK:
        imageUrls.add("https://1i9wu42vzknf1h4zwf2to5aq-wpengine.netdna-ssl.com/wp-content/uploads/2019/02/x_AN-1981_YthDeskPROF_o_s_.jpg");
        imageUrls.add("https://cdn.shopify.com/s/files/1/0075/2815/3206/products/426789547.jpg?v=1559247399");
        break;
      case FURNITURE:
        imageUrls.add("https://hips.hearstapps.com/vader-prod.s3.amazonaws.com/1592920567-mid-century-double-pop-up-coffee-table-walnut-white-marble-2-c.jpg");
        imageUrls.add("https://apicms.thestar.com.my/uploads/images/2020/02/21/570850.jpg");
        break;
    }
    return imageUrls;
  }

  /**
   * return some hashtags stored in a list
   *
   * @return a list of hashtags
   */
  public static List<String> sampleHashtags() {
    List<String> hashtags = new ArrayList<>();
    hashtags.add("something");
    hashtags.add("something too");
    return hashtags;
  }
}
