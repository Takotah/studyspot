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
    private static final int NO_SHOW_MINUTES = 15;

    public BookingService(BookingStore store) {
        this.store = store;
    }

    /** True if the time period a overlaps the time period b. */
    public static boolean overlaps(LocalDateTime startA, LocalDateTime endA,
                                   LocalDateTime startB, LocalDateTime endB) {
        return startA.isBefore(endB) && startB.isBefore(endA);
    }

    /** Bookings that currently hold a room: booked, or checked in */
    private List<Booking> activeBookingsForRoom(int roomId) {
    	List<Booking> active = new ArrayList<>();
    	active.addAll(store.bookingsForRoom(roomId,  BookingStatus.BOOKED));
    	active.addAll(store.bookingsForRoom(roomId,  BookingStatus.CHECKED_IN));
    	return active; 
    }
    /** Returns the rooms that have no active booking overlapping start to end. */
    public List<Room> freeRooms(LocalDateTime start, LocalDateTime end) {
        List<Room> free = new ArrayList<>();
        for (Room room : store.listRooms()) {
            boolean busy = false;
            for (Booking b : activeBookingsForRoom(room.roomId())) {
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
        return book(studentId, roomId, start, minutes, LocalDateTime.now());
    }

    /** Books a room. The current time is passed in so tests can choose it. */
    public Booking book(String studentId, int roomId, LocalDateTime start, int minutes,
                        LocalDateTime now) {
        if (studentId == null || studentId.isBlank()) {
            throw new BookingException("A student ID is required.");
        }
        if (store.getRoom(roomId) == null) {
            throw new BookingException("There is no room " + roomId + ".");
        }
        if (start.isBefore(now)) {
            throw new BookingException("Bookings cannot start in the past.");
        }
        LocalDateTime end = start.plusMinutes(minutes);

        if (minutes < 30 || minutes > 120) {
            throw new BookingException("Bookings must be between 30 and 120 minutes long.");
        }
        for (Booking b : activeBookingsForRoom(roomId)) {
            if (overlaps(start, end, b.start(), b.end())) {
                throw new BookingException("Room " + roomId + " is already booked for that time.");
            }
        }

        return store.addBooking(roomId, studentId, start, end);
    }

    	/** Cancels a booking. Students may cancel only their own bookings. */
    	public void cancel(int bookingId, String studentId) {
    	    Booking b = store.getBooking(bookingId);
    	    if (b == null) {
    	        throw new BookingException("There is no booking " + bookingId + ".");
    	    }
    	    if (!b.studentId().equals(studentId)) {
    	        throw new BookingException("You can only cancel your own bookings.");
    	    }
    	    if (b.status() != BookingStatus.BOOKED) {
    	        throw new BookingException("Only booked bookings can be cancelled.");
    	    }
    	    store.setStatus(bookingId, BookingStatus.CANCELLED);
    	}
    	
        /** A student checks in to their own booking. */
        public void checkIn(int bookingId, String studentId) {
            Booking b = store.getBooking(bookingId);
            if (b == null) {
                throw new BookingException("There is no booking " + bookingId + ".");
            }
            if (!b.studentId().equals(studentId)) {
                throw new BookingException("You can only check in to your own bookings.");
            }
            if (b.status() != BookingStatus.BOOKED) {
                throw new BookingException("Only booked bookings can be checked in.");
            }
            store.setStatus(bookingId, BookingStatus.CHECKED_IN);
        }

        /** Cancels every booking that is still BOOKED 15 minutes or more after its start time. */
        public void cancelNoShows(LocalDateTime now) {
            for (Room room : store.listRooms()) {
                for (Booking b : store.bookingsForRoom(room.roomId(), BookingStatus.BOOKED)) {
                    if (!now.isBefore(b.start().plusMinutes(NO_SHOW_MINUTES))) {
                        store.setStatus(b.bookingId(), BookingStatus.CANCELLED);
                    }
                }
            }
        }
        

    public List<Booking> bookingsForStudent(String studentId) {
        return store.bookingsForStudent(studentId);
    }
}
