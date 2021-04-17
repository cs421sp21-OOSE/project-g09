package email;

import dao.UserDao;
import dao.jdbiDao.JdbiUserDao;
import model.Post;
import model.User;
import email.WishlistEmails;
import model.WishlistPostSkeleton;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import util.database.DataStore;
import util.database.Database;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WishlistEmailsTest {
    private static List<User> sampleUsers;
    private static List<Post> samplePosts;
    private static List<WishlistPostSkeleton> sampleWishlistSkeleton;
    private UserDao userDao;
    private static Jdbi jdbi;

    @BeforeAll
    static void setSamples() {
        sampleUsers = DataStore.sampleUsers();
        samplePosts = DataStore.samplePosts();
        sampleWishlistSkeleton = DataStore.sampleWishlistPosts();
    }

    @BeforeAll
    static void connectToDatabase() throws URISyntaxException {
        Database.USE_TEST_DATABASE = true; // use test dataset
        Database.main(null); // reset dataset and add samples
        jdbi = Database.getJdbi();
    }

    @BeforeEach
    void injectDependency() throws URISyntaxException {
        Database.truncateTables(jdbi);
        Database.insertSampleUsers(jdbi, sampleUsers);
        Database.insertSamplePosts(jdbi, samplePosts);
        Database.insertSampleWishlistPosts(jdbi, sampleWishlistSkeleton);
        userDao = new JdbiUserDao(jdbi);
    }

    @AfterAll
    static void setUseProductionDatabase() {
        Database.USE_TEST_DATABASE = false; // use production dataset
    }

    @Test
    void doNothing() {

    }

    @Test
    @DisplayName("send an email.")
    void updateSendMail() throws IOException {
        WishlistEmails.basicWishlistUpdateEmail(jdbi, "000000000000000000000000000000000000");
    }

    @Test
    @DisplayName("send an email.")
    void updateSendStyledMail() throws IOException {
        WishlistEmails.styledWishlistUpdateEmail(jdbi, "000000000000000000000000000000000000");
    }

}
