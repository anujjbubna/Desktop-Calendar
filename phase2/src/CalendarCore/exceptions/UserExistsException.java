package CalendarCore.exceptions;

public class UserExistsException extends Throwable {
    public String toString() {
        return "User already exists.";
    }
}

