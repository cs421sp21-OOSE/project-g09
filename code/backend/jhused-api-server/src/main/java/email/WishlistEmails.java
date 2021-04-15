package email;

import com.sendgrid.helpers.mail.Mail;
import dao.jdbiDao.JdbiUserDao;
import dao.jdbiDao.JdbiWishlistPostSkeletonDao;
import model.User;
import email.WUETemplate;
import model.WishlistPostSkeleton;
import org.jdbi.v3.core.Jdbi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that controls all wishlist email sending.
 */
public class WishlistEmails {

    public WishlistEmails() {
        //this class should not be instantiated!
    }

    /**
     * Sends emails to all users who have wishlist-ed the post with postId
     * @param postId of updated post
     */
    public static void basicWishlistUpdateEmail(Jdbi jdbi, String postId) throws IOException {
        //create the skeleton dao.
        JdbiWishlistPostSkeletonDao skeletonDao = new JdbiWishlistPostSkeletonDao(jdbi);

        //create the user dao.
        JdbiUserDao userDao = new JdbiUserDao(jdbi);

        //get the wishlistPostSkeletons.
        List<WishlistPostSkeleton> updatedPostSkeletons =  skeletonDao.readAllFromPostId(postId);

        //create the user list.
        //List<User> users = new ArrayList<>();

        //TODO eventually need to change this over to a bulk email method.

        //for each skeleton, get the associated user.
        for(WishlistPostSkeleton skeleton : updatedPostSkeletons) {
            //get one user who has this post wishlist-ed
            User currentUser = userDao.read(skeleton.getUserId());

            //create the email.
            Mail mail = WUETemplate.basicWUEEmail(currentUser.getEmail());

            //send the email.
            try {
                SendMail.main(mail);
            } catch (IOException e) {
                throw new IOException(e);
            }

        }

    }


}
