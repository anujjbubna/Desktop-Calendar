package CalendarCore.calendar;

import CalendarCore.dates.DateTime;

public class CalendarClock {
    private static DateTime current;
    private static int first = 0;


    public CalendarClock() {
        if (first == 0) {
            current = new DateTime();
            current.setTimeInMillis(System.currentTimeMillis());
            first = 1;
        }
    }


    /**
     * Getter function for time
     *
     * @return current time
     */
    public DateTime getTime() {
        return current.clone();
    }

    /**
     * increment the clock by a certain amount
     *
     * @param type   the unit you want to increment by
     *               valid units are:
     *               sec
     *               min
     *               hour
     *               day
     *               week
     * @param amount the amount you want to increment by
     */
    public void increment(String type, int amount) {
        switch (type) {
            case "sec":
                current.setTimeInMillis(current.getTimeInMillis() + amount * 1000);
                break;
            case "min":
                current.setTimeInMillis(current.getTimeInMillis() + amount * 60000);
                break;
            case "hour":
                current.setTimeInMillis(current.getTimeInMillis() + amount * 60 * 60 * 1000);
                break;
            case "day":
                current.setTimeInMillis(current.getTimeInMillis() + amount * 24 * 60 * 60 * 1000);
                break;
            case "week":
                current.setTimeInMillis(current.getTimeInMillis() + amount * 7 * 24 * 60 * 60 * 1000);
        }
    }
}
