package util.database;

import model.*;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.postgresql.ds.PGSimpleDataSource;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import org.sql2o.converters.Converter;
import org.sql2o.quirks.PostgresQuirks;
import util.sql2oConverter.InstantConverter;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class with methods to establish JDBC connection, set schemas, etc.
 */
public final class Database {
  public static boolean USE_TEST_DATABASE = false;
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
    Jdbi jdbi = getJdbi();
    createAutoUpdateTimestampDBFunc(jdbi);
    drop(jdbi);
    createUsersTableWithSampleData(jdbi, DataStore.sampleUsers());
    createPostsTableWithSampleData(jdbi, DataStore.samplePosts());
    createWishlistPostsTableWithSampleData(jdbi, DataStore.sampleWishlistPosts());
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

    Map<Class, Converter> converters = new HashMap<>();
    converters.put(Instant.class, new InstantConverter());
    Sql2o sql2o = new Sql2o(dbUrl, username, password, new PostgresQuirks(converters));
    return sql2o;
  }

  public static Jdbi getJdbi() throws URISyntaxException {
    String databaseUrl = getDatabaseUrl();
    URI dbUri = new URI(databaseUrl);

    PGSimpleDataSource ds = new PGSimpleDataSource();
    ds.setServerNames(new String[]{dbUri.getHost() + ":" + dbUri.getPort()});
    ds.setDatabaseName(dbUri.getPath().substring(1));
    ds.setUser(dbUri.getUserInfo().split(":")[0]);
    ds.setPassword(dbUri.getUserInfo().split(":")[1]);
    ds.setLoadBalanceHosts(true);
    if (!dbUri.getHost().contains("localhost"))
      ds.setSslMode("require");
//    HikariConfig hc = new HikariConfig();
//    hc.setDataSource(ds);
//    hc.setMaximumPoolSize(6);
    return Jdbi.create(ds).installPlugin(new PostgresPlugin());
  }

  /**
   * create message table with samples
   * @param jdbi jdbi
   * @param samples message samples
   */
  public static void createMessageTableWithSampleData(Jdbi jdbi, List<Message> samples) {
    String sql = "CREATE TABLE IF NOT EXISTS message("
        + "id char(36) NOT NULL PRIMARY KEY,"
        + "sender_id VARCHAR(50) NOT NULL,"
        + "receiver_id VARCHAR(50) NOT NULL,"
        + "message VARCHAR NOT NULL,"
        + "read BOOLEAN DEFAULT FALSE,"
        + "sent_time TIMESTAMPEZ DEFAULT CURRENT_TIMESTAMP,"
        + "FOREIGN KEY (sender_id) " // Note: no comma here
        + "REFERENCES jhused_user(id) "
        + "ON DELETE CASCADE,"
        + "FOREIGN KEY (receiver_id) " // Note: no comma here
        + "REFERENCES jhused_user(id) "
        + "ON DELETE CASCADE"
        + ");";
    jdbi.useTransaction(handle -> {
      handle.execute(sql);
    });
  }

  public static void insertSampleMessages(Jdbi jdbi, List<Message> samples) {
    String sql = "INSERT INTO message(id, sender_id, receiver_id, message, read, sent_time) "
        + "VALUES(:id, :sender_id, :receiver_id, message, read, sent_time);";
    jdbi.useTransaction(handle -> {
      PreparedBatch batch = handle.prepareBatch(sql);
      for (Message message: samples) {
        batch.bindBean(message).add();
      }
      batch.execute();
    });
  }

  public static void createWishlistPostsTableWithSampleData(Jdbi jdbi, List<WishlistPostSkeleton> samples) {
    String sql = "CREATE TABLE IF NOT EXISTS wishlist_post("
            + "id VARCHAR(50) NOT NULL PRIMARY KEY,"
            + "user_id VARCHAR(50) NOT NULL"
            + ");";
    jdbi.useTransaction(handle -> {
      handle.execute(sql);
    });
    insertSampleWishlistPosts(jdbi, samples);
  }

  public static void insertSampleWishlistPosts(Jdbi jdbi, List<WishlistPostSkeleton> samples) {
    String sql = "INSERT INTO wishlist_post(id, user_id) "
            + "VALUES(:id, :user_id);";
    jdbi.useTransaction(handle -> {
      PreparedBatch batch = handle.prepareBatch(sql);
      for (WishlistPostSkeleton wishlistPostSkeleton: samples) {
        batch.bindBean(wishlistPostSkeleton).add();
      }
      batch.execute();
    });
  }

  /**
   * Create user table schema and add sample users to it.
   *
   * @param jdbi    a Jdbi object connected to the database to be used in this application.
   * @param samples a list of sample users.
   */
  public static void createUsersTableWithSampleData(Jdbi jdbi, List<User> samples) {
    String sql = "CREATE TABLE IF NOT EXISTS jhused_user("
        + "id VARCHAR(50) NOT NULL PRIMARY KEY,"
        + "name VARCHAR(100) NOT NULL,"
        + "email VARCHAR(100) NOT NULL,"
        + "profile_image VARCHAR(200),"
        + "location VARCHAR(500)"
        + ");";
    jdbi.useTransaction(handle -> {
      handle.execute(sql);
    });
    insertSampleUsers(jdbi, samples);
  }

  public static void drop(Jdbi jdbi) {
    jdbi.useTransaction(handle -> {
      handle.execute("DROP TABLE IF EXISTS message;");
      handle.execute("DROP TABLE IF EXISTS wishlist_post;");
      handle.execute("DROP TABLE IF EXISTS post_hashtag;");
      handle.execute("DROP TABLE IF EXISTS image;");
      handle.execute("DROP TABLE IF EXISTS hashtag;");
      handle.execute("DROP TABLE IF EXISTS post;");
      handle.execute("DROP TYPE IF EXISTS Category;");
      handle.execute("DROP TYPE IF EXISTS SaleState;");
      handle.execute("DROP TABLE IF EXISTS jhused_user;");});
  }

  /**
   * Used for inserting the sample users
   *
   * @param jdbi a Jdbi object connected to the database to be used in this application.
   * @param samples samples of users
   */
  public static void insertSampleUsers(Jdbi jdbi, List<User> samples) {
    String sql = "INSERT INTO jhused_user(id, name, email, profile_image, location) "
        + "VALUES(:id, :name, :email, :profileImage," +
        ":location);";
    jdbi.useTransaction(handle -> {
      PreparedBatch batch = handle.prepareBatch(sql);
      for (User user: samples) {
        batch.bindBean(user).add();
      }
      batch.execute();
    });
  }

  /**
   * Create post table schema and add sample posts to it.
   *
   * @param jdbi    a Jdbi object connected to the database to be used in this application.
   * @param samples a list of sample posts.
   * @throws Sql2oException an generic exception thrown by Sql2o encapsulating anny issues with the Sql2o ORM.
   */
  public static void createPostsTableWithSampleData(Jdbi jdbi, List<Post> samples) throws Sql2oException {
    // Must drop image and hashtag before post, to avoid foreign key dependency error
    String sql = "CREATE TABLE IF NOT EXISTS post("
        + "id CHAR(36) NOT NULL PRIMARY KEY,"
        + "user_id VARCHAR(50),"   // make this foreign key in future iterations
        + "title VARCHAR(50) NOT NULL,"
        + "price NUMERIC(12, 2) NOT NULL,"  //NUMERIC(precision, scale) precision: valid numbers, 25.3213's precision
        // is 6 because it has 6 digital numbers. scale: for 25.3213, it's scale
        // is 4, because it has 4 digits after decimal point.
        + "sale_state SaleState NOT NULL DEFAULT 'SALE',"
        + "description VARCHAR(5000),"
        + "category Category NOT NULL,"
        + "location VARCHAR(100) NOT NULL,"
        + "create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,"
        + "update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,"
        + "FOREIGN KEY (user_id) " // Note: no comma here
        + "REFERENCES jhused_user(id) "
        + "ON DELETE CASCADE"
        + ");";
    jdbi.useTransaction(handle -> {
      handle.execute("CREATE TYPE Category as enum (" +
          getAllNamesGivenValues(Category.values()) +
          ");");
      handle.execute("CREATE TYPE SaleState as enum (" +
          getAllNamesGivenValues(SaleState.values()) +
          ");");
      handle.execute(sql);
      registerAutoUpdateTimestampDBFuncTriggerToTable(jdbi, "post");
      createHashtagsTable(jdbi);
      createPostsHashtagsTable(jdbi);
      createImagesTable(jdbi);
    });
    insertSamplePosts(jdbi, samples);
  }

  public static void truncateTables(Jdbi jdbi) throws Sql2oException {
    // no need to truncate images and post_hashtag, as they will be deleted automatically when
    // foreign key table get truncated.
    jdbi.useTransaction(handle -> {
      handle.execute("TRUNCATE TABLE jhused_user CASCADE");
      handle.execute("TRUNCATE TABLE hashtag CASCADE");
    });
  }

  /**
   * Used for test, avoid creating table each time
   *
   * @param jdbi    a Jdbi object connected to the database to be used in this application.
   * @param samples samples of posts
   */
  public static void insertSamplePosts(Jdbi jdbi, List<Post> samples) {
    for (Post post : samples) {
      addPostsWithInnerObjects(jdbi, post);
    }
  }

  /**
   * Create table: image
   * image has a forein key referencing id of post.
   * When the post that own this image get deleted, this image
   * will be automatically deleted.
   *
   * @param jdbi a Jdbi object connected to the database to be used in this application.
   * @throws Sql2oException
   */
  public static void createImagesTable(Jdbi jdbi) throws Sql2oException {
    String sql = "CREATE TABLE IF NOT EXISTS image("
        + "id CHAR(36) NOT NULL PRIMARY KEY,"
        + "post_id CHAR(36) NOT NULL,"
        + "url VARCHAR(500) NOT NULL,"
        + "FOREIGN KEY (post_id) " // Note: no comma here
        + "REFERENCES post(id) "
        + "ON DELETE CASCADE"
        + ");";
    jdbi.useTransaction(handle -> {
          handle.execute("DROP TABLE IF EXISTS image;");
          handle.execute(sql);
        }
    );
  }

  /**
   * Create table: post_hashtag
   * post_hashtag store many to many relationships between post and hashtag
   * it has two foreign keys, referencing the primary keys of post and hashtag
   * When deleting post or hashtag, corresponding row in this table will be automatically
   * deleted, thanks to ON DELETE CASCADE.
   *
   * @param jdbi a Jdbi object connected to the database to be used in this application.
   * @throws Sql2oException
   */
  public static void createPostsHashtagsTable(Jdbi jdbi) throws Sql2oException {
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
    jdbi.useTransaction(handle -> {
      handle.execute("DROP TABLE IF EXISTS post_hashtag;");
      handle.execute(sql);
    });
  }

  /**
   * Create table: hashtag
   * hashtag store hashtag id and hashtag (the content)
   *
   * @param jdbi a Jdbi object connected to the database to be used in this application.
   * @throws Sql2oException
   */
  public static void createHashtagsTable(Jdbi jdbi) {
    String sql = "CREATE TABLE IF NOT EXISTS hashtag("
        + "id CHAR(36) NOT NULL PRIMARY KEY,"
        + "hashtag VARCHAR(100) NOT NULL UNIQUE"
        + ");";
    jdbi.useTransaction(handle -> {
      handle.execute("DROP TABLE IF EXISTS hashtag;");
      handle.execute(sql);
    });
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
   * @param jdbi a Jdbi object connected to the database to be used in this application.
   * @param post the to be add Post object
   * @throws Sql2oException
   */
  private static void addPostsWithInnerObjects(Jdbi jdbi, Post post) {
    String sql = "INSERT INTO post(id, user_id, title, price, sale_state, description, category, location) "
        + "VALUES(:id, :userId, :title, :price, CAST(:saleState AS SaleState), " +
        ":description, CAST(:category AS Category), :location);";
    jdbi.useTransaction(handle -> {
      handle.createUpdate(sql).bindBean(post).execute();
    });
    addImages(jdbi, post.getImages());
    addHashtags(jdbi, post.getHashtags());
    addOnePostManyHashtags(jdbi, post, post.getHashtags());
  }

  /**
   * Add image instances to image table.
   *
   * @param jdbi   a Jdbi object connected to the database to be used in this application.
   * @param images a list of image instances
   * @throws Sql2oException
   */
  private static void addImages(Jdbi jdbi, List<Image> images) {
    String sql = "INSERT INTO image(id, post_id, url) "
        + "VALUES(:id, :postId, :url);";
    jdbi.useTransaction(handle -> {
      PreparedBatch batch = handle.prepareBatch(sql);
      for (Image image : images) {
        batch.bindBean(image).add();
      }
      batch.execute();
    });
  }

  /**
   * Add hashtag to hastags table.
   * First check if the hashtag is already in the table.
   * If insert a hashtag that already in the table (eigher primary key duplicate
   * or hashtag (the column) duplicate), the database will raise exception.
   *
   * @param jdbi     a Jdbi object connected to the database to be used in this application.
   * @param hashtags a list of hashtag instances
   * @throws Sql2oException
   */
  private static void addHashtags(Jdbi jdbi, List<Hashtag> hashtags) {
    String sql = "INSERT INTO hashtag(id, hashtag) "
        + "VALUES(:id, :hashtag) ON CONFLICT DO NOTHING;";
    jdbi.useTransaction(handle -> {
      PreparedBatch batch = handle.prepareBatch(sql);
      for (Hashtag hashtag : hashtags) {
        batch.bindBean(hashtag).add();
      }
      batch.execute();
    });
  }

  /**
   * Add post to hashtag relationship to post_hashtag table.
   *
   * @param jdbi     a Jdbi object connected to the database to be used in this application.
   * @param post     the post that relates to hashtag
   * @param hashtags a list of the hashtag that relates to post
   * @throws Sql2oException
   */
  private static void addOnePostManyHashtags(Jdbi jdbi, Post post, List<Hashtag> hashtags) {
    String sql = "INSERT INTO post_hashtag(post_id, hashtag_id) "
        + "VALUES(:postId, :hashtagId);";
    String post_id = post.getId();
    jdbi.useTransaction(handle -> {
      PreparedBatch batch = handle.prepareBatch(sql);
      for (Hashtag hashtag : hashtags) {
        batch.bind("postId", post_id).bind("hashtagId", hashtag.getId()).add();
      }
      batch.execute();
    });
  }

  /**
   * Create a function in database to automatically
   *
   * @param jdbi a Jdbi object connected to the database to be used in this application.
   * @return FUNC_NAME the name of the function
   */
  private static void createAutoUpdateTimestampDBFunc(Jdbi jdbi) throws Sql2oException {
    String sql = "CREATE OR REPLACE FUNCTION " + AUTO_UPDATE_TIMESTAMP_FUNC_NAME + "() "
        + "RETURNS TRIGGER AS $$ "
        + "BEGIN"
        + "    NEW.update_time = CURRENT_TIMESTAMP; "
        + "    RETURN NEW; "
        + "END; "
        + "$$ language 'plpgsql';";

    jdbi.useTransaction(handle -> handle.execute(sql));
  }

  /**
   * Create a trigger on TABLE_NAME to automatically update the "update_time" column
   * to CURRENT_TIMESTAMP.
   *
   * @param jdbi       a Jdbi object connected to the database to be used in this application.
   * @param TABLE_NAME the table's name
   * @return
   * @throws Sql2oException
   */
  private static void registerAutoUpdateTimestampDBFuncTriggerToTable(Jdbi jdbi, final String TABLE_NAME) throws Sql2oException {
    final String TRIG_NAME = "auto_update_" + TABLE_NAME + "_update_time";
    jdbi.useTransaction(handle -> handle.execute("CREATE TRIGGER " + TRIG_NAME + " BEFORE UPDATE ON " + TABLE_NAME +
        " FOR EACH ROW EXECUTE " +
        "PROCEDURE " + AUTO_UPDATE_TIMESTAMP_FUNC_NAME + "();"));
  }

  /**
   * return all names of a enum for creating enum type in database
   *
   * @param values all the values of a enum, pass Enum.values() to this arg
   * @param <T>    The Enum type
   * @return a string of enum names. For example: "'SALE', 'SOLD', 'DEALING'".
   */
  private static <T extends Enum<T>> String getAllNamesGivenValues(T[] values) {
    String allNames = "";
    for (T s : values) {
      allNames = allNames + "'" + s.name() + "', ";
    }
    allNames = allNames.substring(0, allNames.lastIndexOf(", "));
    return allNames;
  }
}
