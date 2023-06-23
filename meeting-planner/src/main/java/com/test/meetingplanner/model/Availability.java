package com.test.meetingplanner.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Availability {
    private List<String> emails;
    private int days;
    private int numberOfSlots;

    private LocalDate startDate;

    public Availability() {
    }
    public Availability(List<String> emails, int days, int numberOfSlots, LocalDate startDate) {
        this.emails = emails;
        this.days = days;
        this.numberOfSlots = numberOfSlots;
        this.startDate= startDate;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getNumberOfSlots() {
        return numberOfSlots;
    }

    public void setNumberOfSlots(int numberOfSlots) {
        this.numberOfSlots = numberOfSlots;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

}
