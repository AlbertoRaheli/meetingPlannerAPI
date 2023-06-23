# Meeting Planner

Design and implement the API for a minimal scheduling application. This API should be able to handle the following requirements:

1) Create persons with a name and unique email.

2) Create meetings involving one or more persons at a given time slot.

3) A meeting can only start at the hour mark and only last exactly one hour.

4) Show the schedule, i.e., the upcoming meetings, for a given person.

5) Suggest one or more available timeslots for meetings given a group of persons.

You should not implement a GUI, a simple unit test or console demo should suffice. Also please try to keep the number of third party libraries to a minimum. 
Finally, please do not spend time on storing data in files or databases.

# Solution
The exercise has been solved in Java (version 17) using the [Spring Boot](https://spring.io/) framework (version 3.1.0) and 
all the API endpoints have been tested using the platform [Postman](https://www.postman.com/).

## Code Structure

The code is structured as follows:

- `MeetingController`: This class serves as the main controller for all the meeting planner API. It handles requests related to users, meetings, user calendar, and availability.

- `User`: This class represents a user with a name and a unique email address.

- `Meeting`: This class represents a meeting with participants, title, start time, end time, and a unique ID.

- `Calendar`: This class represents the calendar/schedule of a specific user and contains a list of their upcoming meetings.

- `Timeslot`: This class represents a generic time slot with a start date&time and end date&time.

- `Availability`: This class represents a list of common available Timeslots for a group of users. It has the possibility to set a maximum number of days before which the meeting must be scheduled and 
              how many suggestions you want to request 

## Setup and Usage

1. Clone the repository meeting-planner
2. Start the server running the file "src\main\java\com\test\meetingplanner\MeetingPlannerApplication.java"
3. The API will be accessible at `http://localhost:8080/meetingPlanner`.

## API Endpoints

### Create User

- Endpoint: `/meetingPlanner/user`
- Method: `POST`
- Description: Creates a new user.
- Request Body: User object containing user details. Examples:
  - {"name": "Alice", "email": "alice@deltek.com"}
  - {"name": "Bob", "email": "bob@deltek.com"}
- Response: The created user object if successful, or an error message if the user already exists.

### Get User

- Endpoint: `/meetingPlanner/user/{email}`
- Method: `GET`
- Description: Retrieves a user based on their email address.
- Parameters:
    - `email` (path parameter): The email address of the user (alice@deltek.com)
- Response: The user object if found, or an error message if the user is not found.

### Create Meeting

- Endpoint: `/meetingPlanner/meeting`
- Method: `POST`
- Description: Creates a new meeting.
- Request Body: Meeting object containing meeting details. Examples:
  - {"title": "Meeting alice&bob 1", "participants": [{"email": "alice@deltek.com"},{"email": "bob@deltek.com"}],"startTime": "2023-07-24T10:00:00"}
  - {"title": "Meeting alice", "participants": [{"email": "alice@deltek.com"}],"startTime": "2023-07-24T11:00:00"}
  - {"title": "Meeting bob", "participants": [{"email": "bob@deltek.com"}],"startTime": "2023-07-24T13:00:00"}
- Response: The created meeting object with additional information (meetingId, name of participants, endDate&Time) if successful, or an error message if any validation checks fail. Current validations:
  - user not found
  - startdate&Time is in the past
  - start time is not between working hours (8am-17pm)
  - the start time is not at the hour mark


### Get User Calendar

- Endpoint: `/meetingPlanner/user/{email}/calendar`
- Method: `GET`
- Description: Retrieves the calendar for a user based on their email address.
- Parameters:
    - `email` (path parameter): The email address of the user. (alice@deltek.com)
- Response: The calendar object containing the list of meetings object for the user if found, or an error message if the user is not found.

### Get Available Timeslots

- Endpoint: `/meetingPlanner/availability`
- Method: `POST`
- Description: Generates suggestion of available timeslots (between working hours) for meetings based on participant already scheduled meetings. 
- Request Body: Availability object specifying participant emails, number of days before which the meeting must be scheduled , the desired number of timeslots and a starting date for  when to start suggesting timeslots. 
If the starting date is today the suggested timeslots start at the next hour mark. Examples:
  - {"emails": ["alice@deltek.com", "bob@deltek.com"], "days": 2, "numberOfSlots": 8, "startDate":"2023-07-24"}
  - {"emails": ["alice@deltek.com", "bob@deltek.com"], "days": 2, "numberOfSlots": 8, "startDate": changetoCURRENTDATE}
- Response: The list of available timeslots if successful, or an error message if any validation checks fail. Current validations:
  - user not found
  - startdate&Time is in the past







