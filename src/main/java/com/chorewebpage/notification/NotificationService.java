package com.chorewebpage.notification;

import com.chorewebpage.config.KidDirectoryProperties;
import com.chorewebpage.config.NtfyProperties;
import com.chorewebpage.model.Chore;
import com.chorewebpage.model.Kid;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d 'at' h:mma");

    private final JavaMailSender mailSender;
    private final KidDirectoryProperties kidDirectoryProperties;
    private final RestClient ntfyRestClient;
    private final NtfyProperties ntfyProperties;

    public NotificationService(
            JavaMailSender mailSender,
            KidDirectoryProperties kidDirectoryProperties,
            RestClient ntfyRestClient,
            NtfyProperties ntfyProperties) {
        this.mailSender = mailSender;
        this.kidDirectoryProperties = kidDirectoryProperties;
        this.ntfyRestClient = ntfyRestClient;
        this.ntfyProperties = ntfyProperties;
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
        sendPush(contact.getNtfyTopic(), chore.getName(), messageBody);
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

    private void sendPush(String ntfyTopic, String title, String messageBody) {
        if (!StringUtils.hasText(ntfyTopic)) {
            return;
        }
        try {
            RestClient.RequestBodySpec request = ntfyRestClient
                    .post()
                    .uri("/{topic}", ntfyTopic)
                    .contentType(MediaType.TEXT_PLAIN)
                    .header("Title", title);

            if (StringUtils.hasText(ntfyProperties.getAccessToken())) {
                request = request.header(HttpHeaders.AUTHORIZATION, "Bearer " + ntfyProperties.getAccessToken());
            }

            request
                    .body(messageBody)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Sent ntfy notification to topic {}", ntfyTopic);
        } catch (Exception ex) {
            log.warn("Unable to send ntfy notification to topic {}: {}", ntfyTopic, ex.getMessage());
        }
    }
}
