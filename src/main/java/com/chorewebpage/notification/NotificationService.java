package com.chorewebpage.notification;

import com.chorewebpage.config.KidDirectoryProperties;
import com.chorewebpage.config.SmsProperties;
import com.chorewebpage.model.Chore;
import com.chorewebpage.model.Kid;
import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d 'at' h:mma");

    private final JavaMailSender mailSender;
    private final KidDirectoryProperties kidDirectoryProperties;
    private final SmsProperties smsProperties;

    public NotificationService(
            JavaMailSender mailSender,
            KidDirectoryProperties kidDirectoryProperties,
            SmsProperties smsProperties) {
        this.mailSender = mailSender;
        this.kidDirectoryProperties = kidDirectoryProperties;
        this.smsProperties = smsProperties;
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
        sendSms(contact.getPhoneNumber(), messageBody);
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

    private void sendSms(String toNumber, String messageBody) {
        if (!smsProperties.isEnabled() || !StringUtils.hasText(toNumber)) {
            return;
        }
        if (!StringUtils.hasText(smsProperties.getAccountSid())
                || !StringUtils.hasText(smsProperties.getAuthToken())
                || !StringUtils.hasText(smsProperties.getFromNumber())) {
            log.warn("SMS notification skipped because Twilio is not fully configured.");
            return;
        }
        try {
            Twilio.init(smsProperties.getAccountSid(), smsProperties.getAuthToken());
            Message.creator(new PhoneNumber(toNumber), new PhoneNumber(smsProperties.getFromNumber()), messageBody)
                    .create();
            log.info("Sent SMS notification to {}", toNumber);
        } catch (ApiException ex) {
            log.warn("Unable to send SMS to {}: {}", toNumber, ex.getMessage());
        }
    }
}
