package CalendarCore.alerts;


import CalendarCore.dates.DateTime;

import java.io.Serializable;

public class Alert implements Comparable<Alert>, Serializable {
    private String message;
    private DateTime startTime;

    public Alert(String message, DateTime time) {
        this.message = message;
        this.startTime = time;
    }


    public String getMessage() {
        return this.message;
    }

    public DateTime getTime() {
        return this.startTime;
    }


    @Override
    public String toString() {
        return startTime + " - " + message;
    }

    /**
     * if start time of this alert is greater than the start time of a, return 1
     * if start time of this alert is equal to the start time of a, return 0
     * return -1 otherwise
     *
     * @param a The other alert
     * @return -1, o or 1
     */
    @Override
    public int compareTo(Alert a) {
        return startTime.compareTo(a.getTime());
    }
}
