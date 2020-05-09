package CalendarCore.exceptions;

public class TestSyntaxException extends Throwable {
    String specificProblem;

    public TestSyntaxException(String error) {
        specificProblem = error;
    }

    @Override
    public String toString() {
        return "TestSyntax Exception: The test data's syntax is incorrect and cannot be parsed.  Reason: " + specificProblem;
    }
}
