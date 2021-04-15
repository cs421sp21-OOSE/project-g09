package email;

import dao.PostDao;
import dao.jdbiDao.JdbiPostDao;
import model.Post;
import model.User;
import email.WishlistEmails;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import util.database.DataStore;
import util.database.Database;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class WishlistEmailsTest {
    private static List<Post> samples;
    private static List<User> sampleUsers;
    private PostDao postDao;
    private static Jdbi jdbi;

    @BeforeAll
    static void setSamplePosts() {
        samples = DataStore.samplePosts();
        sampleUsers = DataStore.sampleUsers();
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
        Database.insertSamplePosts(jdbi, samples);
        postDao = new JdbiPostDao(jdbi);
    }

    @AfterAll
    static void setUseProductionDatabase() {
        Database.USE_TEST_DATABASE = false; // use production dataset
    }

    //@Test
    void doNothing() {

    }

    @Test
    @DisplayName("send an email.")
    void updateSendMail() throws IOException {
        WishlistEmails.basicWishlistUpdateEmail(jdbi, "000000000000000000000000000000000000");
    }

}
