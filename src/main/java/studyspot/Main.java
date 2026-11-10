package studyspot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * User interface layer: a simple text menu.
 * This class only talks to the user and calls the logic layer (BookingService).
 */
public class Main {
    private static final DateTimeFormatter INPUT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter SHOW = DateTimeFormatter.ofPattern("EEE dd MMM HH:mm");

    private final Scanner in = new Scanner(System.in);
    private final BookingService service = new BookingService(new BookingStore());
    private String studentId;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        studentId = ask("Student ID: ");
        while (true) {
            System.out.println("""

                    StudySpot
                      1. Show free rooms
                      2. Book a room
                      3. Cancel a booking
                      4. My bookings
                      0. Quit""");
            String choice = ask("Choose: ");
            try {
                switch (choice) {
                    case "0" -> { return; }
                    case "1" -> showFreeRooms();
                    case "2" -> bookRoom();
                    case "3" -> cancelBooking();
                    case "4" -> myBookings();
                    default -> System.out.println("Please choose 0 to 4.");
                }
            } catch (BookingException e) {
                System.out.println("Sorry: " + e.getMessage());
            } catch (NumberFormatException | DateTimeParseException e) {
                System.out.println("Sorry, that input was not valid.");
            } catch (UnsupportedOperationException e) {
                System.out.println("Not available yet: " + e.getMessage());
            }
        }
    }

    private String ask(String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine().trim() : "0";
    }

    private LocalDateTime askTime(String prompt) {
        return LocalDateTime.parse(ask(prompt + " (yyyy-MM-dd HH:mm): "), INPUT);
    }

    private void showFreeRooms() {
        LocalDateTime start = askTime("Start time");
        int minutes = Integer.parseInt(ask("Length in minutes: "));
        List<Room> rooms = service.freeRooms(start, start.plusMinutes(minutes));
        if (rooms.isEmpty()) {
            System.out.println("No rooms are free then.");
        }
        for (Room r : rooms) {
            System.out.printf("  %d. %s (seats %d)%n", r.roomId(), r.name(), r.capacity());
        }
    }

    private void bookRoom() {
        int roomId = Integer.parseInt(ask("Room number: "));
        LocalDateTime start = askTime("Start time");
        int minutes = Integer.parseInt(ask("Length in minutes: "));
        Booking b = service.book(studentId, roomId, start, minutes);
        System.out.printf("Booked! Booking number %d: room %d, %s to %s.%n",
                b.bookingId(), b.roomId(), b.start().format(SHOW), b.end().toLocalTime());
    }

    private void cancelBooking() {
        int bookingId = Integer.parseInt(ask("Booking number to cancel: "));
        service.cancel(bookingId, studentId);
        System.out.println("Booking cancelled.");
    }

    private void myBookings() {
        List<Booking> bookings = service.bookingsForStudent(studentId);
        if (bookings.isEmpty()) {
            System.out.println("You have no bookings.");
        }
        for (Booking b : bookings) {
            System.out.printf("  #%d: room %d, %s to %s [%s]%n", b.bookingId(), b.roomId(),
                    b.start().format(SHOW), b.end().toLocalTime(), b.status());
        }
    }
}
