package util;

import model.Hashtag;
import model.Image;
import model.Post;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import org.sql2o.quirks.PostgresQuirks;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * A utility class with methods to establish JDBC connection, set schemas, etc.
 */
public final class Database {
  public static boolean USE_TEST_DATABASE = true;
  public static final String AUTO_UPDATE_TIMESTAMP_FUNC_NAME = "auto_update_update_time_column";

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
    createAutoUpdateTimestampDBFunc(sql2o);
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
        + dbUri.getPort() + dbUri.getPath();
    // accommodate local postgresql
    if (!dbUri.getHost().contains("localhost")) {
      dbUrl = dbUrl + "?sslmode=require";
    }

    Sql2o sql2o = new Sql2o(dbUrl, username, password, new PostgresQuirks());
    return sql2o;
  }

  /**
   * Create post table schema and add sample posts to it.
   *
   * @param sql2o   a Sql2o object connected to the database to be used in this application.
   * @param samples a list of sample posts.
   * @throws Sql2oException an generic exception thrown by Sql2o encapsulating anny issues with the Sql2o ORM.
   */
  public static void createPostsTableWithSampleData(Sql2o sql2o, List<Post> samples) throws Sql2oException {
    try (Connection conn = sql2o.open()) {
      // Must drop image and hashtag before post, to avoid foreign key dependency error
      conn.createQuery("DROP TABLE IF EXISTS post_hashtag;").executeUpdate();
      conn.createQuery("DROP TABLE IF EXISTS image;").executeUpdate();
      conn.createQuery("DROP TABLE IF EXISTS hashtag;").executeUpdate();
      conn.createQuery("DROP TABLE IF EXISTS post;").executeUpdate();
      conn.createQuery("DROP TYPE IF EXISTS Category;").executeUpdate();
      conn.createQuery("CREATE TYPE Category as enum ('FURNITURE', 'TV', 'DESK', 'CAR');").executeUpdate();


      // change naming rule to use underscores, as column names are case insensitive
      String sql = "CREATE TABLE IF NOT EXISTS post("
          + "id CHAR(36) NOT NULL PRIMARY KEY,"
          + "user_id CHAR(36),"   // make this foreign key in future iterations
          + "title VARCHAR(50) NOT NULL,"
          + "price NUMERIC(12, 2) NOT NULL,"  //NUMERIC(precision, scale) precision: valid numbers, 25.3213's precision
          // is 6 because it has 6 digital numbers. scale: for 25.3213, it's scale
          // is 4, because it has 4 digits after decimal point.
          + "description VARCHAR(5000),"
          + "category Category NOT NULL,"
          + "location VARCHAR(100) NOT NULL,"
          + "create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,"
          + "update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP"
          + ");";
      conn.createQuery(sql).executeUpdate();
      registerAutoUpdateTimestampDBFuncTriggerToTable(sql2o, "post");
      createHashtagsTable(sql2o);
      createPostsHashtagsTable(sql2o);
      createImagesTable(sql2o);

      insertSampleData(sql2o, samples);
    }
  }

  public static void truncateTables(Sql2o sql2o) throws Sql2oException {
    try (Connection conn = sql2o.open()) {
      // no need to truncate images and post_hashtag, as they will be deleted automatically when
      // foreign key table get truncated.
      conn.createQuery("TRUNCATE TABLE post CASCADE").executeUpdate();
      conn.createQuery("TRUNCATE TABLE hashtag CASCADE").executeUpdate();
    }
  }

  /**
   * Used for test, avoid creating table each time
   *
   * @param sql2o   sql2o
   * @param samples samples of posts
   */
  public static void insertSampleData(Sql2o sql2o, List<Post> samples) {
    for (Post post : samples) {
      addPostsWithInnerObjects(sql2o, post);
    }
  }

  /**
   * Create table: image
   * image has a forein key referencing id of post.
   * When the post that own this image get deleted, this image
   * will be automatically deleted.
   *
   * @param sql2o sql2o
   * @throws Sql2oException
   */
  public static void createImagesTable(Sql2o sql2o) throws Sql2oException {
    try (Connection conn = sql2o.open()) {
      conn.createQuery("DROP TABLE IF EXISTS image;").executeUpdate();
      String sql = "CREATE TABLE IF NOT EXISTS image("
          + "id CHAR(36) NOT NULL PRIMARY KEY,"
          + "post_id CHAR(36) NOT NULL,"
          + "url VARCHAR(500) NOT NULL,"
          + "FOREIGN KEY (post_id) " // Note: no comma here
          + "REFERENCES post(id) "
          + "ON DELETE CASCADE"
          + ");";
      conn.createQuery(sql).executeUpdate();
    }
  }

  /**
   * Create table: post_hashtag
   * post_hashtag store many to many relationships between post and hashtag
   * it has two foreign keys, referencing the primary keys of post and hashtag
   * When deleting post or hashtag, corresponding row in this table will be automatically
   * deleted, thanks to ON DELETE CASCADE.
   *
   * @param sql2o sql2o
   * @throws Sql2oException
   */
  public static void createPostsHashtagsTable(Sql2o sql2o) throws Sql2oException {
    try (Connection conn = sql2o.open()) {
      conn.createQuery("DROP TABLE IF EXISTS post_hashtag;").executeUpdate();
      String sql = "CREATE TABLE IF NOT EXISTS post_hashtag("
          + "post_id CHAR(36) NOT NULL,"
          + "hashtag_id CHAR(36) NOT NULL,"
          + "PRIMARY KEY (post_id, hashtag_id),"
          + "FOREIGN KEY (post_id) " // Note: no comma here
          + "REFERENCES post(id) "
          + "ON DELETE CASCADE,"
          + "FOREIGN KEY (hashtag_id) " // Note: no comma here
          + "REFERENCES hashtag(id) "
          + "ON DELETE CASCADE"
          + ");";
      conn.createQuery(sql).executeUpdate();
    }
  }

  /**
   * Create table: hashtag
   * hashtag store hashtag id and hashtag (the content)
   *
   * @param sql2o
   * @throws Sql2oException
   */
  public static void createHashtagsTable(Sql2o sql2o) throws Sql2oException {
    try (Connection conn = sql2o.open()) {
      conn.createQuery("DROP TABLE IF EXISTS hashtag;").executeUpdate();
      String sql = "CREATE TABLE IF NOT EXISTS hashtag("
          + "id CHAR(36) NOT NULL PRIMARY KEY,"
          + "hashtag VARCHAR(100) NOT NULL UNIQUE"
          + ");";
      conn.createQuery(sql).executeUpdate();
    }
  }

  // Get either the test or the production Database URL

  /**
   * get either the test or the production database url
   *
   * @return database url
   * @throws URISyntaxException if database url is not set
   */
  private static String getDatabaseUrl() throws URISyntaxException {
    String databaseUrl = null;
    if (USE_TEST_DATABASE)
      databaseUrl = System.getenv("TEST_DATABASE_URL");
    else
      databaseUrl = System.getenv("DATABASE_URL");
    if (databaseUrl == null) {
      if (USE_TEST_DATABASE) {
        throw new URISyntaxException("null", "TEST_DATABASE_URL is not set");
      } else {
        throw new URISyntaxException("null", "DATABASE_URL is not set");
      }
    }
    return databaseUrl;
  }

  // Add Post to the database connected to the conn object.

  /**
   * Add Post to the database connected to the conn object.
   *
   * @param sql2o
   * @param post  the to be add Post object
   * @throws Sql2oException
   */
  private static void addPostsWithInnerObjects(Sql2o sql2o, Post post) throws Sql2oException {
    try (Connection conn = sql2o.open()) {
      String sql = "INSERT INTO post(id, user_id, title, price, description, category, location) "
          + "VALUES(:id, :userId, :title, :price, :description, CAST(:category AS Category), :location);";
      conn.createQuery(sql).bind(post).executeUpdate();
      for (Image image : post.getImages()) {
        addImage(sql2o, image);
      }
      for (Hashtag hashtag : post.getHashtags()) {
        addHashtag(sql2o, hashtag);
        addPostHashtag(sql2o, post, hashtag);
      }
    }
  }

  /**
   * Add image instance to image table.
   *
   * @param sql2o sql2o
   * @param image image instance
   * @throws Sql2oException
   */
  private static void addImage(Sql2o sql2o, Image image) throws Sql2oException {
    try (Connection conn = sql2o.open()) {
      String sql = "INSERT INTO image(id, post_id, url) "
          + "VALUES(:id, :postId, :url);";
      conn.createQuery(sql).bind(image).executeUpdate();
    }
  }

  /**
   * Add hashtag to hastags table.
   * First check if the hashtag is already in the table.
   * If insert a hashtag that already in the table (eigher primary key duplicate
   * or hashtag (the column) duplicate), the database will raise exception.
   *
   * @param sql2o   sql2o
   * @param hashtag hashtag instance
   * @throws Sql2oException
   */
  private static void addHashtag(Sql2o sql2o, Hashtag hashtag) throws Sql2oException {
    try (Connection conn = sql2o.open()) {
      // To use simpleflatmapper, must use Query as original chain will break
//      Query query = conn.createQuery("SELECT * from hashtag where hashtag_id=:hashtagId OR " +
//          "hashtag=:hashtag;");
//      // Below line is all you need to add when using simpleflatmapper, everything else is the same
//      query.setAutoDeriveColumnNames(true)
//          .setResultSetHandlerFactoryBuilder(new SfmResultSetHandlerFactoryBuilder());
//      // bind, no need to convert names
//      List<Hashtag> existingHashtag = query.bind(hashtag).executeAndFetch(Hashtag.class);
//      if (existingHashtag.isEmpty()) {
      String sql = "INSERT INTO hashtag(id, hashtag) "
          + "VALUES(:id, :hashtag) ON CONFLICT DO NOTHING;";
      conn.createQuery(sql).bind(hashtag).executeUpdate();
//      }
    }
  }

  /**
   * Add post to hashtag relationship to post_hashtag table.
   *
   * @param sql2o   sql2o
   * @param post    the post that relates to hashtag
   * @param hashtag the hashtag that relates to post
   * @throws Sql2oException
   */
  private static void addPostHashtag(Sql2o sql2o, Post post, Hashtag hashtag) throws Sql2oException {
    try (Connection conn = sql2o.open()) {
      String sql = "INSERT INTO post_hashtag(post_id, hashtag_id) "
          + "VALUES(:postId, :hashtagId);";
      conn.createQuery(sql).addParameter("postId", post.getId())
          .addParameter("hashtagId", hashtag.getId())
          .executeUpdate();
    }
  }

  /**
   * Create a function in database to automatically
   *
   * @param sql2o sql2o
   * @return FUNC_NAME the name of the function
   */
  private static void createAutoUpdateTimestampDBFunc(Sql2o sql2o) throws Sql2oException {
    try (Connection conn = sql2o.open()) {
      String sql = "CREATE OR REPLACE FUNCTION " + AUTO_UPDATE_TIMESTAMP_FUNC_NAME + "() "
          + "RETURNS TRIGGER AS $$ "
          + "BEGIN"
          + "    NEW.update_time = CURRENT_TIMESTAMP; "
          + "    RETURN NEW; "
          + "END; "
          + "$$ language 'plpgsql';";
      conn.createQuery(sql).executeUpdate();
    }
  }

  /**
   * Create a trigger on TABLE_NAME to automatically update the "update_time" column
   * to CURRENT_TIMESTAMP.
   *
   * @param sql2o      sql2o
   * @param TABLE_NAME the table's name
   * @return
   * @throws Sql2oException
   */
  private static void registerAutoUpdateTimestampDBFuncTriggerToTable(Sql2o sql2o, final String TABLE_NAME) throws Sql2oException {
    try (Connection conn = sql2o.open()) {
      final String TRIG_NAME = "auto_update_" + TABLE_NAME + "_update_time";
      conn.createQuery("CREATE TRIGGER " + TRIG_NAME + " BEFORE UPDATE ON " + TABLE_NAME + " FOR EACH ROW EXECUTE " +
          "PROCEDURE " + AUTO_UPDATE_TIMESTAMP_FUNC_NAME + "();").executeUpdate();
    }
  }
}
