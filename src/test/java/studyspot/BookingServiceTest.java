package studyspot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Example tests. Run them with:  mvn test
 * Add your own tests below these (Task 6).
 */
class BookingServiceTest {
    private static final LocalDateTime START = LocalDateTime.of(2030, 1, 15, 10, 0);
    private BookingService service;

    @BeforeEach
    void setUp() {
        // a fresh, empty store for every test
        service = new BookingService(new BookingStore());
    }

    @Test
    void allRoomsAreFreeWhenThereAreNoBookings() {
        List<Room> free = service.freeRooms(START, START.plusMinutes(60));
        assertEquals(6, free.size());
    }

    @Test
    void bookedRoomIsNoLongerFree() {
        service.book("S1", 1, START, 60);
        List<Room> free = service.freeRooms(START, START.plusMinutes(60));
        assertFalse(free.stream().anyMatch(r -> r.roomId() == 1));
    }

    @Test
    void bookingAnUnknownRoomIsRejected() {
        assertThrows(BookingException.class, () -> service.book("S1", 99, START, 60));
    }
    @Test
    void bookingShorterThan30MinutesIsRejected() {
        assertThrows(BookingException.class, () -> service.book("S1", 1, START, 29));
    }

    @Test
    void bookingLongerThan120MinutesIsRejected() {
        assertThrows(BookingException.class, () -> service.book("S1", 1, START, 121));
    }

    @Test
    void doubleBookingTheSameRoomIsRejected() {
        service.book("S1", 1, START, 60);
        assertThrows(BookingException.class,
                () -> service.book("S2", 1, START.plusMinutes(30), 60));
    }

    @Test
    void studentCannotCancelAnotherStudentsBooking() {
        Booking b = service.book("S1", 1, START, 60);
        assertThrows(BookingException.class, () -> service.cancel(b.bookingId(), "S2"));
    }
}