package CalendarCore;

import CalendarCore.dates.DateTime;
import CalendarCore.events.Event;
import CalendarCore.memos.Memo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Searcher implements Serializable {

    /**
     * @return ArrayList of Events with the start date startDate
     */
    public ArrayList<Event> eventsFromStartDatetime(ArrayList<Event> events, DateTime startDate) {
        ArrayList<Event> validEvents = new ArrayList<>();
        for (Event evt : events) {
            if (evt.getStart().equalDates(startDate)) {
                validEvents.add(evt);
            }
        }

        return validEvents;
    }

    /**
     * @return ArrayList of Events with the end date endDate
     */
    public ArrayList<Event> eventsFromEndDatetime(ArrayList<Event> events, DateTime endDate) {
        ArrayList<Event> validEvents = new ArrayList<>();
        for (Event evt : events) {
            if (evt.getEnd().equalDates(endDate)) {
                validEvents.add(evt);
            }
        }

        return validEvents;
    }

    /**
     * It returns a list of all the events in the month of the given date.
     *
     * @param events: ArrayList of all the events.
     * @param date:   The date whose month will be considered.
     * @return ArrayList of Events in a particular month.
     */
    public ArrayList<Event> eventsInMonth(ArrayList<Event> events, DateTime date) {
        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);
        ArrayList<Event> monthEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.getStart().dateInMonth(month, year)) {
                monthEvents.add(event);
            }
        }
        return monthEvents;
    }

    /**
     * @param events: ArrayList of events which is going to be searched through
     * @param start:  Day whose week will be considered. Eg- inputting 21/09/2012 here will give us all the events in the
     *                week of 21/09/2012
     * @return ArrayList of events in the week of <start>.
     */
    public ArrayList<Event> eventsInWeek(ArrayList<Event> events, DateTime start) {
        long millisInMinute = 1000 * 60;
        long startInMillis = start.getTimeInMillis();
        long endInMillis = startInMillis + 7 * 24 * 60 * millisInMinute;
        ArrayList<Event> weekEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.getStart().getTimeInMillis() >= startInMillis
                    && event.getStart().getTimeInMillis() <= endInMillis) {
                weekEvents.add(event);
            }
        }
        return weekEvents;
//        DateTime endDate = null;
//        for(int i = 0; i < 6; i = i + 1){
//            endDate = start.nextDate();
//        }
//        for(Event event: events){
//            if((event.getStart().compareTo(start) == 0 || event.getStart().compareTo(start) > 0) &&
//                    (event.getStart().compareTo(endDate) == 0 || event.getStart().compareTo(endDate) < 0)){
//                weekEvents.add(event);
//            }
//        }
//        return weekEvents;
    }

    /**
     * This method searches through a provided event list for events in a particular hour.
     *
     * @param events: ArrayList of events which is going to be searched through.
     * @param start:  The start time of the one hour period in which we are searching for the events.
     * @return ArrayList of all the events with one hour of <start>.
     */
    public ArrayList<Event> eventsInHour(ArrayList<Event> events, DateTime start) {
        ArrayList<Event> hourEvents = new ArrayList<>();
        long millisInMinute = 1000 * 60;
        long startInMillis = start.getTimeInMillis();
        for (Event event : events) {
            if ((event.getStart().getTimeInMillis() - startInMillis) <= (60 * millisInMinute)) {
                hourEvents.add(event);
            }
        }
        return hourEvents;
    }

    /**
     * @return ArrayList of event ids associated with the given memo
     */
    public ArrayList<Long> eventsFromMemo(Memo memo) {
        return memo.getListOfEventID();
    }


    /**
     * Gets all the events that match a particular tag
     *
     * @param events the list of all events you want to check
     * @param tag    the tag you want to check events by
     * @return an arraylist of events that match a tag
     */
    public ArrayList<Event> getEventByTag(ArrayList<Event> events, String tag) {
        ArrayList<Event> matchedEvents = new ArrayList<>();
        for (Event e : events) {
            if (e.matchesTag(tag)) {
                matchedEvents.add(e);
            }
        }
        return matchedEvents;
    }


}
