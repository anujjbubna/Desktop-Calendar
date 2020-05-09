package CalendarCore.exceptions;

public class DateNotSupportedException extends RuntimeException {
    public DateNotSupportedException() {
        super("This DateTime Has Exited the Accepted Range");
    }
}
