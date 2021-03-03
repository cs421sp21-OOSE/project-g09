package dao;

import exceptions.DaoException;
import model.Course;
import org.junit.jupiter.api.*;
import util.DataStore;
import util.Database;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Sql2oCourseDaoTest {
  private static List<Course> samples;
  private CourseDao courseDao;

  @BeforeAll
  static void setSampleCourses() {
    samples = DataStore.sampleCourses();
  }

  @BeforeEach
  void injectDependency() throws URISyntaxException {
    Database.USE_TEST_DATABASE=true; // use test dataset
    Database.main(null); // reset dataset and add samples
    courseDao = new Sql2oCourseDao(Database.getSql2o());
  }

  @AfterAll
  static void setUseProductionDatabase(){
    Database.USE_TEST_DATABASE = false; // use production dataset
  }

  @Test
  void doNothing() {

  }

  @Test
  @DisplayName("create works for valid input")
  void createNewCourse() {
    Course c1 = new Course("EN.601.421", "Object-Oriented Software Engineering");
    Course c2 = courseDao.create(c1.getOfferingName(), c1.getTitle());
    assertEquals(c1, c2);
  }

  @Test
  @DisplayName("create throws exception for duplicate course")
  void createThrowsExceptionDuplicateData() {
    assertThrows(DaoException.class, () -> {
      courseDao.create("EN.500.112", "GATEWAY COMPUTING: JAVA");
    });
  }

  @Test
  @DisplayName("create throws exception for invalid input")
  void createThrowsExceptionIncompleteData() {
    assertThrows(DaoException.class, () -> {
      courseDao.create(null, null);
    });
  }

  @Test
  @DisplayName("read a course given its offering name")
  void readCourseGivenOfferingName() {
    for (Course c2 : samples) {
      Course c1 = courseDao.read(c2.getOfferingName());
      assertEquals(c2, c1);
    }
  }

  @Test
  @DisplayName("read returns null given invalid offering name")
  void readCourseGivenInvalidOfferingName() {
    Course c1 = courseDao.read("EN.00.999");
    assertNull(c1);
  }

  @Test
  @DisplayName("read all the courses")
  void readAll() {
    List<Course> courses = courseDao.readAll();
    assertIterableEquals(samples, courses);
  }

  @Test
  @DisplayName("read all the courses that contain a query string in their title")
  void readAllGivenTitle() {
    String query = "data";
    List<Course> courses = courseDao.readAll(query);
    assertNotEquals(0, courses.size());
    for (Course course : courses) {
      assertTrue(course.getTitle().toLowerCase().contains(query.toLowerCase()));
    }
  }

  @Test
  @DisplayName("readAll(query) returns empty list when query not matches courses' title")
  void readAllGivenNonExistingTitle() {
    String query = "game";
    List<Course> courses = courseDao.readAll(query);
    assertEquals(0, courses.size());
  }

  @Test
  @DisplayName("updating a course works")
  void updateWorks() {
    String title = "Updated Title!";
    Course course = courseDao.update(samples.get(0).getOfferingName(), title);
    assertEquals(title, course.getTitle());
    assertEquals(samples.get(0).getOfferingName(), course.getOfferingName());
  }

  @Test
  @DisplayName("Update returns null for an invalid offeringCode")
  void updateReturnsNullInvalidCode() {
    Course course = courseDao.update("EN.000.999", "UpdatedTitle");
    assertNull(course);
  }

  @Test
  @DisplayName("Update throws exception for an invalid title")
  void updateThrowsExceptionInvalidTitle() {
    assertThrows(DaoException.class, () -> {
      courseDao.update(samples.get(0).getOfferingName(), null);
    });
  }

  @Test
  @DisplayName("delete works for valid input")
  void deleteExistingCourse() {
    Course courseDeleted = courseDao.delete(samples.get(0).getOfferingName());
    assertEquals(courseDeleted, samples.get(0));
    assertNull(courseDao.read(courseDeleted.getOfferingName()));
  }

  @Test
  @DisplayName("delete returns null for non existing course")
  void deleteThrowsExceptionNoMatchData() {
    assertNull(courseDao.delete("EN.000.999"));
  }

  @Test
  @DisplayName("delete returns null for invalid input")
  void deleteThrowsExceptionIncompleteData() {
    assertNull(courseDao.delete(null));
  }

}

