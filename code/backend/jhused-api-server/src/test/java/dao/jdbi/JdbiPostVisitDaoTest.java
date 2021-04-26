package dao.jdbi;

import dao.jdbiDao.JdbiWishlistPostSkeletonDao;
import model.PostVisit;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.database.DataStore;
import util.database.Database;

import java.net.URISyntaxException;
import java.util.List;

public class JdbiPostVisitDaoTest {

  private static final List<PostVisit> samplePostVisits = DataStore.samplePostVisits();
  private static Jdbi jdbi;

  @BeforeAll
  static void connectToDatabase() throws URISyntaxException {
    Database.USE_TEST_DATABASE = true; // use test dataset
    Database.main(null); // reset dataset and add samples
    jdbi = Database.getJdbi();
  }

  @BeforeEach
  void injectDependency() throws URISyntaxException {
    Database.truncateTable(jdbi, "post_visit");
    Database.insertSamplePostVisit(jdbi, samplePostVisits);
  }

  @AfterAll
  static void setUseProductionDatabase() {
    Database.USE_TEST_DATABASE = false; // use production dataset
  }

  @Test
  void doNothing() {
  }
}
