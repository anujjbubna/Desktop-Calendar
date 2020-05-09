package CalendarCore.alerts;

import CalendarCore.dates.DateTime;
import CalendarCore.events.Event;
import CalendarCore.events.SeriesGenerator;

import java.io.Serializable;
import java.util.ArrayList;

public class AlertSystem implements Serializable {

    /**
     * @param e        - Event to which the alert is to be added.
     * @param message  - message that the alert should display.
     * @param dateTime - Time of the Alert
     */
    public void addAlert(Event e, String message, DateTime dateTime) {
        Alert a = new Alert(message, dateTime);
        ArrayList<Alert> ar = new ArrayList<>();
        ar.add(a);
        e.addAlerts(ar);
    }

    /**
     * @param event     - event to which the SeriesAlert is to be added.
     * @param message   - message which the alert should display
     * @param freq      - frequency with which the series alert should recur
     * @param startTime - starting time of the alert.
     */
    public void addSeriesAlert(Event event, String message, AlertGenerator.FREQUENCY freq,
                               DateTime startTime) {
        AlertGenerator ag = new AlertGenerator(message, freq);
        event.addAlerts(ag.generate(startTime, event.getStart()));
    }

    public ArrayList<Alert> getAlerts(ArrayList<Event> events, ArrayList<SeriesGenerator> generators, DateTime start,
                                      DateTime end) {
        ArrayList<Alert> alerts = new ArrayList<>();

        for (Event e : events) {
            ArrayList<Alert> allAlerts = e.getAlerts();
            for (Alert alert : allAlerts) {
                if (alert.getTime().compareTo(start) >= 0 && alert.getTime().compareTo(end) <= 0) {
                    alerts.add(alert);
                }
            }
        }

        for (SeriesGenerator sg : generators) {
            if (sg.getCreatedOn().compareTo(start) >= 0 && sg.getEndDate().compareTo(end) <= 0) {
                alerts.addAll(sg.getTemplate().getAlerts());
            }
        }
        return alerts;
    }
}
