package email.Welcome;

import com.sendgrid.helpers.mail.Mail;
import dao.UserDao;
import dao.WishlistPostSkeletonDao;
import dao.jdbiDao.JdbiUserDao;
import dao.jdbiDao.JdbiWishlistPostSkeletonDao;
import email.SendMail;
import email.Wishlist.WUETemplate;
import model.User;
import model.WishlistPostSkeleton;
import org.jdbi.v3.core.Jdbi;

import java.io.IOException;
import java.util.List;

public class WelcomeEmails {
    /**
     * Sends emails to new users
     * @param userEmail of new user
     */
    public static void basicWelcomeEmail(String userEmail) throws IOException {
        //create the email.
        Mail mail = WETemplate.basicWEEmail(userEmail);

        //for debugging
        /*System.out.println("Sending mail to: " + userEmail);*/

        //send the email.
        try {
            SendMail.main(mail);
        } catch (IOException e) {
            throw new IOException(e);
        }


    }

}
