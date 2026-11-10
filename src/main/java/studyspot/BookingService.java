package studyspot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Logic layer: the booking rules live here.
 * The user interface (Main) calls this class; this class calls the data layer (BookingStore).
 * Look for the TODO comments: those are the parts you will write.
 */
public class BookingService {
    private final BookingStore store;

    public BookingService(BookingStore store) {
        this.store = store;
    }

    /** True if the time period a overlaps the time period b. */
    public static boolean overlaps(LocalDateTime startA, LocalDateTime endA,
                                   LocalDateTime startB, LocalDateTime endB) {
        return startA.isBefore(endB) && startB.isBefore(endA);
    }

    /** Returns the rooms that have no active booking overlapping start to end. */
    public List<Room> freeRooms(LocalDateTime start, LocalDateTime end) {
        List<Room> free = new ArrayList<>();
        for (Room room : store.listRooms()) {
            boolean busy = false;
            for (Booking b : store.bookingsForRoom(room.roomId(), BookingStatus.BOOKED)) {
                if (overlaps(start, end, b.start(), b.end())) {
                    busy = true;
                }
            }
            if (!busy) {
                free.add(room);
            }
        }
        return free;
    }

    /** Books a room and returns the new booking. */
    public Booking book(String studentId, int roomId, LocalDateTime start, int minutes) {
        if (store.getRoom(roomId) == null) {
            throw new BookingException("There is no room " + roomId + ".");
        }
        LocalDateTime end = start.plusMinutes(minutes);

        // TODO (Task 4): a booking must be between 30 and 120 minutes long.
        // TODO (Task 4): a room must never be booked twice for the same time.

        return store.addBooking(roomId, studentId, start, end);
    }

    /** Cancels a booking. Students may cancel only their own bookings. */
    public void cancel(int bookingId, String studentId) {
        // TODO (Task 4): implement cancelling.
        throw new UnsupportedOperationException("Cancelling is not written yet.");
    }

    public List<Booking> bookingsForStudent(String studentId) {
        return store.bookingsForStudent(studentId);
    }
}
