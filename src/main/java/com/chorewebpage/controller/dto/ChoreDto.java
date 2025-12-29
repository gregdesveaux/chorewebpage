package com.chorewebpage.controller.dto;

import com.chorewebpage.model.Chore;
import com.chorewebpage.model.Frequency;
import com.chorewebpage.model.Kid;
import java.time.LocalDateTime;
import java.util.function.Function;

public class ChoreDto {
    private Long id;
    private String name;
    private Frequency frequency;
    private Kid assignedTo;
    private String assignedToName;
    private LocalDateTime nextDueDate;
    private LocalDateTime lastCompletedAt;
    private Kid lastCompletedBy;
    private String lastCompletedByName;
    private String kidOneName;
    private String kidTwoName;

    public static ChoreDto fromEntity(Chore chore, Function<Kid, String> kidNameProvider) {
        ChoreDto dto = new ChoreDto();
        dto.id = chore.getId();
        dto.name = chore.getName();
        dto.frequency = chore.getFrequency();
        dto.assignedTo = chore.getAssignedTo();
        dto.assignedToName = kidNameProvider.apply(chore.getAssignedTo());
        dto.nextDueDate = chore.getNextDueDate();
        dto.lastCompletedAt = chore.getLastCompletedAt();
        dto.lastCompletedBy = chore.getLastCompletedBy();
        dto.lastCompletedByName = chore.getLastCompletedBy() != null
                ? kidNameProvider.apply(chore.getLastCompletedBy())
                : null;
        dto.kidOneName = kidNameProvider.apply(Kid.ONE);
        dto.kidTwoName = kidNameProvider.apply(Kid.TWO);
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public Kid getAssignedTo() {
        return assignedTo;
    }

    public LocalDateTime getNextDueDate() {
        return nextDueDate;
    }

    public LocalDateTime getLastCompletedAt() {
        return lastCompletedAt;
    }

    public Kid getLastCompletedBy() {
        return lastCompletedBy;
    }

    public String getLastCompletedByName() {
        return lastCompletedByName;
    }

    public String getKidOneName() {
        return kidOneName;
    }

    public String getKidTwoName() {
        return kidTwoName;
    }
}
