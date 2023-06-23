package com.test.meetingplanner.controller;

import com.test.meetingplanner.model.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/meetingPlanner")
public class MeetingController {
    private static long idCounter = 0;
    public static synchronized String createID() {return String.valueOf(idCounter++);}

    private List<User> users = new ArrayList<>();
    private List<Meeting> meetings = new ArrayList<>();

    @GetMapping("/user/{email}")
    public User getUser(@PathVariable String email){
        Optional<User> optionalUser = users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();

        // error if user doesn't exist
        return optionalUser.orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }


    @PostMapping("/user")
    public User createUser(@RequestBody User user) {
        for (User usr : users) {
            if (usr.getEmail().equals(user.getEmail())) {
                throw new IllegalArgumentException("This email already exists");
            }
        }

        users.add(user);
        return user;
    }

    @PostMapping("/meeting")
    public Meeting createMeeting(@RequestBody Meeting meeting) {
        List<User> participants = new ArrayList<>();
        // Validate participants trough email address
        for (User participant : meeting.getParticipants()) {
            boolean exists = false;
            for (User usr : users) {
                if (usr.getEmail().equals(participant.getEmail())) {
                    participants.add(usr);
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                throw new IllegalArgumentException("Participant not found: " + participant.getEmail());
            }
        }
        // Check if the meeting start time is in the past
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (meeting.getStartTime().isBefore(currentDateTime)) {
            throw new IllegalArgumentException("Meeting start time cannot be in the past");
        }

        // Define working hours
        LocalTime startWorkingHour = LocalTime.of(8, 0);
        LocalTime endWorkingHour = LocalTime.of(17, 0);

        // Check if the meeting start within the working hours
        LocalTime meetingStartTime = meeting.getStartTime().toLocalTime();
        if (meetingStartTime.isBefore(startWorkingHour) || meetingStartTime.isAfter(endWorkingHour.minusHours(1))) {
            throw new IllegalArgumentException("Meeting start time should be within working hours (8:00 AM - 5:00 PM)");
        }

        // Validate start time (must be at the hour mark)
        if (meeting.getStartTime().getMinute() != 0 || meeting.getStartTime().getSecond() != 0) {
            throw new IllegalArgumentException("Meeting can start only at the hour mark");
        }

        // set duration (must be exactly one hour)
        LocalDateTime endTime= meeting.getStartTime().plusHours(1);
        meeting.setEndTime(endTime);

        // Generate unique ID
        String id = createID();
        meeting.setMeetingId(id);

        //add participant info from email (name)
        meeting.setParticipants(participants);

        // Add the meeting to the list
        meetings.add(meeting);

        return meeting;
    }

    @GetMapping("/user/{email}/calendar")
    public Calendar getCalendar (@PathVariable String email) {
        List<Meeting> personMeetings = new ArrayList<>();

        boolean isUserValid = false;
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                isUserValid = true;
                break;
            }
        }
        if (!isUserValid) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }

        // Find meetings where the person is a participant
        for (Meeting meeting : meetings) {
            for (User participant : meeting.getParticipants()) {
                if (participant.getEmail().equals(email)) {
                    personMeetings.add(meeting);
                    break;
                }
            }
        }

        Calendar calendar = new Calendar();
        calendar.setMeetings(personMeetings);

        return calendar;
    }

    @PostMapping("/availability")
    public List<Timeslot> getAvailableTimeslots(@RequestBody Availability availability) {
        List<User> participants = new ArrayList<>();

        // Add users by their emails
        for (String email : availability.getEmails()) {
            Optional<User> optionalUser = users.stream()
                    .filter(user -> user.getEmail().equals(email))
                    .findFirst();

            if (optionalUser.isEmpty()) {
                throw new IllegalArgumentException("User not found with email: " + email);
            }

            optionalUser.ifPresent(participants::add);
        }

        // restrict suggestion to working hours
        LocalTime startWorkingHour = LocalTime.of(9, 0);
        LocalTime endWorkingHour = LocalTime.of(18, 0);

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime= LocalTime.now();
        LocalDateTime currentDateTime = LocalDateTime.of(currentDate, currentTime);


        LocalDate startDate = availability.getStartDate();

        //Set start time to look for timeslots at the next hour mark if the meeting needs to be scheduled in this working day
        // else the startTime for the suggestion is 8am of whatever future starDate selected
        LocalTime timeTemp;
        if (startDate.isEqual(currentDate)){
            timeTemp = currentTime.plusHours(1).truncatedTo(ChronoUnit.HOURS);
        }
        else {
            timeTemp = startWorkingHour;
        }


        List<Timeslot> availableTimeslots = new ArrayList<>();

        // Generate available timeslots specified number of days
        for (int day = 0; day < availability.getDays(); day++) {
            LocalDate scheduleDate = startDate.plusDays(day);

            // Check working hours for each day
            for (LocalTime time = timeTemp; time.isBefore(endWorkingHour); time = time.plusHours(1)) {
                LocalDateTime startTime = LocalDateTime.of(scheduleDate, time);

                // Confirm that start time is in the future
                if (startTime.isAfter(currentDateTime)) {
                    LocalDateTime endTime = startTime.plusHours(1);

                    // Confirm timeslot doesn't conflict with existing meetings
                    boolean conflict = false;
                    for (Meeting meeting : meetings) {
                        // Check availability of participants during the timeslot
                        for (User participant : participants) {
                            if (meeting.getParticipants().contains(participant) && !(endTime.isBefore(meeting.getStartTime()) || endTime.isEqual(meeting.getStartTime()) || startTime.isEqual(meeting.getEndTime()) ||startTime.isAfter(meeting.getEndTime()))) {
                                conflict = true;
                                break;
                            }
                        }
                        if (conflict) {
                            break;
                        }
                    }

                    // Add the timeslot to the available timeslots list if no conflicts found
                    if (!conflict) {
                        availableTimeslots.add(new Timeslot(startTime, endTime));
                    }

                    // Break the loop if the required number of timeslots has been found
                    if (availableTimeslots.size() >= availability.getNumberOfSlots()) {
                        break;
                    }
                }
                else if (startTime.isBefore(currentDateTime)) {
                    throw new IllegalArgumentException("Meeting start time cannot be in the past");
                }
                timeTemp = startWorkingHour;
            }

            // Break the loop if the required number of timeslots has been found
            if (availableTimeslots.size() >= availability.getNumberOfSlots()) {
                break;
            }
            // Before going to next day set starTime at 8am
            timeTemp = startWorkingHour;
        }

        return availableTimeslots;
    }
}
