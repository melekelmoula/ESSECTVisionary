package com.devmate.java;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;


public class EmailUtil {

    public static void sendEmail(String to, String subject, String body) {
        final String username = "Your Email"; // your email
        final String password = "Your Password"; // your email password

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.office365.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("Your Email"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
