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
    @Test
    void bookingThatStartsInThePastIsRejected() {
        LocalDateTime now = START.plusDays(1);   // pretend it is the day after START
        assertThrows(BookingException.class, () -> service.book("S1", 1, START, 60, now));
    }

    @Test
    void bookingWithAnEmptyStudentIdIsRejected() {
        assertThrows(BookingException.class, () -> service.book("  ", 1, START, 60));
    }
    @Test
    void bookingIsCancelledWhenNotCheckedInWithin15Minutes() {
        service.book("S1", 1, START, 60);
        service.cancelNoShows(START.plusMinutes(15));
        assertEquals(BookingStatus.CANCELLED, service.bookingsForStudent("S1").get(0).status());
    }

    @Test
    void bookingIsNotCancelledBefore15MinutesHavePassed() {
        service.book("S1", 1, START, 60);
        service.cancelNoShows(START.plusMinutes(14));
        assertEquals(BookingStatus.BOOKED, service.bookingsForStudent("S1").get(0).status());
    }

    @Test
    void checkedInBookingIsNotCancelledAsNoShow() {
        Booking b = service.book("S1", 1, START, 60);
        service.checkIn(b.bookingId(), "S1");
        service.cancelNoShows(START.plusMinutes(30));
        assertEquals(BookingStatus.CHECKED_IN, service.bookingsForStudent("S1").get(0).status());
    }

    @Test
    void checkedInRoomIsStillNotFree() {
        Booking b = service.book("S1", 1, START, 60);
        service.checkIn(b.bookingId(), "S1");
        assertFalse(service.freeRooms(START, START.plusMinutes(60)).stream()
                .anyMatch(r -> r.roomId() == 1));
    }
}