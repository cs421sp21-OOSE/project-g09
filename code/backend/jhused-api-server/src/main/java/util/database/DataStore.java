package util.database;

import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * A utility class with methods to create sample data.
 */
public final class DataStore {

  private DataStore() {
    // This class should not be instantiated.
  }

  public static List<Rate> sampleRates() {
    List<Rate> rates = new ArrayList<>();
    List<User> users = sampleUsers();
    int totalUsers = users.size();
    for (int i = 1; i < totalUsers; ++i) {
      rates.add(new Rate(users.get(i).getId(), users.get(1 + (i + 1) % (totalUsers - 1)).getId(), (i + 3) % 6));
      rates.add(new Rate(users.get(i).getId(), users.get(1 + (i + 2) % (totalUsers - 1)).getId(), (i + 2) % 6));
    }
    return rates;
  }

  /**
   * Create a list of sample Users.
   *
   * @return a list of sample Users.
   */
  public static List<User> sampleUsers() {
    User user0 = new User("JHUsedAdmin", "JHUsed Administrator", "ldibern1@jh.edu", "https://images6.fanpop"
        + ".com/image/photos/33700000/Arya-Stark-arya-stark-33779443-1600-1200.jpg", "", new ArrayList<>(),
        new ArrayList<>());
    User user1 = new User("001" + "1".repeat(33), "Arya", "abc1@yahoo.com", "https://images6.fanpop"
        + ".com/image/photos/33700000/Arya-Stark-arya-stark-33779443-1600-1200.jpg", "keyser Quad", getPostForUser(
        "001" + "1".repeat(33)), getWishListPostsForUser("001" + "1".repeat(33)));
    User user2 = new User("002" + "1".repeat(33), "Tyrion", "abc2@yahoo.com", "http://pix2.tvzhe"
        + ".com/thumb/character/184/535/405x540.jpg", "Freshman quad", getPostForUser("002" + "1".repeat(33)),
        getWishListPostsForUser("002" + "1".repeat(33)));
    User user3 = new User("003" + "1".repeat(33), "Danny", "abc3@yahoo.com", "https://img.cvtvcn"
        + ".com/group1/default/20191022/13/45/1/1571723140170.jpg", "Wyman quad",
        getPostForUser("003" + "1".repeat(33)), getWishListPostsForUser("003" + "1".repeat(33)));
    User user4 = new User("004" + "1".repeat(33), "Jon", "abc4@yahoo.com", "https://cw1.tw/CW/images/article/201708"
        + "/article-599e6c4e2f51d.jpg", "decker quad", getPostForUser("004" + "1".repeat(33)),
        getWishListPostsForUser("004" + "1".repeat(33)));
    User user5 = new User("005" + "1".repeat(33), "Cersi", "abc5@yahoo.com", "https://hips.hearstapps.com/hmg-prod.s3"
        + ".amazonaws.com/images/cersei-euron-gift-1500579238.png?crop=0.563xw:1xh;center,top&resize=980:*", "hodson "
        + "hall", getPostForUser(
        "005" + "1".repeat(33)), getWishListPostsForUser("005" + "1".repeat(33)));
    User user6 = new User("005" + "2".repeat(33), "Shae", "abc6@yahoo.com", "https://static.wikia.nocookie"
        + ".net/asoiaf/images/3/3d/Shae_HBO.jpg/revision/latest/scale-to-width-down/300?cb=20120205045225&path-prefix"
        + "=zh", "the beach", getPostForUser("005" + "2".repeat(33)), getWishListPostsForUser("005" + "2".repeat(33)));
    User user7 = new User("007" + "1".repeat(33), "NightKing", "abc7@yahoo.com", "https://encrypted-tbn0.gstatic"
        + ".com/images?q=tbn:ANd9GcQ1QwaGlXQD0Ov_7ekfNe5d1kJtMmulrFQUpw&usqp=CAU", "the beach", getPostForUser("empty"
    ), getWishListPostsForUser("empty"));
    List<User> samples = new ArrayList<>(Arrays.asList(user0, user1, user2, user3, user4, user5, user6, user7));

    return samples;
  }

  public static List<Post> getWishListPostsForUser(String userId) {
    List<WishlistPostSkeleton> skeletons = DataStore.sampleWishlistPosts();
    List<Post> samplePosts = DataStore.samplePosts();
    List<Post> ret = new ArrayList<>();
    for (WishlistPostSkeleton skeleton : skeletons) {
      if (userId == skeleton.getUserId()) {
        for (Post sample : samplePosts) {
          if (sample.getId() == skeleton.getPostId()) {
            ret.add(sample);
          }
        }
      }
    }
    return ret;
  }

