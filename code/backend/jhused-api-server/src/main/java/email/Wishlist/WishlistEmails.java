package email.Wishlist;

import com.sendgrid.helpers.mail.Mail;
import dao.UserDao;
import dao.WishlistPostSkeletonDao;
import dao.jdbiDao.JdbiUserDao;
import dao.jdbiDao.JdbiWishlistPostSkeletonDao;
import email.SendMail;
import model.User;
import model.WishlistPostSkeleton;
import org.jdbi.v3.core.Jdbi;

import java.io.IOException;
import java.util.List;

/**
 * Class that controls all wishlist email sending.
 */
public class WishlistEmails {
    /**
     * Sends emails to all users who have wishlist-ed the post with postId
     * @param postId of updated post
     */
    public static void basicWishlistUpdateEmail(Jdbi jdbi, String postId) throws IOException {
        WishlistPostSkeletonDao skeletonDao = new JdbiWishlistPostSkeletonDao(jdbi);
        UserDao userDao = new JdbiUserDao(jdbi);


        //get the wishlistPostSkeletons.
        List<WishlistPostSkeleton> wishlistPostSkeletons = skeletonDao.readAllFromPostId(postId);

        //for debugging
        /*if(wishlistPostSkeletons.size() == 0) {
            System.out.println("No wishlist-ed posts!");
        }*/

        //TODO eventually need to change this over to a bulk email method.

        //for each skeleton, get the associated user.
        for(WishlistPostSkeleton skeleton : wishlistPostSkeletons) {
            //get one user who has this post wishlist-ed
            User currentUser = userDao.read(skeleton.getUserId());

            //create the email.
            Mail mail = WUETemplate.basicWUEEmail(currentUser.getEmail());

            //for debugging
            /*System.out.println("Sending mail to: " + currentUser.getEmail());*/

            //send the email.
            try {
                SendMail.main(mail);
            } catch (IOException e) {
                throw new IOException(e);
            }

        }

    }

    /**
     * Sends emails to all users who have wishlist-ed the post with postId
     * @param postId of updated post
     */
    public static void styledWishlistUpdateEmail(Jdbi jdbi, String postId) throws IOException {
        WishlistPostSkeletonDao skeletonDao = new JdbiWishlistPostSkeletonDao(jdbi);
        UserDao userDao = new JdbiUserDao(jdbi);


        //get the wishlistPostSkeletons.
        List<WishlistPostSkeleton> wishlistPostSkeletons = skeletonDao.readAllFromPostId(postId);

        //for debugging
        /*if(wishlistPostSkeletons.size() == 0) {
            System.out.println("No wishlist-ed posts!");
        }*/

        //TODO eventually need to change this over to a bulk email method.

        //for each skeleton, get the associated user.
        for(WishlistPostSkeleton skeleton : wishlistPostSkeletons) {
            //get one user who has this post wishlist-ed
            User currentUser = userDao.read(skeleton.getUserId());

            //create the styled email.
            Mail mail = WUETemplate.styledWUEEmail(currentUser.getEmail());

            //for debugging
            /*System.out.println("Sending mail to: " + currentUser.getEmail());*/

            //send the email.
            try {
                SendMail.main(mail);
            } catch (IOException e) {
                throw new IOException(e);
            }

        }

    }


}
