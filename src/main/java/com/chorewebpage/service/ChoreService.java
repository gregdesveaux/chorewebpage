package com.chorewebpage.service;

import com.chorewebpage.model.Chore;
import com.chorewebpage.model.Kid;
import com.chorewebpage.repository.ChoreRepository;
import com.chorewebpage.notification.NotificationService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChoreService {

    private static final Logger log = LoggerFactory.getLogger(ChoreService.class);

    private final ChoreRepository choreRepository;
    private final NotificationService notificationService;

    public ChoreService(ChoreRepository choreRepository, NotificationService notificationService) {
        this.choreRepository = choreRepository;
        this.notificationService = notificationService;
    }

    public List<Chore> listChores() {
        return choreRepository.findAll();
    }

    @Transactional
    public Optional<Chore> markDone(Long id, Kid completedBy) {
        return choreRepository
                .findById(id)
                .map(chore -> {
                    LocalDateTime completionTime = LocalDateTime.now();
                    chore.setLastCompletedAt(completionTime);
                    chore.setLastCompletedBy(completedBy);
                    chore.setNextDueDate(completionTime.plusDays(chore.getFrequency().getDays()));
                    chore.setAssignedTo(chore.getAssignedTo().otherKid());
                    Chore saved = choreRepository.save(chore);
                    log.info(
                            "Chore '{}' completed by {}. Next due {} for {}",
                            saved.getName(),
                            completedBy,
                            saved.getNextDueDate(),
                            saved.getAssignedTo());
                    return saved;
                });
    }

    public void notifyDueChores() {
        LocalDateTime now = LocalDateTime.now();
        List<Chore> dueChores = choreRepository.findByNextDueDateLessThanEqual(now);
        dueChores.forEach(notificationService::notifyChoreDue);
    }
}
