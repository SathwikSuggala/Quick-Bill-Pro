package utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.*;
import jakarta.mail.util.ByteArrayDataSource;
import java.util.Properties;
import java.io.UnsupportedEncodingException;

public class EmailUtil {
    private static final String USERNAME = "leelasaiagencie@gmail.com";
    private static final String PASSWORD = "bjpt hovb keom iuas";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    public static void sendEmailWithAttachment(String toEmail, String subject, String body, byte[] imageBytes, String fileName) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(USERNAME, "LEELA SAI AGENCIES"));
        } catch (UnsupportedEncodingException e) {
            message.setFrom(new InternetAddress(USERNAME));
        }
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);

        // Email body
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(body, "text/html; charset=utf-8");

        // Image attachment
        MimeBodyPart imagePart = new MimeBodyPart();
        ByteArrayDataSource source = new ByteArrayDataSource(imageBytes, "image/png");
        imagePart.setDataHandler(new DataHandler(source));
        imagePart.setFileName(fileName);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(imagePart);

        message.setContent(multipart);

        try {
            Transport.send(message);
        } catch (SendFailedException sfe) {
            // Invalid address or similar
            throw new MessagingException("Invalid recipient address. Please check the email address and try again.", sfe);
        } catch (jakarta.mail.MessagingException me) {
            Throwable cause = me.getCause();
            if (cause != null && cause.getMessage() != null && cause.getMessage().toLowerCase().contains("unknownhostexception")) {
                throw new MessagingException("No internet connection. Please check your network and try again.", me);
            }
            if (me.getMessage() != null && (me.getMessage().toLowerCase().contains("could not connect") || me.getMessage().toLowerCase().contains("unknownhostexception"))) {
                throw new MessagingException("No internet connection. Please check your network and try again.", me);
            }
            throw new MessagingException("Failed to send email: " + me.getMessage(), me);
        } catch (Exception e) {
            throw new MessagingException("Unexpected error while sending email: " + e.getMessage(), e);
        }
    }
} 