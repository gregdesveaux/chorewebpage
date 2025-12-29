package com.chorewebpage.config;

import com.chorewebpage.service.ChoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ChoreNotificationScheduler {

    private static final Logger log = LoggerFactory.getLogger(ChoreNotificationScheduler.class);

    private final ChoreService choreService;

    public ChoreNotificationScheduler(ChoreService choreService) {
        this.choreService = choreService;
    }

    @Scheduled(fixedDelayString = "${chore.notifications.interval-ms:3600000}")
    public void sendDueChoreReminders() {
        log.debug("Running scheduled chore reminder check");
        choreService.notifyDueChores();
    }
}
