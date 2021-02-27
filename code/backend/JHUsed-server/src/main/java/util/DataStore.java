package util;

import java.util.ArrayList;
import java.util.List;
import model.Post;

/**
 * A utility class with methods to create sample data.
 */
public final class DataStore {

  private DataStore() {
    // This class should not be instantiated.
  }

  /**
   * Create a list of sample CS Posts.
   *
   * @return a list of sample CS Posts.
   */
  public static List<Post> samplePosts() {
    List<Post> samples = new ArrayList<>();
    samples.add(new Post("001", "table"));
    samples.add(new Post("002", "chair"));
    samples.add(new Post("003", "desk"));
    samples.add(new Post("004", "mattress"));
    return samples;
  }
}
