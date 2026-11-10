package studyspot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data layer: keeps rooms and bookings in memory.
 * This class only stores and finds data. It does not check any booking rules.
 */
public class BookingStore {
    private final Map<Integer, Room> rooms = new LinkedHashMap<>();
    private final Map<Integer, Booking> bookings = new LinkedHashMap<>();
    private int nextBookingId = 1;

    public BookingStore() {
        addRoom(new Room(1, "Room A", 4));
        addRoom(new Room(2, "Room B", 4));
        addRoom(new Room(3, "Room C", 6));
        addRoom(new Room(4, "Room D", 6));
        addRoom(new Room(5, "Room E", 8));
        addRoom(new Room(6, "Room F", 10));
    }

    private void addRoom(Room room) {
        rooms.put(room.roomId(), room);
    }

    // ---- rooms

    public List<Room> listRooms() {
        return new ArrayList<>(rooms.values());
    }

    /** Returns the room, or null if there is no room with that number. */
    public Room getRoom(int roomId) {
        return rooms.get(roomId);
    }

    // ---- bookings

    public Booking addBooking(int roomId, String studentId, LocalDateTime start, LocalDateTime end) {
        Booking b = new Booking(nextBookingId++, roomId, studentId, start, end, BookingStatus.BOOKED);
        bookings.put(b.bookingId(), b);
        return b;
    }

    /** Returns the booking, or null if there is no booking with that number. */
    public Booking getBooking(int bookingId) {
        return bookings.get(bookingId);
    }

    /** All bookings for one room that have the given status. */
    public List<Booking> bookingsForRoom(int roomId, BookingStatus status) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings.values()) {
            if (b.roomId() == roomId && b.status() == status) {
                result.add(b);
            }
        }
        return result;
    }

    /** All bookings made by one student, earliest first. */
    public List<Booking> bookingsForStudent(String studentId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings.values()) {
            if (b.studentId().equals(studentId)) {
                result.add(b);
            }
        }
        result.sort(Comparator.comparing(Booking::start));
        return result;
    }

    public void setStatus(int bookingId, BookingStatus status) {
        Booking b = bookings.get(bookingId);
        if (b != null) {
            bookings.put(bookingId, b.withStatus(status));
        }
    }
}
