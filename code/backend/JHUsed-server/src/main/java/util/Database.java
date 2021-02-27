package util;

import java.net.URI;
import model.Post;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.net.URISyntaxException;
import java.util.List;

/**
 * A utility class with methods to establish JDBC connection, set schemas, etc.
 */
public final class Database {
  public static boolean USE_TEST_DATABASE = false;

  private Database() {
    // This class should not be instantiated.
  }

  /**
   * Connect to the database and build the tables with sample data for this application.
   * <p>
   * Caution: Use this to cleanup the database.
   * </p>
   *
   * @param args command-line arguments; not used here.
   * @throws URISyntaxException Checked exception thrown to indicate the provided database URL cannot be parsed as a
   *     URI reference.
   */
  public static void main(String[] args) throws URISyntaxException {
    Sql2o sql2o = getSql2o();
    createPostsTableWithSampleData(sql2o, DataStore.samplePosts());
  }

  /**
   * Create and return a Sql2o object connected to the database pointed to by the DATABASE_URL.
   *
   * @return a Sql2o object connected to the database to be used in this application.
   * @throws URISyntaxException Checked exception thrown to indicate the provided database URL cannot be parsed as a
   *     URI reference.
   * @throws Sql2oException an generic exception thrown by Sql2o encapsulating anny issues with the Sql2o ORM.
   */
  public static Sql2o getSql2o() throws URISyntaxException, Sql2oException {
    String databaseUrl = getDatabaseUrl();
    URI dbUri = new URI(databaseUrl);

    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':'
        + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

    Sql2o sql2o = new Sql2o(dbUrl, username, password);
    return sql2o;
  }

  /**
   * Create Posts table schema and add sample CS Posts to it.
   *
   * @param sql2o a Sql2o object connected to the database to be used in this application.
   * @param samples a list of sample CS Posts.
   * @throws Sql2oException an generic exception thrown by Sql2o encapsulating anny issues with the Sql2o ORM.
   */
  public static void createPostsTableWithSampleData(Sql2o sql2o, List<Post> samples) throws Sql2oException {
    // TODO Implement Me!
    try (Connection conn = sql2o.open()) {
      conn.createQuery("DROP TABLE IF EXISTS Posts;").executeUpdate();

      String sql = "CREATE TABLE IF NOT EXISTS Posts("
          + "id VARCHAR(15) NOT NULL PRIMARY KEY,"
          + "title VARCHAR(50) NOT NULL"
          + ");";
      conn.createQuery(sql).executeUpdate();

      sql = "INSERT INTO Posts(id, title) VALUES(:id, :title);";
      for (Post Post : samples) {
        add(conn, Post);
      }
    }
  }

  // Get either the test or the production Database URL
  private static String getDatabaseUrl() throws URISyntaxException {
    String databaseUrl = System.getenv("DATABASE_URL");
    return databaseUrl;
  }

  // Add Post to the database connected to the conn object.
  private static void add(Connection conn, Post Post) throws Sql2oException {
    // TODO Implement Me!
    String sql = "INSERT INTO Posts(id, title) VALUES(:id, :title);";
    conn.createQuery(sql).bind(Post).executeUpdate();
  }
}
