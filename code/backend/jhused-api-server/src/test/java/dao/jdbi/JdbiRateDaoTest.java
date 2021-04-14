package dao.jdbi;

import dao.RateDao;
import dao.jdbiDao.JdbiRateDao;
import exceptions.DaoException;
import model.Rate;
import model.User;
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

import static org.junit.jupiter.api.Assertions.*;

public class JdbiRateDaoTest {
  private static final List<Rate> sampleRates = DataStore.sampleRates();
  private static final List<User> sampleUsers = DataStore.sampleUsers();
  private static Jdbi jdbi;
  private RateDao rateDao;

  @BeforeAll
  static void connectToDatabase() throws URISyntaxException {
    Database.USE_TEST_DATABASE = true; // use test dataset
    Database.main(null); // reset dataset and add samples
    jdbi = Database.getJdbi();
  }

  @BeforeEach
  void injectDependency() throws URISyntaxException {
    Database.truncateTable(jdbi, "rate");
    Database.insertSampleRates(jdbi, DataStore.sampleRates());
    rateDao = new JdbiRateDao(jdbi);
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
    Database.truncateTable(jdbi, "rate");
    for (Rate rate : sampleRates) {
      assertEquals(rate, rateDao.create(rate));
    }
  }

  @Test
  void createThrowsDaoExceptionInvalidInput() {
    assertThrows(DaoException.class, () -> {
      Rate rate = new Rate("kjj0210hnf", sampleUsers.get(0).getId(), 0);
      rateDao.create(rate);
    });
    assertThrows(DaoException.class, () -> {
      Rate rate = new Rate(sampleUsers.get(0).getId(), "ljiefibsldfisjfei", 0);
      rateDao.create(rate);
    });
    assertThrows(DaoException.class, () -> {
      Rate rate = new Rate(sampleUsers.get(2).getId(), sampleUsers.get(0).getId(), -1);
      rateDao.create(rate);
    });
    assertThrows(DaoException.class, () -> {
      Rate rate = new Rate(sampleUsers.get(2).getId(), sampleUsers.get(0).getId(), 6);
      rateDao.create(rate);
    });
  }

  @Test
  void createOrUpdateWorks() {
    Rate newRate = new Rate(sampleUsers.get(1).getId(), sampleUsers.get(0).getId(), 4);
    assertEquals(newRate, rateDao.createOrUpdate(newRate.getRaterId(), newRate.getSellerId(), newRate));
    Rate existingRate = sampleRates.get(0);
    existingRate.setRate((existingRate.getRate() + 1) % 6);
    assertEquals(existingRate, rateDao.createOrUpdate(existingRate.getRaterId(), existingRate.getSellerId(),
        existingRate));
  }

  @Test
  void createOrUpdateThrowsDaoExceptionInvalidInput() {
    assertThrows(DaoException.class, () -> {
      Rate rate = new Rate("kjj0210hnf", sampleUsers.get(0).getId(), 0);
      rateDao.createOrUpdate(rate.getRaterId(), rate.getSellerId(), rate);
    });
    assertThrows(DaoException.class, () -> {
      Rate rate = new Rate(sampleUsers.get(0).getId(), "ljisejlfii", 0);
      rateDao.createOrUpdate(rate.getRaterId(), rate.getSellerId(), rate);
    });
    assertThrows(DaoException.class, () -> {
      Rate rate = new Rate(sampleUsers.get(2).getId(), sampleUsers.get(0).getId(), -1);
      rateDao.createOrUpdate(rate.getRaterId(), rate.getSellerId(), rate);
    });
    assertThrows(DaoException.class, () -> {
      Rate rate = new Rate(sampleUsers.get(2).getId(), sampleUsers.get(0).getId(), 6);
      rateDao.createOrUpdate(rate.getRaterId(), rate.getSellerId(), rate);
    });
    assertThrows(DaoException.class, () -> {
      Rate rate = new Rate(sampleUsers.get(2).getId(), sampleUsers.get(0).getId(), 2);
      rateDao.createOrUpdate(rate.getRaterId(), rate.getSellerId(), null);
    });
  }

  @Test
  void readWorks() {
    for (Rate rate : sampleRates) {
      assertEquals(rate, rateDao.read(rate.getRaterId(), rate.getSellerId()));
    }
  }

  @Test
  void readAverageWorks() {
    Map<String, Double> avg = new LinkedHashMap<>();
    Map<String, Integer> cnt = new LinkedHashMap<>();
    for (Rate rate : sampleRates) {
      Double current = avg.get(rate.getSellerId());
      if (current == null) {
        current = (double) rate.getRate();
        avg.put(rate.getSellerId(), current);
        cnt.put(rate.getSellerId(), 1);
      } else {
        int n = cnt.get(rate.getSellerId());
        current = (current * n + rate.getRate()) / (n + 1);
        avg.put(rate.getSellerId(), current);
        cnt.put(rate.getSellerId(), n + 1);
      }
    }

    avg.forEach((key, value) -> assertEquals(rateDao.readAvgRateOfASeller(key),
        ((double) Math.round(value * 100)) / 100));
  }

  @Test
  void readReturnNullNonExistingIds() {
    assertNull(rateDao.read("lijsleifjsief", sampleUsers.get(1).getId()));
    assertNull(rateDao.read(sampleUsers.get(1).getId(), "lsiefliesjflisj"));
    assertNull(rateDao.read(null, sampleUsers.get(1).getId()));
    assertNull(rateDao.read(sampleUsers.get(1).getId(), null));
  }

  @Test
  void updateWorks() {
    for (Rate rate : sampleRates) {
      rate.setRate((rate.getRate() + 1) % 6);
      assertEquals(rate, rateDao.update(rate.getRaterId(), rate.getSellerId(), rate));
    }
  }

  @Test
  void updateThrowsDaoExceptionNullRate() {
    assertThrows(DaoException.class, () -> {
      Rate rate = new Rate(sampleRates.get(0).getRaterId(), sampleRates.get(0).getSellerId(), 1);
      rateDao.update(rate.getRaterId(), rate.getSellerId(), null);
    });
  }

  @Test
  void deleteWorks() {
    for (Rate rate : sampleRates) {
      assertEquals(rate, rateDao.delete(rate.getRaterId(), rate.getSellerId()));
    }
  }

  @Test
  void deleteReturnNullNonExistingRate() {
    assertNull(rateDao.delete("lsieflijssliefjilsjf", sampleRates.get(0).getSellerId()));
  }
}
