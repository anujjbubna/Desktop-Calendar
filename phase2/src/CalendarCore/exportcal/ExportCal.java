package CalendarCore.exportcal;

import CalendarCore.Searcher;
import CalendarCore.dates.DateTime;
import CalendarCore.events.Event;


import java.io.*;
import java.util.ArrayList;

public class ExportCal implements Serializable {
    Searcher searcher = new Searcher();

    /**
     * Writes into the file with the name filename.
     *
     * @param events   : ArrayList of events.
     * @param filename : name of the file to be created.
     */
    private void writeFile(ArrayList<Event> events, String filename) {
        try {
            File file = new File(filename + ".txt");
            FileWriter fileWriter = new FileWriter(file);
            for (Event event : events) {
                String toEnter = event.getTitle() + " " + event.getStart().toString();
                fileWriter.write(toEnter + '\n');
            }
            fileWriter.close();
            System.out.println("Your file is saved and ready to view.");
        } catch (IOException e) {
            System.out.println("There was an error.");
            e.printStackTrace();
        }
    }

    /**
     * Exports all the events on a particular day to a text file.
     *
     * @param events - ArrayList of all the events.
     * @param date   - Date on which all the events for.
     * @param path   - The path for the directory in which the file will be saved
     */
    public void exportDay(ArrayList<Event> events, DateTime date, String path) {
        ArrayList<Event> eventsInDay = searcher.eventsFromStartDatetime(events, date);
        writeFile(eventsInDay, path + "/eventsInDay");
    }

    /**
     * Exports all the events in a particular month to a text file.
     *
     * @param events - ArrayList of all the events.
     * @param date   - The date whose month and year are to be considered.
     * @param path   - The path for the directory in which the file will be saved
     */
    public void exportMonth(ArrayList<Event> events, DateTime date, String path) {
        ArrayList<Event> eventsInMonth = searcher.eventsInMonth(events, date);
        writeFile(eventsInMonth, path + "/eventsInMonth");
    }

    /**
     * Exports all the events in a the week of the given date to a text file.
     *
     * @param events - ArrayList of all the events.
     * @param date   - The date whose week is to be considered.
     * @param path   - The path for the directory in which the file will be saved
     */
    public void exportWeek(ArrayList<Event> events, DateTime date, String path) {
        ArrayList<Event> eventsInWeek = searcher.eventsInWeek(events, date);
        writeFile(eventsInWeek, path + "/eventsInWeek");
    }

    /**
     * Exports all the events in the hour of the given date (This date should include the time too.)
     *
     * @param events - ArrayList of all the events.
     * @param date   - The DateTime object whose hour is to be considered.
     * @param path   - The path for the directory in which the file will be saved
     */
    public void exportHour(ArrayList<Event> events, DateTime date, String path) {
        ArrayList<Event> eventsInHour = searcher.eventsInHour(events, date);
        writeFile(eventsInHour, path + "/eventsInHour");
    }

}
