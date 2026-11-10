package studyspot;

import java.time.LocalDateTime;

/** One booking of one room. Bookings never change; the store replaces a booking when its status changes. */
public record Booking(int bookingId, int roomId, String studentId,
                      LocalDateTime start, LocalDateTime end, BookingStatus status) {

    /** Returns a copy of this booking with a different status. */
    public Booking withStatus(BookingStatus newStatus) {
        return new Booking(bookingId, roomId, studentId, start, end, newStatus);
    }
}
