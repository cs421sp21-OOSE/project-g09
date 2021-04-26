package dao.jdbi;

import dao.PostVisitDao;
import dao.jdbiDao.JdbiPostVisitDao;
import model.PostVisit;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.database.DataStore;
import util.database.Database;

import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JdbiPostVisitDaoTest {

  private static final List<PostVisit> samplePostVisits = DataStore.samplePostVisits();
  private static Jdbi jdbi;
  private static PostVisitDao postVisitDao;

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
    postVisitDao = new JdbiPostVisitDao(jdbi);
  }

  @AfterAll
  static void setUseProductionDatabase() {
    Database.USE_TEST_DATABASE = false; // use production dataset
  }

  @Test
  void doNothing() {
  }

  @Test
  void createWorks() {
    Database.truncateTable(jdbi, "post_visit");
    for (PostVisit postVisit : samplePostVisits) {
      assertEquals(postVisit, postVisitDao.create(postVisit));
    }
  }

  @Test
  void createDuplicateReturnNull() {
    for (PostVisit postVisit : samplePostVisits) {
      assertNull(postVisitDao.create(postVisit));
    }
  }

  @Test
  void readWorks() {
    for (PostVisit postVisit : samplePostVisits) {
      assertEquals(postVisit, postVisitDao.read(postVisit.getPostId(), postVisit.getUserId()));
    }
  }

  @Test
  void readReturnNullNonExisting() {
    assertNull(postVisitDao.read("lsijfesjflij","JHUsedAdmin"));
    assertNull(postVisitDao.read("3".repeat(36),"lsiefjleisjfis"));
  }

  @Test
  void visitCountWorks() {
    Map<String, Integer> viewCnt = new LinkedHashMap<>();
    for(PostVisit postVisit:samplePostVisits){
      Integer cnt = viewCnt.get(postVisit.getPostId());
      if(cnt==null){
        viewCnt.put(postVisit.getPostId(),1);
      }else{
        cnt+=1;
        viewCnt.put(postVisit.getPostId(),cnt);
      }
    }
    viewCnt.forEach((k,v)->{
      assertEquals(v,postVisitDao.visitCount(k));
    });
  }

  @Test
  void NonExistingViewCount0() {
    assertEquals(0,postVisitDao.visitCount("lsiefisejf"));
  }
}
