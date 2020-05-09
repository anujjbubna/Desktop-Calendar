package CalendarCore.events;

import CalendarCore.alerts.AlertSystem;
import CalendarCore.calendar.CalendarClock;
import CalendarCore.dates.DateTime;
import CalendarCore.exceptions.EventNotInSystemException;
import CalendarCore.exceptions.InvalidInputException;
import CalendarCore.memos.MemoSystem;

import java.io.Serializable;
import java.util.ArrayList;

public class EventSystem implements Serializable {

    /**
     * List of all the events created by the User
     */
    private ArrayList<Event> listOfEvents;

    /**
     * List of all past events of the User
     */
    private ArrayList<Event> pastEvents;

    /**
     * List of all ongoing events of the User
     */
    private ArrayList<Event> ongoingEvents;

    /**
     * List of all upcoming events of the User
     */
    private ArrayList<Event> upcomingEvents;

    /**
     * List of generators
     */
    private ArrayList<SeriesGenerator> listOfGenerators;

    /**
     * Alert System for Events
     */
    private AlertSystem alertSystem;

    /**
     * List Of generators for statutoryEvents
     */

    private ArrayList<StatutoryEventGenerator> listOfStatutoryEventGenerators;


    /**
     * Constructor
     *
     * @param alertSystem alert system for the Event system
     */
    public EventSystem(AlertSystem alertSystem) {
        listOfEvents = new ArrayList<>();
        pastEvents = new ArrayList<>();
        ongoingEvents = new ArrayList<>();
        upcomingEvents = new ArrayList<>();
        listOfGenerators = new ArrayList<>();
        listOfStatutoryEventGenerators = new ArrayList<>();
        this.alertSystem = alertSystem;
    }

    public ArrayList<Event> getPastEvents() {
        update();
        return pastEvents;
    }

    public ArrayList<Event> getOngoingEvents() {
        update();
        return ongoingEvents;
    }

    public ArrayList<Event> getUpcomingEvents() {
        update();
        return upcomingEvents;
    }

    public ArrayList<SeriesGenerator> getListOfGenerators() {
        return listOfGenerators;
    }


    /**
     * Getter for AlertSystem
     *
     * @return the AlertSystem for this EventSystem
     */
    public AlertSystem getAlertSystem() {
        return alertSystem;
    }

    /**
     * A new Event (Single) created by the User
     *
     * @param title      Title given to the Event
     * @param start_date Start Datetime for the Event
     * @param end_date   End Datetime for the Event
     * @return The event's id
     */
    public long createSingleEvent(String title, DateTime start_date, DateTime end_date) {
        Event e = new Event(title, start_date, end_date);
        listOfEvents.add(e);
        return e.id;
    }


    public Event getEventByID(long id) {
        for (Event e : listOfEvents) {
            if (e.id == id) {
                return e;
            }
        }
        for(SeriesGenerator sg:listOfGenerators){
            if(sg.getId() == id){
                return sg.getTemplate();
            }
        }

        for(StatutoryEventGenerator seg:listOfStatutoryEventGenerators){
            if(seg.getId() == id){
                return seg.toEvent(new DateTime());
            }
        }
        return new Event();
    }

    public SeriesGenerator getGeneratorByID(long id) throws InvalidInputException {
        for (SeriesGenerator sg : getListOfGenerators()) {
            if (sg.getId() == id) {
                return sg;
            }
        }
        throw new InvalidInputException();
    }


    /**
     * A new Event (INFINITE Series) created by the User
     *
     * @param seriesName Name of series of Events
     * @param title      Title given to the Event
     * @param startTime  Start Datetime for the Event
     * @param endTime    End Datetime for the Event
     * @param f          Frequency of Event
     * @param count      Number of events
     */
    public long createSeriesEvent(String seriesName, String title, DateTime startTime, DateTime endTime, SeriesGenerator.FREQUENCY[] f, int count) {
        SeriesEvent template = new SeriesEvent(seriesName, title, startTime, endTime, null);
        // We'll have a way to make Alerts for the SeriesEvent using template
        SeriesGenerator sg = new SeriesGenerator(startTime, f, template, count);
        listOfGenerators.add(sg);
        return sg.getId();
    }

    /**
     * A new Event (FINITE Series) created by the User
     *
     * @param seriesName Name of series of Events
     * @param title      Title given to Event
     * @param startTime  Start DateTime of Event
     * @param endTime    End DateTime of Event
     * @param f          Frequency of Event
     * @param endDate    End date for Event to end
     */
    public long createSeriesEvent(String seriesName, String title, DateTime startTime, DateTime endTime, SeriesGenerator.FREQUENCY[] f, DateTime endDate) {
        SeriesEvent template = new SeriesEvent(seriesName, title, startTime, endTime, null);
        SeriesGenerator sg = new SeriesGenerator(startTime, f, template, endDate);
        listOfGenerators.add(sg);
        return sg.getId();
    }

    /**
     * @param name  the name of the event
     * @param day   the day of the week of the event
     * @param week  the week of the month of the event
     * @param month the month of the year of the event
     * @return the id of the created event
     */
    public long createStatutoryEvent(String name, int day, int week, int month) {
        StatutoryEventGenerator statutoryEventGenerator = new StatutoryEventGenerator(name, day, week, month);
        addStatutoryEvent(statutoryEventGenerator);
        return statutoryEventGenerator.getId();
    }

