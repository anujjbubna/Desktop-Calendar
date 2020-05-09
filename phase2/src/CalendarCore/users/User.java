package CalendarCore.users;

import CalendarCore.calendar.CalendarClock;
import CalendarCore.calendar.CalendarSystem;
import CalendarCore.dates.DateTime;
import CalendarCore.events.Event;
import CalendarCore.events.SeriesGenerator;

import java.io.Serializable;
import java.util.ArrayList;

import CalendarCore.exceptions.CalendarNotInTheSystemException;
import CalendarCore.exceptions.CalendarInTheSystemException;
import CalendarCore.exportcal.ExportCal;

public class User implements Serializable {

    /**
     * A list of CalendarSystem for the User
     */
    private ArrayList<CalendarSystem> listOfCalendarSystem = new ArrayList<>();

    /**
     * Name of the User
     */
    private String userName;

    /**
     * Password of the User
     */
    private String password;

    private CalendarSystem defaultCalendar;

    private ExportCal exportCal = new ExportCal();

    /**
     * Constructor for User Class
     * @param userName Name of the User
     * @param password of the User
     */
    public User(String userName, String password){
        this.userName = userName;
        this.password = password;


        defaultCalendar = new CalendarSystem(userName);
        listOfCalendarSystem.add(defaultCalendar);
    }

    /**
     * Getter function for name of User
     * @return name
     */
    public String getUserName(){
        return userName;
    }

    /**
     * Login Credentials of the User
     * @param userName User ID
     * @param password Password entered while logging in
     * @return if the login credentials are correct
     */
    public boolean login(String userName, String password){
        return this.userName.equals(userName) && this.password.equals(password);
    }

    /**
     * Create new CalendarSystem
     * @param calendarSystem the calendarSystem the user wants to create
     * @throws CalendarInTheSystemException if the calendar is already in the system
     */
    public void createNewCalendarSystem(CalendarSystem calendarSystem) throws CalendarInTheSystemException{
        if(listOfCalendarSystem.contains(calendarSystem))
        {
            throw new CalendarInTheSystemException();
        }
        this.listOfCalendarSystem.add(calendarSystem);
    }

    /**
     * Add a single event to a particular CalendarSystem of this user
     * @param calendar the name of the calendar we want to add to
     * @param e the event we want to add
     * @throws CalendarNotInTheSystemException if the calendar is not in the system
     */
    public void addSingleEvent(String calendar, Event e) throws CalendarNotInTheSystemException{
        CalendarSystem cal = new CalendarSystem("TemporaryWillBeDeleted");
        boolean found = false;
        for (CalendarSystem cals: listOfCalendarSystem){
            if (cals.getNameOfCalendar().equals(calendar)){
                cal = cals;
                found = true;
            }
        }
        if (!found){
            throw new CalendarNotInTheSystemException();
        }
        cal.addSingleEvent(e);
    }

    /**
     * Add a series event to a particular CalendarSystem of this user
     * @param calendar the name of the calendar we want to add to
     * @param g the SeriesGenerator of the SeriesEvent we want to add
     * @throws CalendarNotInTheSystemException if the calendar is not in the System
     */
    public void addSeriesEvent(String calendar, SeriesGenerator g) throws CalendarNotInTheSystemException{
        CalendarSystem cal = new CalendarSystem("TemporaryWillBeDeleted");
        boolean found = false;
        for (CalendarSystem cals: listOfCalendarSystem){
            if (cals.getNameOfCalendar().equals(calendar)){
                cal = cals;
                found = true;
            }
        }
        if (!found){
            throw new CalendarNotInTheSystemException();
        }

        cal.addSeriesEvent(g);
    }

    private ArrayList<Event> getAllEvents(){
        ArrayList<Event> events = new ArrayList<>();
        // We get a 4 month range centered at the current time
        CalendarClock c = new CalendarClock();
        DateTime start = c.getTime().offsetMonths(-2);
        DateTime end = c.getTime().offsetMonths(2);
        for(CalendarSystem cs : listOfCalendarSystem){
            events.addAll(cs.getEventSystem().getEventsBetween(start, end));
        }
        return events;
    }

    public ArrayList<CalendarSystem> getListOfCalendarSystem() {
        return listOfCalendarSystem;
    }

    public CalendarSystem getDefaultCalendar() {
        return defaultCalendar;
    }

    /**
     * Exports all the events in the month of the date for this user.
     * @param date - The date whose month is to be considered.
     * @param  path - The directory path for the exported data
     */
    public void exportMonth(DateTime date, String path){
        exportCal.exportMonth(getAllEvents(), date, path);
    }

    /**
     * Exports all the events in the week of the date for this user.
     * @param date - The date whose week is to be considered.
     * @param  path - The directory path for the exported data
     */
    public void exportWeek(DateTime date, String path){
        exportCal.exportWeek(getAllEvents(), date, path);
    }

    /**
     * Exports all the events in the day provided for this user.
     * @param date - The date which is being considered.
     * @param  path - The directory path for the exported data
     */
    public void exportDay(DateTime date, String path){
        exportCal.exportDay(getAllEvents(), date, path);
    }

    /**
     * Exports all the events in the hour of the DateTime object provided for this user.
     * @param date - The date which is being considered.
     * @param  path - The directory path for the exported data
     */
    public void exportHour(DateTime date, String path){
        exportCal.exportHour(getAllEvents(), date, path);
    }
}
