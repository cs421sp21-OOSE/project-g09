package util;

import model.Post;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.net.URI;
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
   *                            URI reference.
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
   *                            URI reference.
   * @throws Sql2oException     an generic exception thrown by Sql2o encapsulating anny issues with the Sql2o ORM.
   */
  public static Sql2o getSql2o() throws URISyntaxException, Sql2oException {
    String databaseUrl = getDatabaseUrl();
    URI dbUri = new URI(databaseUrl);

    String username = dbUri.getUserInfo().split(":")[0];
    String password = dbUri.getUserInfo().split(":")[1];
    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':'
        + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

    return new Sql2o(dbUrl, username, password);
  }

  /**
   * Create posts table schema and add sample CS posts to it.
   *
   * @param sql2o   a Sql2o object connected to the database to be used in this application.
   * @param samples a list of sample CS posts.
   * @throws Sql2oException an generic exception thrown by Sql2o encapsulating anny issues with the Sql2o ORM.
   */
  public static void createPostsTableWithSampleData(Sql2o sql2o, List<Post> samples) throws Sql2oException {

    try (Connection conn = sql2o.open()) {
      conn.createQuery("DROP TABLE IF EXISTS posts;").executeUpdate();

      String sql = "CREATE TABLE IF NOT EXISTS posts("
          + "offeringName VARCHAR(15) NOT NULL PRIMARY KEY,"
          + "title VARCHAR(50) NOT NULL"
          + ");";
      conn.createQuery(sql).executeUpdate();

      for (Post post : samples) {
        add(conn, post);
      }
    }
  }

  // Get either the test or the production Database URL
  private static String getDatabaseUrl() throws URISyntaxException {
    String databaseUrl = null;
    if (USE_TEST_DATABASE)
      databaseUrl = System.getenv("TEST_DATABASE_URL");
    else
      databaseUrl = System.getenv("DATABASE_URL");
    if (databaseUrl==null)
    {
      if(USE_TEST_DATABASE)
        throw new URISyntaxException("null", "TEST_DATABASE_URL is not set");
      else
        throw new URISyntaxException("null", "DATABASE_URL is not set");
    }
    return databaseUrl;
  }

  // Add post to the database connected to the conn object.
  private static void add(Connection conn, Post post) throws Sql2oException {
    String sql = "INSERT INTO posts(offeringName, title) VALUES(:offeringName, :title);";
    conn.createQuery(sql).bind(post).executeUpdate();
  }
}