  public static List<PostVisit> samplePostVisits() {
    List<PostVisit> postVisits = new ArrayList<>();
    postVisits.add(new PostVisit("JHUsedAdmin", "0".repeat(36)));
    postVisits.add(new PostVisit("001111111111111111111111111111111111", "1".repeat(36)));
    postVisits.add(new PostVisit("001111111111111111111111111111111111", "3".repeat(36)));
    postVisits.add(new PostVisit("002111111111111111111111111111111111", "3".repeat(36)));
    postVisits.add(new PostVisit("003111111111111111111111111111111111", "4".repeat(36)));
    postVisits.add(new PostVisit("003111111111111111111111111111111111", "3".repeat(36)));
    postVisits.add(new PostVisit("004111111111111111111111111111111111", "5".repeat(36)));
    postVisits.add(new PostVisit("005111111111111111111111111111111111", "6".repeat(36)));
    postVisits.add(new PostVisit("005222222222222222222222222222222222", "7".repeat(36)));
    postVisits.add(new PostVisit("007111111111111111111111111111111111", "7".repeat(36)));
    return postVisits;
  }

  /**
   * Create a list of sample Wishlist Posts
   *
   * @return a list of sample wishlist posts.
   */
  public static List<WishlistPostSkeleton> sampleWishlistPosts() {
    WishlistPostSkeleton post1 = new WishlistPostSkeleton("0".repeat(36), "JHUsedAdmin");
    WishlistPostSkeleton post2 = new WishlistPostSkeleton("1".repeat(36), "001111111111111111111111111111111111");
    WishlistPostSkeleton post3 = new WishlistPostSkeleton("3".repeat(36), "002111111111111111111111111111111111");
    WishlistPostSkeleton post4 = new WishlistPostSkeleton("4".repeat(36), "003111111111111111111111111111111111");
    WishlistPostSkeleton post5 = new WishlistPostSkeleton("5".repeat(36), "004111111111111111111111111111111111");
    WishlistPostSkeleton post6 = new WishlistPostSkeleton("6".repeat(36), "005111111111111111111111111111111111");
    WishlistPostSkeleton post7 = new WishlistPostSkeleton("7".repeat(36), "005222222222222222222222222222222222");
    WishlistPostSkeleton post8 = new WishlistPostSkeleton("7".repeat(36), "007111111111111111111111111111111111");
    List<WishlistPostSkeleton> samples = new ArrayList<>(Arrays.asList(post1, post2, post3, post4, post5, post6,
        post7, post8));
    return samples;
  }

  public static List<Post> getPostForUser(String id) {
    List<Post> allPosts = DataStore.samplePosts();
    List<Post> userPosts = new ArrayList<>();
    for (Post post : allPosts) {
      if (post.getUserId().equals(id)) {
        userPosts.add(post);
      }
    }
    return userPosts;
  }

  public static User getNewUserForTest() {
    List<Post> samplePosts = DataStore.samplePosts();
    List<Post> wishList = new ArrayList<>(Arrays.asList(samplePosts.get(0), samplePosts.get(1)));
    Post postNew = new Post("12".repeat(18), "008" + "1".repeat(33),
        "2008 Toyota car", 7100D, SaleState.SOLD,
        "It still works",
        DataStore.sampleImages(Category.CAR),
        DataStore.sampleHashtags(Category.CAR),
        Category.CAR,
        "Towson");
    User userNew = new User("008" + "1".repeat(33), "Ed", "abc8@yahoo.com", "https://images6.fanpop"
        + ".com/image/photos/33700000/Arya-Stark-arya-stark-33779443-1600-1200.jpg", "keyser Quad",
        new ArrayList<>(Arrays.asList(postNew)), wishList);
    return userNew;
  }

