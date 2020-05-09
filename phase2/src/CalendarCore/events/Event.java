package CalendarCore.events;

import CalendarCore.dates.DateTime;
import CalendarCore.alerts.Alert;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Comparable<Event>, Serializable {

    /**
     * A unique id for a given event
     */
    protected long id;

    /**
     * The Start Date and Time for the event
     */
    protected DateTime start;

    /**
     * The End Date and time for the event
     */
    protected DateTime end;

    /**
     * The Event's title
     */
    protected String title;

    /**
     * The 0+ Alerts for this event
     */
    protected ArrayList<Alert> alerts;

    /**
     * The 0+ tags for the event
     */
    protected ArrayList<String> tags;


    /**
     * Useless constructor to make code compile but never really gets used.
     */
    public Event() {
    }

    /**
     * @param title The events title
     * @param start The start time and date
     * @param end   The end time and date
     */
    public Event(String title, DateTime start, DateTime end) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.alerts = new ArrayList<>();
        this.tags = new ArrayList<>();

        // Creates an id that is very unlikely to be the same as any other id
        id = title.hashCode() + start.getTimeInMillis();
    }

    public long getId() {
        return id;
    }

    /**
     * @return The start date and time
     */
    public DateTime getStart() {
        return start;
    }

    /**
     * @param start The new Start date and time
     */
    public void setStart(DateTime start) {
        this.start = start;
    }

    /**
     * @return The end date and time
     */
    public DateTime getEnd() {
        return end;
    }

    /**
     * @param end The new end date and time
     */
    public void setEnd(DateTime end) {
        this.end = end;
    }

    /**
     * @return The event title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The new event title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The Alerts for this event
     */
    public ArrayList<Alert> getAlerts() {
        return alerts;
    }

    /**
     * @return The tags for this event
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * Adds 1+ alerts to the alerts ArrayList
     * Precondition: alerts != []
     *
     * @param alerts The alerts to add
     */
    public void addAlerts(ArrayList<Alert> alerts) {
        this.alerts.addAll(alerts);
    }

    /**
     * Adds 1+ strings to the tags ArrayList
     * Precondition: tags != []
     *
     * @param tags The tags to add
     */
    public void addTags(ArrayList<String> tags) {
        this.tags.addAll(tags);
    }


    /**
     * Convert this event into a SeriesEvent
     *
     * @param seriesName The name of the series to add this event to
     * @return The new SeriesEvent
     */
    public SeriesEvent toSeries(String seriesName) {
        SeriesEvent temp = new SeriesEvent(seriesName, seriesName + ":" + title, start, end, null);
        temp.addAlerts(alerts);
        temp.addTags(tags);
        temp.id = this.id;
        return temp;
    }

    /**
     * @return A string representation of the event
     */
    @Override
    public String toString() {
        return title + ": " + start + " to " + end;
    }

    /**
     * Compares the START dates and times of the events
     *
     * @param o The other Event
     * @return -1 iff this < other. 0 iff this = other. 1 iff this > other
     */
    @Override
    public int compareTo(Event o) {
        return start.compareTo(o.start);
    }

    /**
     * Checks if an event is on a particular date
     *
     * @param date the date you want to check if its on. must be at 12:00:am
     * @return boolean to represent whether the event is on date
     */
    public boolean isOnDate(DateTime date) {
        if ((this.start.getTimeInMillis() >= date.getTimeInMillis()) && this.start.getTimeInMillis() <= (date.getTimeInMillis() + 1000 * 60 * 60 * 24)) {
            return true;
        }

        if ((this.start.getTimeInMillis() >= date.getTimeInMillis()) && this.start.getTimeInMillis() <= (date.getTimeInMillis() + 1000 * 60 * 60 * 24)) {
            return true;
        }
        return false;
    }

    /**
     * Tells whether the tag matches the tags in this event
     *
     * @param tag the tag you want to check
     * @return whether the tag matches or not
     */
    public boolean matchesTag(String tag) {
        for (String t : tags) {
            if (t.equals(tag)) {
                return true;
            }
        }
        return false;
    }
}