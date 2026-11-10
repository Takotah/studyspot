package studyspot;

/** Thrown when a request breaks a booking rule. The message is shown to the user. */
public class BookingException extends RuntimeException {
    public BookingException(String message) {
        super(message);
    }
}
