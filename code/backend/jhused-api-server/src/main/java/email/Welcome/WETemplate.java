package email.Welcome;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.ASM;
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
        Content content = new Content("text/plain", "Welcome to JHUsed!");
        Mail mail = new Mail(from, subject, to, content);
        return mail;
    }

    /**
     * Returns the styled welcome email
     * @param userEmail
     * @return
     */
    public static Mail styledWEEmail(String userEmail) {
        Email from = new Email("jhusedemail@gmail.com", "JHUsed");
        String subject = "Welcome to JHUsed";
        Email to = new Email(userEmail);
        Content content = new Content("text/html", "Welcome to JHUsed!");
        Mail mail = new Mail(from, subject, to, content);

        //set the templateId from the sendgrid website.
        mail.setTemplateId("d-f79d67f0ead94ff08bdff7bfef53ebd6");

        ASM asm = new ASM();
        asm.setGroupId(16722);
        mail.setASM(asm);

        return mail;
    }




}
