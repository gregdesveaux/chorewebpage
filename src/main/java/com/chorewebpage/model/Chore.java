package com.chorewebpage.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "chores")
public class Chore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Kid assignedTo;

    @Column(nullable = false)
    private LocalDateTime nextDueDate;

    private LocalDateTime lastCompletedAt;

    @Enumerated(EnumType.STRING)
    private Kid lastCompletedBy;

    public Chore() {
    }

    public Chore(String name, Frequency frequency, Kid assignedTo, LocalDateTime nextDueDate) {
        this.name = name;
        this.frequency = frequency;
        this.assignedTo = assignedTo;
        this.nextDueDate = nextDueDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Kid getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Kid assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDateTime getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDateTime nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public LocalDateTime getLastCompletedAt() {
        return lastCompletedAt;
    }

    public void setLastCompletedAt(LocalDateTime lastCompletedAt) {
        this.lastCompletedAt = lastCompletedAt;
    }

    public Kid getLastCompletedBy() {
        return lastCompletedBy;
    }

    public void setLastCompletedBy(Kid lastCompletedBy) {
        this.lastCompletedBy = lastCompletedBy;
    }
}
