package email.Welcome;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

/**
 * WELCOME EMAIL TEMPLATE
 */
public class WETemplate {

    /**
     * Returns the most basic text email.
     * @param userEmail
     * @return
     */
    public static Mail basicWEEmail(String userEmail) {
        Email from = new Email("jhusedemail@gmail.com", "JHUsed");
        String subject = "Welcome to JHUsed";
        Email to = new Email(userEmail);
        Content content = new Content("text/plain", "Welcome to JHUsed! We hope you enjoy your stay.");
        Mail mail = new Mail(from, subject, to, content);
        return mail;
    }




}
