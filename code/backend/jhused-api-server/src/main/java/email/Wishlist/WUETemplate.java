package email.Wishlist;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

/**
 * WISHLIST UPDATE EMAIL TEMPLATE
 */
public class WUETemplate {

    /**
     * Returns the most basic text email.
     * @param userEmail
     * @return
     */
    public static Mail basicWUEEmail(String userEmail) {
        Email from = new Email("jhusedemail@gmail.com", "JHUsed");
        String subject = "JHUsed Wishlist Update";
        Email to = new Email(userEmail);
        Content content = new Content("text/plain", "One of your wishlist-ed posts has been updated!");
        Mail mail = new Mail(from, subject, to, content);
        return mail;
    }

    /**
     * Returns the styled update email.
     * @param userEmail
     * @return
     */
    public static Mail styledWUEEmail(String userEmail) {
        Email from = new Email("jhusedemail@gmail.com", "JHUsed");
        String subject = "JHUsed Wishlist Update";
        Email to = new Email(userEmail);
        Content content = new Content("text/html", "One of your wishlist posts has been updated!");
        Mail mail = new Mail(from, subject, to, content);

        //set the templateId from the sendgrid website.
        mail.setTemplateId("d-ba70e2cbac7b4a279e30a5e5119fb5bb");

        return mail;
    }



}
