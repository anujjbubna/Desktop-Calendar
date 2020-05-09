package CalendarCore.exceptions;

public class CalendarNotInTheSystemException extends Throwable {
    @Override
    public String toString() {
        return "Calendar of this name does not exists.";
    }
}
