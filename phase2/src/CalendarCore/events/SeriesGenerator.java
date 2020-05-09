package CalendarCore.events;

import CalendarCore.dates.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class SeriesGenerator implements Serializable {
    /**
     * The frequencies at which an event can occur
     */
    public enum FREQUENCY {
        RECUR_WEEKLY_SUN,
        RECUR_WEEKLY_MON,
        RECUR_WEEKLY_TUE,
        RECUR_WEEKLY_WED,
        RECUR_WEEKLY_THU,
        RECUR_WEEKLY_FRI,
        RECUR_WEEKLY_SAT,
        RECUR_DAILY,
        RECUR_MONTHLY,
        RECUR_YEARLY,
    }

    /**
     * The limits at which a generator will end
     */
    public enum LIMIT {
        RECUR_INFINITE,
        RECUR_FINITE,
        RECUR_UNTIL
    }

    /**
     * A Series event which acts as the template for the generator
     * The Start and End times as well as the title and series names are copied
     */
    private SeriesEvent template;

    /**
     * The frequency at which this generator generates events
     */
    private FREQUENCY[] frequency;

    /**
     * The limit of this Generator
     */
    private LIMIT limit;

    /**
     * The date when this generator will no longer generate
     */
    private DateTime endDate;

    /**
     * The date on which this generator was created
     */
    public DateTime createdOn;


    /**
     * A FINITE generator which will create events count times
     *
     * @param createdOn The date the generator was created
     * @param frequency The frequencies at which events are created
     *                  Precondition: If len(frequency) > 1 Then frequency contains days of the week Else
     *                  frequency contains Daily, Monthly or Yearly
     * @param template  The template which is replicated.
     *                  The Start and End times as well as the title and series names are copied
     * @param count     The number of recurrences of this event
     */
    public SeriesGenerator(DateTime createdOn, FREQUENCY[] frequency, SeriesEvent template,
                           int count) {
        this.createdOn = createdOn;
        this.limit = LIMIT.RECUR_FINITE;
        this.frequency = frequency;
        this.template = template;
        this.endDate = calculateEndDate(count);
    }

    /**
     * @param count The number of recurrences of the generator
     * @return Returns an endDate for the generator given the number of recurrences
     */
    private DateTime calculateEndDate(int count) {
        long createdTime = createdOn.getTimeInMillis();
        long milliInDay = 86400000;
        long additionalTime;

        // End after repeating frequency[0] count times
        switch (frequency[0]) {
            case RECUR_DAILY:
                additionalTime = count * milliInDay;
                break;
            case RECUR_MONTHLY:
                int days = 0;
                int curMonth = createdOn.get(Calendar.MONTH);
                int curYear = createdOn.get(Calendar.YEAR);

                for (int i = 0; i < count; i++) {
                    switch (curMonth) {
                        case 0:
                        case 2:
                        case 4:
                        case 6:
                        case 7:
                        case 9:
                        case 11:
                            days += 31;
                            break;
                        case 3:
                        case 5:
                        case 8:
                        case 10:
                            days += 30;
                            break;
                        case 1:
                            // Accounts for leap years
                            if (curYear % 400 == 0 || curYear % 4 == 0) {
                                days += 1;
                            }
                            days += 28;
                            break;
                    }

                    if (curMonth == 11) {
                        curYear += 1;
                    }
                    curMonth = (curMonth + 1) % 12;
                }

                additionalTime = days * milliInDay;
                break;
            case RECUR_YEARLY:
                additionalTime = count * 365 * milliInDay + milliInDay * (long) (Math.ceil(count / 4.0));
                break;
            default:
                // A weekly recurrence, which will occur on one or more days for count weeks.
                additionalTime = count * 7 * milliInDay;
                break;
        }
        DateTime temp = new DateTime();
        temp.setTimeInMillis(createdTime + additionalTime);
        return temp;
    }

    /**
     * A UNTIL generator which will create events until an end date
     *
     * @param createdOn The date the generator was created
     * @param frequency The frequencies at which events are created
     *                  Precondition: If len(frequency) > 1 Then frequency contains days of the week Else
     *                  frequency contains Daily, Monthly or Yearly
     * @param template  The template which is replicated.
     *                  The Start and End times as well as the title and series names are copied
     * @param endDate   The date until this generator works
     */
    public SeriesGenerator(DateTime createdOn, FREQUENCY[] frequency, SeriesEvent template,
                           DateTime endDate) {
        this.createdOn = createdOn;
        this.limit = LIMIT.RECUR_UNTIL;
        this.frequency = frequency;
        this.template = template;
        this.endDate = endDate;
    }

    /**
     * Generate events in the given interval
     *
     * @param start The start time for the generation interval
     * @param end   The end time for the generation interval
     * @return The events within that interval
     */
    public ArrayList<SeriesEvent> generate(DateTime start, DateTime end) {
        ArrayList<SeriesEvent> events = new ArrayList<>();

        // We increment so that the inputs are inclusive, inclusive
        // Max is the maximum DateTime supported, we ensure we are not incrementing past there
        DateTime max = new DateTime();
        if (end.compareTo(max) < 0) {
            end = end.nextDate();
        }

        if (limit == LIMIT.RECUR_INFINITE) {
            this.endDate = end;
        }

        // Ensures generator cannot generate before its start date
        if (start.compareTo(createdOn) < 0) {
            if (end.compareTo(createdOn) < 0) {
                return events;
            } else {
                start = createdOn;
            }
        }

        //Ensures the generator cannot generate after its end date
        if (end.compareTo(endDate) > 0) {
            if (start.compareTo(endDate) > 0) {
                return events;
            } else {
                end = endDate;
            }
        }
        int sTimeHour = template.getStart().time()[0];
        int sTimeMin = template.getStart().time()[1];
        int eTimeHour = template.getEnd().time()[0];
        int eTimeMin = template.getEnd().time()[1];
        while (!start.equalDates(end)) {
            int date = start.get(Calendar.DATE);
            int mon = start.get(Calendar.MONTH);
            int year = start.get(Calendar.YEAR);
            int day = start.get(Calendar.DAY_OF_WEEK);

            boolean create = false;
            switch (frequency[0]) {
                case RECUR_DAILY:
                    create = true;
                    break;
                case RECUR_MONTHLY:
                    create = date == template.getStart().get(Calendar.DATE);
                    break;
                case RECUR_YEARLY:
                    create = (date == template.getStart().get(Calendar.DATE)) &&
                            (mon == template.getStart().get(Calendar.MONTH));
                    break;
                default:
                    // A weekly recurrence, which will occur on one or more days for count weeks.
                    for (FREQUENCY f : frequency) {
                        if (day == dayToInt(f)) {
                            create = true;
                            break;
                        }
                    }
            }

            if (create) {
                SeriesEvent temp = new SeriesEvent(template.getSeriesName(), template.getTitle(),
                        new DateTime(year, mon, date, sTimeHour, sTimeMin),
                        new DateTime(year, mon, date, eTimeHour, eTimeMin), this);
                temp.addAlerts(template.getAlerts());
                temp.addTags(template.getTags());
                events.add(temp);
            }


            // Increment by 1 day
            start = start.nextDate();
        }
        return events;
    }

    /**
     * @param f A FREQUENCY enum for a day of the week
     * @return A numerical representation of the frequency enum. If f is not a day of the week return -1
     */
    private static int dayToInt(FREQUENCY f) {
        switch (f) {
            case RECUR_WEEKLY_SUN:
                return 1;
            case RECUR_WEEKLY_MON:
                return 2;
            case RECUR_WEEKLY_TUE:
                return 3;
            case RECUR_WEEKLY_WED:
                return 4;
            case RECUR_WEEKLY_THU:
                return 5;
            case RECUR_WEEKLY_FRI:
                return 6;
            case RECUR_WEEKLY_SAT:
                return 7;
        }
        return -1;
    }

    /**
     * @return end date
     */
    public DateTime getEndDate() {
        return endDate;
    }

    /**
     * @return The date the generator was created
     */
    public DateTime getCreatedOn() {
        return createdOn;
    }


    public long getId() {
        return template.getId();
    }

    public SeriesEvent getTemplate() {
        return template;
    }

    public void setFrequency(FREQUENCY[] frequency) {
        this.frequency = frequency;
    }

    public void setEndCount(int count) {
        this.limit = LIMIT.RECUR_FINITE;
        this.endDate = calculateEndDate(count - 1);
    }

    public void setEndDate(DateTime endDate) {
        this.limit = LIMIT.RECUR_UNTIL;
        this.endDate = endDate;
    }
}
