package util.database;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    samples.add(new Post("0".repeat(36), "001"+"1".repeat(33),
        "Brown Coffee Table", 99.99D, SaleState.SALE,
        "Sleek modern looking, almost new",
        sampleImages("0".repeat(36),"1".repeat(36),Category.FURNITURE),
        sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Hampden"
    ));
    samples.add(new Post("1".repeat(36), "002"+"1".repeat(33),
        "Samsung TV brand new", 489.99D, SaleState.SALE,
        "Samsung, brand new, what else to say?",
        sampleImages("2".repeat(36),"3".repeat(36),Category.TV),
        sampleHashtags(Category.TV),
        Category.TV,
        "BestBuy"
    ));
    samples.add(new Post("3".repeat(36), "003"+"1".repeat(33),
        "Dream car to sell", 20000D, SaleState.SALE,
        "Compact car with amazing turquoise color",
        sampleImages("4".repeat(36),"5".repeat(36),Category.CAR),
        sampleHashtags(Category.CAR),
        Category.CAR,
        "Inner Harbor"
    ));
    samples.add(new Post("4".repeat(36), "004"+"1".repeat(33),
        "Vintage office desk", 129.99D, SaleState.SALE,
        "I bought from IKEA",
        sampleImages("6".repeat(36),"7".repeat(36),Category.DESK),
        sampleHashtags(Category.DESK),
        Category.DESK,
        "Carlyle"
    ));
    samples.add(new Post("5".repeat(36), "005"+"1".repeat(33),
        "Minimalist lamp", 29.99D, SaleState.SALE,
        "I'm minimalist",
        sampleImages("8".repeat(36),"9".repeat(36),Category.FURNITURE),
        sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "ICON"
    ));
    samples.add(new Post("6".repeat(36), "005"+"1".repeat(33),
        "Coffee cup", 29.99D, SaleState.SALE,
        "Great for drinking beer",
        sampleImages("10".repeat(18),"12".repeat(18),Category.FURNITURE),
        sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Marylander"
    ));
    samples.add(new Post("7".repeat(36), "005"+"1".repeat(33),
        "1998 Toyota car", 7000D, SaleState.SALE,
        "It still works",
        sampleImages("13".repeat(18),"14".repeat(18),Category.CAR),
        sampleHashtags(Category.CAR),
        Category.CAR,
        "Towson"
    ));
    samples.add(new Post("8".repeat(36), "005"+"2".repeat(33),
            "1998 Toyota car", 7100D, SaleState.SOLD,
            "It still works",
            sampleImages("15".repeat(18),"16".repeat(18),Category.TV),
            sampleHashtags(Category.TV),
            Category.TV,
            "Towson"
    ));
    return samples;
  }

  /**
   * return a list of images corresponding to the list of posts
   * returned by samplePosts()
   * @return a list of images owned by posts returned by samplePosts()
   */
  public static List<Image> sampleImages(){
    List<Image> images = new ArrayList<>();
    List<Post> posts = samplePosts();
    for (Post post: posts)
    {
      images.addAll(post.getImages());
    }
    return images;
  }

  /**
   * return a list of 2 Image (models) given 2 ids.
   * This is used for tests
   * @return a list Image
   */
  public static List<Image> sampleImages(String uuid1, String uuid2, Category category) {
    List<Image> images = new ArrayList<>();

    switch (category) {
      case TV:
        images.add(new Image(uuid1, "https://images.samsung.com/is/image/samsung/levant-uhd-tu8500" +
            "-ua55tu8500uxtw-frontblack-229855928?$720_576_PNG$"));
        images.add(new Image(uuid2, "https://images.samsung.com/is/image/samsung/ca-uhdtv-nu7090" +
            "-un55nu6900fxzc-frontblack-115122587?$720_576_PNG$"));
        break;
      case CAR:
        images.add(new Image(uuid1, "https://d32c3oe4bky4k6.cloudfront.net/-" +
            "/media/usdirect/images/insurance/classic-car-insurance/car_billboard-image.ashx"));
        images.add(new Image(uuid2, "https://hips.hearstapps.com/amv-prod-gp.s3.amazonaws.com/gearpatrol/" +
            "wp-content/uploads/2019/10/Buy-a-Kia-Telluride-Instead-gear-patrol-slide-1.jpg"));
        break;
      case DESK:
        images.add(new Image(uuid1, "https://1i9wu42vzknf1h4zwf2to5aq-wpengine.netdna-ssl" +
            ".com/wp-content/uploads/2019/02/x_AN-1981_YthDeskPROF_o_s_.jpg"));
        images.add(new Image(uuid2, "https://cdn.shopify.com/s/files/1/0075/2815/3206/products/426789547" +
            ".jpg?v=1559247399"));
        break;
      case FURNITURE:
        images.add(new Image(uuid1, "https://hips.hearstapps.com/vader-prod.s3.amazonaws" +
            ".com/1592920567-mid-century-double-pop-up-coffee-table-walnut-white-marble-2-c.jpg"));
        images.add(new Image(uuid2, "https://apicms.thestar.com.my/uploads/images/2020/02/21/570850.jpg"));
        break;
    }
    return images;
  }

  /**
   * return a list Image given category with random id
   *
   * @param category the category of this image
   * @return a list internet image urls
   */
  public static List<Image> sampleImages(Category category) {
    List<Image> images = new ArrayList<>();
    String uuid1 = UUID.randomUUID().toString();
    String uuid2 = UUID.randomUUID().toString();

    switch (category) {
      case TV:
        images.add(new Image(uuid1, "https://images.samsung.com/is/image/samsung/levant-uhd-tu8500" +
            "-ua55tu8500uxtw-frontblack-229855928?$720_576_PNG$"));
        images.add(new Image(uuid2, "https://images.samsung.com/is/image/samsung/ca-uhdtv-nu7090" +
            "-un55nu6900fxzc-frontblack-115122587?$720_576_PNG$"));
        break;
      case CAR:
        images.add(new Image(uuid1, "https://d32c3oe4bky4k6.cloudfront.net/-" +
            "/media/usdirect/images/insurance/classic-car-insurance/car_billboard-image.ashx"));
        images.add(new Image(uuid2, "https://hips.hearstapps.com/amv-prod-gp.s3.amazonaws.com/gearpatrol/" +
            "wp-content/uploads/2019/10/Buy-a-Kia-Telluride-Instead-gear-patrol-slide-1.jpg"));
        break;
      case DESK:
        images.add(new Image(uuid1, "https://1i9wu42vzknf1h4zwf2to5aq-wpengine.netdna-ssl" +
            ".com/wp-content/uploads/2019/02/x_AN-1981_YthDeskPROF_o_s_.jpg"));
        images.add(new Image(uuid2, "https://cdn.shopify.com/s/files/1/0075/2815/3206/products/426789547" +
            ".jpg?v=1559247399"));
        break;
      case FURNITURE:
        images.add(new Image(uuid1, "https://hips.hearstapps.com/vader-prod.s3.amazonaws" +
            ".com/1592920567-mid-century-double-pop-up-coffee-table-walnut-white-marble-2-c.jpg"));
        images.add(new Image(uuid2, "https://apicms.thestar.com.my/uploads/images/2020/02/21/570850.jpg"));
        break;
    }
    return images;
  }

  /**
   * return some hashtags stored in a list with random id
   *
   * @return a list of hashtags
   */
  public static List<Hashtag> sampleHashtags(Category category) {
    List<Hashtag> hashtags = new ArrayList<>();

    switch (category) {
      case TV:
        hashtags.add(new Hashtag("0".repeat(36), "samsung"));
        hashtags.add(new Hashtag("1".repeat(36), "4k"));
        break;
      case CAR:
        hashtags.add(new Hashtag("2".repeat(36), "lexus"));
        hashtags.add(new Hashtag("3".repeat(36), "toyota"));
        break;
      case DESK:
        hashtags.add(new Hashtag("4".repeat(36), "ikea"));
        hashtags.add(new Hashtag("5".repeat(36), "4leg"));
        break;
      case FURNITURE:
        hashtags.add(new Hashtag("6".repeat(36), "table"));
        hashtags.add(new Hashtag("7".repeat(36), "coffee"));
        break;
    }
    return hashtags;
  }
}
