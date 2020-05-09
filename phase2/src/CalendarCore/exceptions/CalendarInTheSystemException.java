package CalendarCore.exceptions;

public class CalendarInTheSystemException extends Throwable {
    @Override
    public String toString() {
        return "Calendar already exists in the system.";
    }
}
