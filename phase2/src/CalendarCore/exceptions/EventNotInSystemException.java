package CalendarCore.exceptions;

public class EventNotInSystemException extends Throwable {
    @Override
    public String toString() {
        return "Event does not exist in the system";
    }
}
