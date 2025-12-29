package com.chorewebpage.model;

public enum Frequency {
    DAILY(1),
    EVERY_THREE_DAYS(3);

    private final int days;

    Frequency(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
