package CalendarCore.dates;

import CalendarCore.exceptions.DateNotSupportedException;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTime implements Comparable<DateTime>, Serializable {

    /**
     * The Calender which is inside the envelope class
     */
    private GregorianCalendar gcal;

    /**
     * The format for displaying the DateTime
     */
    protected static final SimpleDateFormat fmt = new SimpleDateFormat("E dd/MM/yyyy hh:mm a");


    /**
     * Construct based on a string of the form dd/MM/yyyy
     *
     * @param date The date
     */
    public DateTime(String date) {
        gcal = new GregorianCalendar();
        gcal.set(Calendar.AM_PM, Calendar.AM);

        String[] dates = date.split("/");
        gcal.set(Calendar.DATE, Integer.parseInt(dates[0]));
        // We subtract 1 as the months are stored 0-11
        gcal.set(Calendar.MONTH, Integer.parseInt(dates[1]) - 1);
        gcal.set(Calendar.YEAR, Integer.parseInt(dates[2]));
        gcal.set(Calendar.HOUR, 1);
        gcal.set(Calendar.MINUTE, 0);
    }

    /**
     * Construct based on strings of the form dd/MM/yyyy and hh:mm:am
     *
     * @param date The date
     * @param time The time
     */
    public DateTime(String date, String time) {
        gcal = new GregorianCalendar();
        gcal.set(Calendar.AM_PM, Calendar.AM);

        String[] dates = date.split("/");
        gcal.set(Calendar.DATE, Integer.parseInt(dates[0]));
        gcal.set(Calendar.MONTH, Integer.parseInt(dates[1]) - 1);
        gcal.set(Calendar.YEAR, Integer.parseInt(dates[2]));

        String[] times = time.split(":");
        gcal.set(Calendar.MINUTE, Integer.parseInt(times[1]));
        if (times[2].equalsIgnoreCase("am")) {
            gcal.set(Calendar.HOUR, Integer.parseInt(times[0]));
        } else {
            gcal.set(Calendar.HOUR, Integer.parseInt(times[0]) + 12);
        }
    }

    /**
     * Constructs a DateTime as far in the future as possible
     */
    public DateTime() {
        gcal = new GregorianCalendar();
        gcal.set(Calendar.AM_PM, Calendar.AM);
        gcal.setTime(new Date(Long.MAX_VALUE));
    }

    /**
     * Constructs a DateTime
     *
     * @param year       The year
     * @param month      The Month
     * @param dayOfMonth The day of the month
     * @param hourOfDay  The hour of the day (From 0-23)
     * @param minute     The minute of the day (From 0-60)
     */
    public DateTime(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        gcal = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute);
    }

    /**
     * @return The 24-hour hour and the minute of the hour
     */
    public int[] time() {
        return new int[]{gcal.get(Calendar.HOUR_OF_DAY), gcal.get(Calendar.MINUTE)};
    }

    /**
     * @return A string of the DateTime
     */
    @Override
    public String toString() {
        return fmt.format(gcal.getTime());
    }

    public String toSimpleString() {
        SimpleDateFormat fmt = new SimpleDateFormat("MMMM yyyy");
        return fmt.format(gcal.getTime());
    }

    public String toFmtString(String pattern) {
        SimpleDateFormat fmt = new SimpleDateFormat(pattern);
        return fmt.format(gcal.getTime());
    }

    /**
     * @return A copy of the DateTime
     */
    public DateTime clone() {
        DateTime temp = new DateTime();
        temp.setTimeInMillis(getTimeInMillis());
        return temp;
    }

    /**
     * @param other A DateTime object
     * @return True iff the 2 DateTimes have the exact same DATE
     */
    public boolean equalDates(DateTime other) {
        boolean year = gcal.get(Calendar.YEAR) == other.get(Calendar.YEAR);
        boolean month = gcal.get(Calendar.MONTH) == other.get(Calendar.MONTH);
        boolean day = gcal.get(Calendar.DATE) == other.get(Calendar.DATE);
        return year && month && day;
    }


    /**
     * @return The date directly after this DateTime with the same time
     */
    public DateTime nextDate() {
        DateTime temp = this.clone();

        DateTime max = new DateTime();
        if (temp.compareTo(max) == 0) {
            throw new DateNotSupportedException();
        }

        temp.gcal.add(Calendar.DATE, 1);
        return temp;
    }

    /**
     * @return The date directly before this DateTime with the same time
     */
    public DateTime prevDate() {
        DateTime temp = this.clone();

        DateTime min = new DateTime();
        min.setTimeInMillis(0);
        if (temp.compareTo(min) == 0) {
            throw new DateNotSupportedException();
        }
        temp.gcal.add(Calendar.DATE, -1);
        return temp;
    }

    public DateTime offsetMonths(int n) {
        DateTime temp = this.clone();
        temp.gcal.add(Calendar.MONTH, n);
        return temp;
    }

    public DateTime offsetWeeks(int n) {
        DateTime temp = this.clone();
        temp.gcal.add(Calendar.WEEK_OF_YEAR, n);
        return temp;
    }

    /**
     * @param field The field to get
     * @return The
     */
    public int get(int field) {
        switch (field) {
            case Calendar.YEAR:
                return gcal.get(Calendar.YEAR);
            case Calendar.MONTH:
                return gcal.get(Calendar.MONTH);
            case Calendar.DATE:
                return gcal.get(Calendar.DATE);
            case Calendar.HOUR:
                return gcal.get(Calendar.HOUR);
            case Calendar.MINUTE:
                return gcal.get(Calendar.MINUTE);
            case Calendar.DAY_OF_WEEK:
                return gcal.get(Calendar.DAY_OF_WEEK);
        }
        return -1;
    }

    /**
     * @return The DateTime in milliseconds
     */
    public long getTimeInMillis() {
        return gcal.getTimeInMillis();
    }

    /**
     * Set the DateTime
     *
     * @param millis The new DateTime in milliseconds
     */
    public void setTimeInMillis(long millis) {
        gcal.setTimeInMillis(millis);
    }


    public boolean dateInMonth(int month, int year) {
        return gcal.get(Calendar.MONTH) == month && gcal.get(Calendar.YEAR) == year;
    }

    /**
     * @param o The other object
     * @return {-1,0,1}
     */
    @Override
    public int compareTo(DateTime o) {
        return Long.compare(gcal.getTimeInMillis(), o.getTimeInMillis());
    }

    /**
     * @return 1:00 AM of this day time
     */
    public DateTime getStartOfDay() {
        DateTime dawn = this.clone();
        dawn.gcal.set(Calendar.HOUR_OF_DAY, 1);
        dawn.gcal.set(Calendar.MINUTE, 0);
        return dawn;
    }


}
