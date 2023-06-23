package com.test.meetingplanner.model;

import java.util.List;

public class Calendar {
    private List<Meeting> meetings;
    public Calendar() {
    }
    public Calendar(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }


}
