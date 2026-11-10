# StudySpot starter code (Java)

A small room-booking program for the Final Project. Part of it works already; you will add the rest.

## What you need

- Java 17 or later
- Maven (most IDEs, such as IntelliJ IDEA, Eclipse and VS Code, include it)

You can also open this folder as a Maven project in your IDE and use its Run and Test buttons.

## Run the tests

From this folder:

```
mvn test
```

This also writes a test coverage report. Open `target/site/jacoco/index.html` in a browser to see it.

## Run the program

```
mvn compile
java -cp target/classes studyspot.Main
```

## How the code is organized

The code has three layers. Each layer only calls the layer below it.

| Layer | File | What it does |
| --- | --- | --- |
| User interface | `Main.java` | Shows the menu and reads what the user types |
| Logic | `BookingService.java` | Booking rules: what is and is not allowed |
| Data | `BookingStore.java` | Keeps rooms and bookings (in memory) |

`Room.java`, `Booking.java` and `BookingStatus.java` hold the data that all layers share. All files are in `src/main/java/studyspot/`. The tests are in `src/test/java/studyspot/BookingServiceTest.java`.

## What already works

- Showing free rooms for a given time
- Booking a room (but without any rules yet)
- Listing your bookings

## What you will add

Search the code for `TODO`. You will:

- reject bookings shorter than 30 minutes or longer than 120 minutes
- stop a room being booked twice for the same time
- make cancelling work
- later, handle the change request in the project worksheet
