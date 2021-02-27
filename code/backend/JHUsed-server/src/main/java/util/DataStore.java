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
    samples.add(new Post());
    samples.add(new Post());
    samples.add(new Post());
    samples.add(new Post());
    return samples;
  }
}
