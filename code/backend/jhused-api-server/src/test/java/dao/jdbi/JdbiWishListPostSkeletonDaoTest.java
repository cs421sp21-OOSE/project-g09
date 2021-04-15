package dao.jdbi;

import dao.WishlistPostSkeletonDao;
import dao.jdbiDao.JdbiWishlistPostSkeletonDao;
import model.Post;
import model.WishlistPostSkeleton;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import util.database.DataStore;
import util.database.Database;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JdbiWishListPostSkeletonDaoTest {
  private static final List<WishlistPostSkeleton> sampleWishlistPostSkeletons = DataStore.sampleWishlistPosts();
  private static Jdbi jdbi;
  private WishlistPostSkeletonDao wishlistPostSkeletonDao;

  @BeforeAll
  static void connectToDatabase() throws URISyntaxException {
    Database.USE_TEST_DATABASE = true; // use test dataset
    Database.main(null); // reset dataset and add samples
    jdbi = Database.getJdbi();
  }

  @BeforeEach
  void injectDependency() throws URISyntaxException {
    Database.truncateTable(jdbi, "wishlist_post");
    Database.insertSampleWishlistPosts(jdbi, sampleWishlistPostSkeletons);
    wishlistPostSkeletonDao = new JdbiWishlistPostSkeletonDao(jdbi);
  }

  @AfterAll
  static void setUseProductionDatabase() {
    Database.USE_TEST_DATABASE = false; // use production dataset
  }

  //@Test
  void doNothing() {
  }

  @Test
  @DisplayName("test to see if create wishlist entry works")
  void createWishlistEntryWorks() {
    String userId = "004" + "1".repeat(33);
    wishlistPostSkeletonDao.createWishListEntry("0".repeat(36), userId);
    List<Post> posts = wishlistPostSkeletonDao.readAllWishlistEntries(userId);
    List<WishlistPostSkeleton> wishlistEntries = wishlistPostSkeletonDao.readAll(userId);

    assertEquals(posts.size(), wishlistEntries.size());
    for (WishlistPostSkeleton wishlistPostSkeleton : wishlistEntries) {
      assertTrue(() -> {
        for (Post post : posts) {
          if (post.getId().equals(wishlistPostSkeleton.getPostId()))
            return true;
        }
        return false;
      });
    }
  }

  @Test
  void deleteWork() {
    WishlistPostSkeleton wishlistPostSkeleton = wishlistPostSkeletonDao.deleteWishlistEntry(
        sampleWishlistPostSkeletons.get(0).getPostId(), sampleWishlistPostSkeletons.get(0).getUserId());
    assertEquals(sampleWishlistPostSkeletons.get(0), wishlistPostSkeleton);
  }

  @Test
  void readAllFromPostIDWorks() {
    List<WishlistPostSkeleton> wishlistPostSkeletons = wishlistPostSkeletonDao.readAllFromPostId("000000000000000000000000000000000000");

    assertEquals("000000000000000000000000000000000000", wishlistPostSkeletons.get(0).getPostId());
  }

}