  /**
   * NEED REFACTOR
   * Create a list of sample CS courses.
   *
   * @return a list of sample CS courses.
   */
  public static List<Post> samplePosts() {
    Post p1 = new Post("0".repeat(36), "001" + "1".repeat(33),
        "Brown Coffee Table", 99.99D, SaleState.SALE,
        "Sleek modern looking, almost new",
        sampleImages("0".repeat(36), "1".repeat(36), Category.FURNITURE),
        sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Hampden"
    );
    Post p2 = new Post("1".repeat(36), "002" + "1".repeat(33),
        "Samsung ELECTRONICS brand new", 489.99D, SaleState.SALE,
        "Samsung, brand new, what else to say?",
        sampleImages("2".repeat(36), "3".repeat(36), Category.ELECTRONICS),
        sampleHashtags(Category.ELECTRONICS),
        Category.ELECTRONICS,
        "BestBuy"
    );
    Post p3 = new Post("3".repeat(36), "003" + "1".repeat(33),
        "Dream car to sell", 20000D, SaleState.SALE,
        "Compact car with amazing turquoise color",
        sampleImages("4".repeat(36), "5".repeat(36), Category.CAR),
        sampleHashtags(Category.CAR),
        Category.CAR,
        "Inner Harbor"
    );
    Post p4 = new Post("4".repeat(36), "004" + "1".repeat(33),
        "Vintage office desk", 129.99D, SaleState.SALE,
        "I bought from IKEA",
        sampleImages("6".repeat(36), "7".repeat(36), Category.FREE),
        sampleHashtags(Category.FREE),
        Category.FREE,
        "Carlyle"
    );
    Post p5 = new Post("5".repeat(36), "005" + "1".repeat(33),
        "Minimalist lamp", 29.99D, SaleState.SALE,
        "I'm minimalist",
        sampleImages("8".repeat(36), "9".repeat(36), Category.FURNITURE),
        sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "ICON"
    );
    Post p6 = new Post("6".repeat(36), "005" + "1".repeat(33),
        "Coffee cup", 29.99D, SaleState.SALE,
        "Great for drinking beer",
        sampleImages("10".repeat(18), "12".repeat(18), Category.FURNITURE),
        sampleHashtags(Category.FURNITURE),
        Category.FURNITURE,
        "Marylander"
    );
    Post p7 = new Post("7".repeat(36), "005" + "1".repeat(33),
        "1998 Toyota car", 7000D, SaleState.SALE,
        "It still works",
        sampleImages("13".repeat(18), "14".repeat(18), Category.CAR),
        sampleHashtags(Category.CAR),
        Category.CAR,
        "Towson"
    );
    Post p8 = new Post("8".repeat(36), "005" + "2".repeat(33),
        "1998 Toyota car", 7100D, SaleState.SOLD,
        "It still works",
        sampleImages("15".repeat(18), "16".repeat(18), Category.ELECTRONICS),
        sampleHashtags(Category.ELECTRONICS),
        Category.ELECTRONICS,
        "Towson"
    );
    Post p9 = new Post("9".repeat(36), "005" + "2".repeat(33),
        "1998 Toyota car", 7100D, SaleState.SOLD,
        "It still works",
        sampleImages("17".repeat(18), "18".repeat(18), Category.FURNITURE),
        null,
        Category.FURNITURE,
        "Towson"
    );
    return new ArrayList<>(Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9));
  }

  /**
   * return a list of images corresponding to the list of posts
   * returned by samplePosts()
   *
   * @return a list of images owned by posts returned by samplePosts()
   */
  public static List<Image> sampleImages() {
    List<Image> images = new ArrayList<>();
    List<Post> posts = samplePosts();
    for (Post post : posts) {
      images.addAll(post.getImages());
    }
    return images;
  }

  /**
   * return a list of 2 Image (models) given 2 ids.
   * This is used for tests
   *
   * @return a list Image
   */
  public static List<Image> sampleImages(String uuid1, String uuid2, Category category) {
    List<Image> images = new ArrayList<>();

    switch (category) {
      case ELECTRONICS:
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
      case FREE:
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
      case ELECTRONICS:
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
      case FREE:
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
      case ELECTRONICS:
        hashtags.add(new Hashtag("0".repeat(36), "samsung"));
        hashtags.add(new Hashtag("1".repeat(36), "4k"));
        break;
      case CAR:
        hashtags.add(new Hashtag("2".repeat(36), "lexus"));
        hashtags.add(new Hashtag("3".repeat(36), "toyota"));
        break;
      case FREE:
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

  public static List<Message> sampleMessages() {
    List<Message> messages = new ArrayList<>();
    messages.add(new Message(UUID.randomUUID().toString(), "JHUsedAdmin", "001" + "1".repeat(33), "Welcome to JHUsed"
        + ".", false));
    messages.add(new Message(UUID.randomUUID().toString(), "JHUsedAdmin", "002" + "1".repeat(33), "Welcome to JHUsed"
        + ".", false));
    messages.add(new Message(UUID.randomUUID().toString(), "JHUsedAdmin", "003" + "1".repeat(33), "Welcome to JHUsed"
        + ".", false));
    messages.add(new Message(UUID.randomUUID().toString(), "JHUsedAdmin", "004" + "1".repeat(33), "Welcome to JHUsed"
        + ".", false));
    messages.add(new Message(UUID.randomUUID().toString(), "JHUsedAdmin", "005" + "1".repeat(33), "Welcome to JHUsed"
        + ".", false));
    messages.add(new Message(UUID.randomUUID().toString(), "JHUsedAdmin", "005" + "2".repeat(33), "Welcome to JHUsed"
        + ".", false));
    messages.add(new Message(UUID.randomUUID().toString(), "JHUsedAdmin", "007" + "1".repeat(33), "Welcome to JHUsed"
        + ".", false));
    messages.add(new Message(UUID.randomUUID().toString(), "001" + "1".repeat(33), "001" + "1".repeat(33), "Welcome "
        + "to JHUsed.", false));
    messages.add(new Message(UUID.randomUUID().toString(), "005" + "1".repeat(33), "002" + "1".repeat(33), "Welcome "
        + "to JHUsed.", false));
    messages.add(new Message(UUID.randomUUID().toString(), "005" + "2".repeat(33), "003" + "1".repeat(33), "Welcome "
        + "to JHUsed.", false));
    messages.add(new Message(UUID.randomUUID().toString(), "007" + "1".repeat(33), "004" + "1".repeat(33), "Welcome "
        + "to JHUsed.", false));
    messages.add(new Message("0".repeat(36), "JHUsedAdmin", "004" + "1".repeat(33), "Welcome "
        + "to JHUsed.", false));
    return messages;
  }

}
