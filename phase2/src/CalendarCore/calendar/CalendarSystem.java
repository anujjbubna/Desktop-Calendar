package CalendarCore.calendar;

import CalendarCore.dates.DateTime;
import CalendarCore.alerts.Alert;
import CalendarCore.alerts.AlertSystem;
import CalendarCore.events.Event;
import CalendarCore.events.EventSystem;
import CalendarCore.events.SeriesGenerator;
import CalendarCore.memos.MemoSystem;

import java.io.Serializable;
import java.util.ArrayList;

public class CalendarSystem implements Serializable {

    /**
     * Name of the CalendarSystem
     */
    private String nameOfCalendar;

    /**
     * A MemoSystem for the User
     */
    private MemoSystem memoSystem = new MemoSystem();

    /**
     * An AlertSystem for the User
     */
    private AlertSystem alertSystem = new AlertSystem();

    /**
     * An EventSystem for the User
     */
    private EventSystem eventSystem = new EventSystem(alertSystem);

    public MemoSystem getMemoSystem() {
        return memoSystem;
    }

    public CalendarSystem(String nameOfCalendar) {
        this.nameOfCalendar = nameOfCalendar;
    }

    public String getNameOfCalendar() {
        return nameOfCalendar;
    }

    /**
     * Getter function for EventSystem
     *
     * @return EventSystem
     */
    public EventSystem getEventSystem() {
        return eventSystem;
    }

    /**
     * Getter function for AlertSystem
     *
     * @return AlertSystem
     */
    public AlertSystem getAlertSystem() {
        return alertSystem;
    }


    /**
     * Add a single event to the event system
     *
     * @param e the single event you need to add to the event system
     */
    public void addSingleEvent(Event e) {
        eventSystem.addSingleEvent(e);
    }

    /**
     * Get all alerts
     *
     * @return an array list of alerts
     */
    public ArrayList<Alert> getAllAlerts() {
        CalendarClock calendarClock = new CalendarClock();
        // Creating start and end times for the method getEventsBetween()
        DateTime start = calendarClock.getTime();
        DateTime end = calendarClock.getTime();
        start.setTimeInMillis(start.getTimeInMillis() - 12 * 31 * 24 * 60 * 60);
        end.setTimeInMillis(end.getTimeInMillis() + 12 * 31 * 24 * 60 * 60);

        ArrayList<Alert> allAlerts = new ArrayList<>();
        for (Event event : eventSystem.getEventsBetween(start, end)) {
            allAlerts.addAll(event.getAlerts());
        }
        return allAlerts;
    }

    /**
     * Add a series event to the event system
     *
     * @param g the SeriesGenerator for the relevant SeriesEvent
     */
    public void addSeriesEvent(SeriesGenerator g) {
        eventSystem.addSeriesEvent(g);
    }

    /**
     * Gets alerts at a particular time from this calendar.
     *
     * @param time the time at which we want to get alerts for.
     * @return ArrayList of Alerts
     */
    public ArrayList<Alert> getAlertAtTime(DateTime time) {
        DateTime time5 = new DateTime();
        time5.setTimeInMillis(time.getTimeInMillis() + 500);
        return getAlertSystem().getAlerts(eventSystem.getListOfEvents(), eventSystem.getListOfGenerators(), time, time5);
    }

}

