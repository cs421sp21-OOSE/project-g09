package email.Confirmation;

import com.sendgrid.helpers.mail.Mail;
import email.SendMail;
import email.Welcome.WETemplate;

import java.io.IOException;

public class ConfirmationEmails {

    /**
     * Sends basic text emails to users
     * @param oldUserEmail to confirm change
     */
    public static void basicConfirmationEmail(String oldUserEmail) throws IOException {
        //create the email.
        Mail mail = CETemplate.basicCEEmail(oldUserEmail);

        //for debugging
        /*System.out.println("Sending mail to: " + oldUserEmail);*/

        //send the email.
        try {
            SendMail.main(mail);
        } catch (IOException e) {
            throw new IOException(e);
        }


    }
}
