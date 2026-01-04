package com.chorewebpage.notification;

import com.chorewebpage.config.KidDirectoryProperties;
import com.chorewebpage.model.Chore;
import com.chorewebpage.model.Kid;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d 'at' h:mma");

    private final JavaMailSender mailSender;
    private final KidDirectoryProperties kidDirectoryProperties;
    private final FirebaseMessaging firebaseMessaging;

    public NotificationService(
            JavaMailSender mailSender,
            KidDirectoryProperties kidDirectoryProperties,
            @Nullable FirebaseMessaging firebaseMessaging) {
        this.mailSender = mailSender;
        this.kidDirectoryProperties = kidDirectoryProperties;
        this.firebaseMessaging = firebaseMessaging;
    }

    public void notifyChoreDue(Chore chore) {
        Kid assignedKid = chore.getAssignedTo();
        KidContact contact = kidDirectoryProperties.getContactFor(assignedKid);
        String prettyKidName = contact.getName() != null ? contact.getName() : assignedKid.name();

        String messageBody = "Hi "
                + prettyKidName
                + ", it is your turn for the chore: "
                + chore.getName()
                + ". It is due "
                + FORMATTER.format(chore.getNextDueDate())
                + ".";

        sendEmail(contact.getEmail(), "Chore reminder: " + chore.getName(), messageBody);
        sendPush(contact.getFcmToken(), chore.getName(), messageBody);
    }

    private void sendEmail(String toAddress, String subject, String messageBody) {
        if (!StringUtils.hasText(toAddress)) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toAddress);
            message.setSubject(subject);
            message.setText(messageBody);
            mailSender.send(message);
            log.info("Sent email notification to {}", toAddress);
        } catch (Exception e) {
            log.warn("Unable to send email to {}: {}", toAddress, e.getMessage());
        }
    }

    private void sendPush(String fcmToken, String title, String messageBody) {
        if (firebaseMessaging == null || !StringUtils.hasText(fcmToken)) {
            return;
        }
        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder().setTitle(title).setBody(messageBody).build())
                    .build();
            firebaseMessaging.send(message);
            log.info("Sent Firebase push notification to token {}", fcmToken);
        } catch (Exception ex) {
            log.warn("Unable to send push notification to token {}: {}", fcmToken, ex.getMessage());
        }
    }
}
