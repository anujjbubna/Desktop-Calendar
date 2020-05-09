package CalendarCore.alerts;

import CalendarCore.dates.DateTime;

import java.io.Serializable;
import java.util.ArrayList;

public class AlertGenerator implements Serializable {
    public enum FREQUENCY {
        QUADHOURLY,
        BIHOURLY,
        HOURLY,
        DAILY,
        WEEKLY
    }

    private String message;
    private FREQUENCY frequency;

    public AlertGenerator(String message, FREQUENCY frequency) {
        this.message = message;
        this.frequency = frequency;
    }

    public ArrayList<Alert> generate(DateTime start, DateTime end) {
        DateTime curr = start.clone();
        ArrayList<Alert> alerts = new ArrayList<>();

        long additional = 0;
        long millisInMinute = 1000 * 60;
        switch (frequency) {
            case QUADHOURLY:
                additional = 15 * millisInMinute;
                break;
            case BIHOURLY:
                additional = 30 * millisInMinute;
                break;
            case HOURLY:
                additional = 60 * millisInMinute;
                break;
            case DAILY:
                additional = 24 * 60 * millisInMinute;
                break;
            case WEEKLY:
                additional = 7 * 24 * 60 * millisInMinute;
                break;
        }

        while (curr.compareTo(end) <= 0) {
            Alert temp = new Alert(message, curr.clone());
            alerts.add(temp);
            curr.setTimeInMillis(curr.getTimeInMillis() + additional);
        }

        return alerts;
    }
}
