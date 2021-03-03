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
   * Create a list of sample CS courses.
   *
   * @return a list of sample CS courses.
   */
  public static List<Post> samplePosts() {
    List<Post> samples = new ArrayList<>();
    List<String> imageUrls0 = new ArrayList<String>();
    List<String> imageUrls1 = new ArrayList<String>();
    List<String> imageUrls2 = new ArrayList<String>();
    List<String> imageUrls3 = new ArrayList<String>();
    List<String> imageUrls4 = new ArrayList<String>();
    List<String> imageUrls5 = new ArrayList<String>();
    List<String> imageUrls6 = new ArrayList<String>();
    List<String> hashtags0 = new ArrayList<String>();
    List<String> hashtags1 = new ArrayList<String>();
    List<String> hashtags2 = new ArrayList<String>();
    List<String> hashtags3 = new ArrayList<String>();
    List<String> hashtags4 = new ArrayList<String>();
    List<String> hashtags5 = new ArrayList<String>();
    List<String> hashtags6 = new ArrayList<String>();
    hashtags0.add("some thing");
    hashtags0.add("haha");
    hashtags1.add("some thing");
    hashtags1.add("some thing too");
    hashtags2.add("some thing");
    hashtags2.add("some thing too");
    hashtags3.add("some thing");
    hashtags3.add("some thing too");
    hashtags4.add("some thing");
    hashtags4.add("some thing too");
    hashtags5.add("some thing");
    hashtags5.add("some thing too");
    hashtags6.add("some thing");
    hashtags6.add("some thing too");
    Category category0=Category.FURNITURE;
    Category category1=Category.TV;
    Category category2=Category.CAR;
    Category category3=Category.DESK;
    Category category4=Category.TV;
    Category category5=Category.DESK;
    Category category6=Category.FURNITURE;
    imageUrls0.add("https://www.runoob.com/wp-content/uploads/2014/03/postgresql-11-1175122.png");
    imageUrls1.add("https://www.runoob.com/wp-content/uploads/2014/03/postgresql-11-1175122.png");
    imageUrls2.add("https://www.runoob.com/wp-content/uploads/2014/03/postgresql-11-1175122.png");
    imageUrls3.add("https://www.runoob.com/wp-content/uploads/2014/03/postgresql-11-1175122.png");
    imageUrls4.add("https://www.runoob.com/wp-content/uploads/2014/03/postgresql-11-1175122.png");
    imageUrls5.add("https://www.runoob.com/wp-content/uploads/2014/03/postgresql-11-1175122.png");
    imageUrls6.add("https://www.runoob.com/wp-content/uploads/2014/03/postgresql-11-1175122.png");
    samples.add(new Post("0", "001",
        "Dummy furniture", 30D,
        "Description of dummy furniture",
        imageUrls0,
        hashtags0,
        category0,
        "Location of dummy furniture"
        ));
    samples.add(new Post("1", "002",
        "Dummy TV", 40D,
        "Description of dummy TV",
        imageUrls1,
        hashtags1,
        category1,
        "Location of dummy TV"
        ));
    samples.add(new Post("3", "003",
        "Dummy bed", 50D,
        "Description of dummy bed",
        imageUrls2,
        hashtags2,
        category2,
        "Location of dummy bed"
        ));
    samples.add(new Post("4", "004",
        "Dummy desk", 29.99D,
        "Description of dummy desk",
        imageUrls3,
        hashtags3,
        category3,
        "Location of dummy desk"
        ));
    samples.add(new Post("5", "005",
        "Dummy lamp", 29.99D,
        "Description of dummy lamp",
        imageUrls4,
        hashtags4,
        category4,
        "Location of dummy lamp"
        ));
    samples.add(new Post("6","005",
        "Dummy cup", 29.99D,
        "Description of dummy cup",
        imageUrls5,
        hashtags5,
        category5,
        "Location of dummy cup"
        ));
    samples.add(new Post("7","005",
        "Dummy car", 29.99D,
        "Description of dummy car",
        imageUrls6,
        hashtags6,
        category6,
        "Location of dummy car"
        ));
    return samples;
  }
}
