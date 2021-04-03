package dao.jdbi;

import dao.PostHashtagDao;
import dao.WishlistPostSkeletonDao;
import dao.jdbiDao.JdbiPostHashtagDao;
import dao.jdbiDao.JdbiWishlistPostSkeletonDao;
import model.Post;
import model.WishlistPostSkeleton;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import util.database.DataStore;
import util.database.Database;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        //Database.truncateTables(jdbi);
        //Database.insertSampleWishlistPosts(jdbi, sampleWishlistPostSkeletons);
        wishlistPostSkeletonDao = new JdbiWishlistPostSkeletonDao(jdbi);
    }

    @AfterAll
    static void setUseProductionDatabase() {
        Database.USE_TEST_DATABASE = false; // use production dataset
    }

    //@Test
    void doNothing() {
    }

    @Test //test breaks if specified user_id has more than one post wishlisted!
    @DisplayName("test to see if create wishlist entry works")
    void createWishlistEntryWorks() {
        WishlistPostSkeleton newEntry = wishlistPostSkeletonDao.createWishListEntry("8".repeat(36), "005111111111111111111111111111111111");

        List<Post> wishlistSkeletonEntries = wishlistPostSkeletonDao.readAllWishlistEntries("005111111111111111111111111111111111");

        assertEquals(wishlistSkeletonEntries.get(0).getId(), newEntry.getPostId());

    }


}
