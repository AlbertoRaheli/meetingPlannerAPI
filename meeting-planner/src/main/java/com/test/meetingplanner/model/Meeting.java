package com.test.meetingplanner.model;

import java.time.LocalDateTime;
import java.util.List;

public class Meeting {
    private String meetingId;
    private String title;
    private List<User> participants;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Meeting(){}
    public Meeting(String meetingId, String title, List<User> participants, LocalDateTime startTime, LocalDateTime endTime) {
        this.meetingId = meetingId;
        this.title = title;
        this.participants = participants;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