    /**
     * Getter function for listOfEvents
     *
     * @return ArrayList<Event>
     */
    public ArrayList<Event> getListOfEvents() {
        return listOfEvents;
    }

    /**
     * updates the past, ongoing and upcoming event lists
     */
    private void update() {
        DateTime today = new CalendarClock().getTime();
        pastEvents.clear();
        ongoingEvents.clear();
        upcomingEvents.clear();
        for (Event event : listOfEvents) {
            if (today.compareTo(event.getEnd()) > 0)
                pastEvents.add(event);
            else if (today.compareTo(event.getEnd()) <= 0 && today.compareTo(event.getStart()) >= 0)
                ongoingEvents.add(event);
            else if (today.compareTo(event.getStart()) < 0)
                upcomingEvents.add(event);
            else {
                System.out.println("We encountered an unexpected error.");
            }
        }
        for (SeriesGenerator g : listOfGenerators) {
            DateTime dt = new DateTime();
            dt.setTimeInMillis(0);
            pastEvents.addAll(g.generate(dt, today));
            ongoingEvents.addAll(g.generate(today, today));
            dt = new DateTime();
            upcomingEvents.addAll(g.generate(today, dt));
        }

        // Generate Statutory events +- 1 year
        for (StatutoryEventGenerator seg : listOfStatutoryEventGenerators) {
            DateTime past = today.clone().offsetMonths(-12);
            pastEvents.addAll(seg.generate(past, today));

            if (seg.isOnDate(today)) {
                ongoingEvents.add(seg.toEvent(today));
            }

            DateTime future = today.clone().offsetMonths(12);
            upcomingEvents.addAll(seg.generate(today, future));
        }
    }


    /**
     * Delete an Event or series event in this calendar system
     *
     * @param id the id of the event to be deleted
     */
    public void deleteEvent(long id, MemoSystem memoSystem) throws EventNotInSystemException {

        //checking if ids of single events match.
        for (int i = 0; i < listOfEvents.size(); i++) {
            if (listOfEvents.get(i).getId() == id) {
                listOfEvents.remove(i);
                break;
            }
        }

        //checking if ids of series events match
        for (int i = 0; i < listOfGenerators.size(); i++) {
            if (listOfGenerators.get(i).getId() == id) {
                listOfGenerators.remove(i);
                break;
            }
        }

        //checking if ids of statutory events match
        for (int i = 0; i < listOfStatutoryEventGenerators.size(); i++) {
            if (listOfStatutoryEventGenerators.get(i).getId() == id) {
                listOfStatutoryEventGenerators.remove(i);
                break;
            }
        }

        //Removes any possibly associated memos
        memoSystem.removeEventInMemo(id);
    }

    /**
     * returns all the events on a particular date in this eventSystem
     *
     * @param date the date you want to check. must be at 12:00:am
     * @return arr, the list of events.
     */
    public ArrayList<Event> getEventsOnDate(DateTime date) {
        // Sets start time to 1:00 AM of on the date provided by date
        DateTime startDate = date.getStartOfDay();

        ArrayList<Event> arr = new ArrayList<>();
        for (Event e : listOfEvents) {
            if (e.isOnDate(startDate)) {
                sortedInsert(arr, e);
            }
        }

        for (SeriesGenerator generator : listOfGenerators) {
            for (SeriesEvent event : generator.generate(startDate, startDate)) {
                sortedInsert(arr, event);
            }
        }
        for (StatutoryEventGenerator seg : listOfStatutoryEventGenerators) {
            if (seg.isOnDate(date)) {
                sortedInsert(arr, seg.toEvent(date));
            }
        }
        return arr;
    }


    /**
     * Add a single event to the event system
     *
     * @param e the single event you need to add to the event system
     */
    public void addSingleEvent(Event e) {
        listOfEvents.add(e);
    }

    /**
     * Add a series event to the event system
     *
     * @param g the SeriesGenerator for the relevant SeriesEvent
     */
    public void addSeriesEvent(SeriesGenerator g) {
        listOfGenerators.add(g);
    }

    /**
     * Add a statutory event to the event system
     *
     * @param seg the generator of the statutory event you want to add
     */
    public void addStatutoryEvent(StatutoryEventGenerator seg) {
        listOfStatutoryEventGenerators.add(seg);
    }

    private void sortedInsert(ArrayList<Event> events, Event e) {
        int index = 0;
        boolean added = false;
        if (events.size() == 0) {
            events.add(e);
            added = true;
        }

        if (!added) {
            while ((index < events.size()) && (events.get(index).getStart().getTimeInMillis() <= e.getStart().getTimeInMillis())) {
                index++;
            }

            events.add(index, e);

        }
    }


    /**
     * Gets a list of all generatable events between start and end and all single events
     *
     * @param start the start datetime for generatable events
     * @param end   the end datetime for generatable events
     * @return ArrayList containing all the events between start and end
     */
    public ArrayList<Event> getEventsBetween(DateTime start, DateTime end) {
        ArrayList<Event> events = new ArrayList<>();

        for (SeriesGenerator g : listOfGenerators) {
            events.addAll(g.generate(start, end));
        }

        for (StatutoryEventGenerator seg : listOfStatutoryEventGenerators) {
            events.addAll(seg.generate(start, end));
        }

        events.addAll(getListOfEvents());

        return events;
    }

}
