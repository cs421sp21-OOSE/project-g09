package email.Confirmation;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.*;

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

        ASM asm = new ASM();
        asm.setGroupId(16635);
        mail.setASM(asm);


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
        Content content = new Content("text/html", "Your email has been changed! If this was not you, please reply to this email and we will fix the problem. We strongly recommend changing your account password immediately to prevent further " +
                "unauthorized access.");
        Mail mail = new Mail(from, subject, to, content);

        //set the templateId from the sendgrid website.
        mail.setTemplateId("d-bbc8e61119514a60be047bb9e913e50e");

        //assign the unsubscribe groupId
        ASM asm = new ASM();
        asm.setGroupId(16635);
        mail.setASM(asm);

        //create the mailSettings object
        MailSettings settings = new MailSettings();

        //create and assign the basic setting object to true
        Setting setting = new Setting();
        setting.setEnable(true);

        //set this email's bypass list management to true.
        settings.setBypassListManagement(setting);
        mail.setMailSettings(settings);


        return mail;
    }
}
