package CalendarCore.events;

import CalendarCore.dates.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class StatutoryEventGenerator implements Serializable {

    /**
     * day represents the following:
     * 0 - Sunday
     * 1 - Monday
     * 2 - Tuesday
     * 3 - Wednesday
     * 4 - Thursday
     * 5 - Friday
     * 6 - Saturday
     */
    private int day;

    /**
     * Week can only be 1,2,3,4,5
     * Week represents the week this event occurs in
     */
    private int week;

    /**
     * The month this event occurs in. If month > 12 then this event occurs every month
     */
    private int month;

    /**
     * The name of the event
     */
    private String eventName;

    /**
     * The id of this statutory Event
     */
    private long id;

    public StatutoryEventGenerator(String eventName, int day, int week, int month) {
        // Since weeks begin at 1 = SUN
        this.day = day + 1;
        this.week = week;
        // Since months begin at 0 = JAN
        this.month = month - 1;
        this.eventName = eventName;
        id = eventName.hashCode() + (day + week + month) * 120989857;
    }

    public String toString() {
        return eventName + " on " + day + " of " + month + " in week" + week;
    }

    public long getId() {
        return id;
    }

    public boolean isOnDate(DateTime date) {
        switch (week) {
            case 1:
                if (date.get(Calendar.DATE) >= 1 && date.get(Calendar.DATE) <= 7) {
                    if (date.get(Calendar.DAY_OF_WEEK) == day) {
                        // Checks if the event occurs every month
                        if (month > 12) {
                            return true;
                        }
                        return date.get(Calendar.MONTH) == month;
                    }
                }
                break;
            case 2:
                if (date.get(Calendar.DATE) >= 8 && date.get(Calendar.DATE) <= 14) {
                    if (date.get(Calendar.DAY_OF_WEEK) == day) {
                        // Checks if the event occurs every month
                        if (month > 12) {
                            return true;
                        }
                        return date.get(Calendar.MONTH) == month;
                    }
                }
                break;
            case 3:
                if (date.get(Calendar.DATE) >= 15 && date.get(Calendar.DATE) <= 21) {
                    if (date.get(Calendar.DAY_OF_WEEK) == day) {
                        // Checks if the event occurs every month
                        if (month > 12) {
                            return true;
                        }
                        return date.get(Calendar.MONTH) == month;
                    }
                }
                break;
            case 4:
                if (date.get(Calendar.DATE) >= 22 && date.get(Calendar.DATE) <= 28) {
                    if (date.get(Calendar.DAY_OF_WEEK) == day) {
                        // Checks if the event occurs every month
                        if (month > 12) {
                            return true;
                        }
                        return date.get(Calendar.MONTH) == month;
                    }
                }
                break;
            case 5:
                if (date.get(Calendar.DATE) >= 29 && date.get(Calendar.DATE) <= 31) {
                    if (date.get(Calendar.DAY_OF_WEEK) == day) {
                        // Checks if the event occurs every month
                        if (month > 12) {
                            return true;
                        }
                        return date.get(Calendar.MONTH) == month;
                    }
                }
                break;
            default:
                return false;
        }
        return false;
    }

    public Event toEvent(DateTime date){
        Event temp = new Event(eventName, date.clone(), date.clone());
        temp.id = id;
        return temp;
    }


    /**
     * @param start the start time for the generation
     * @param end   the end time for the generation
     * @return an arraylist of events between start and end
     */
    public ArrayList<Event> generate(DateTime start, DateTime end) {
        ArrayList<Event> events = new ArrayList<>();
        DateTime currentDate = start.clone();

        while (currentDate.getTimeInMillis() <= end.getTimeInMillis()) {
            if (isOnDate(currentDate)) {
                events.add(toEvent(currentDate));
            }
            currentDate = currentDate.nextDate();
        }

        return events;
    }


}
