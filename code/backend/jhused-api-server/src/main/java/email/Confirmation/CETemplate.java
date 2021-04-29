package email.Confirmation;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.ASM;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

public class CETemplate {

    /**
     * Returns the most basic text email.
     * @param oldUserEmail to be notified
     * @return mail
     */
    public static Mail basicCEEmail(String oldUserEmail) {
        Email from = new Email("jhusedemail@gmail.com", "JHUsed");
        String subject = "Email changed";
        Email to = new Email(oldUserEmail);
        Content content = new Content("text/plain", "Your email has been changed! If this was not you, please reply to this email and we will fix the problem. We strongly recommend changing your account password immediately to prevent further " +
                "unauthorized access.");
        Mail mail = new Mail(from, subject, to, content);

        return mail;
    }

    /**
     * Returns a styled email.
     * @param oldUserEmail to be notified
     * @return mail
     */
    public static Mail styledCEEmail(String oldUserEmail) {
        Email from = new Email("jhusedemail@gmail.com", "JHUsed");
        String subject = "Email changed";
        Email to = new Email(oldUserEmail);
        Content content = new Content("text/plain", "Your email has been changed! If this was not you, please reply to this email and we will fix the problem. We strongly recommend changing your account password immediately to prevent further " +
                "unauthorized access.");
        Mail mail = new Mail(from, subject, to, content);

        //set the templateId from the sendgrid website.
        mail.setTemplateId("d-f79d67f0ead94ff08bdff7bfef53ebd6");

        return mail;
    }
}
