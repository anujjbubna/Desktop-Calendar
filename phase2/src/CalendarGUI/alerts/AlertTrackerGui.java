package CalendarGUI.alerts;

import CalendarCore.alerts.Alert;
import CalendarCore.calendar.CalendarSystem;
import CalendarCore.dates.DateTime;
import CalendarGUI.theme.Theme;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class AlertTrackerGui implements Observer {
    private ArrayList<CalendarSystem> calendarSystems;

    public AlertTrackerGui(ArrayList<CalendarSystem> calendarSystems) {
        this.calendarSystems = calendarSystems;
    }

    /**
     * A popup for an alert
     *
     * @param alert The alert to display
     */
    private void alertPopup(Alert alert) {
        Stage stage = new Stage();

        Label title = new Label("ALERT");
        title.setUnderline(true);

        Label message = new Label(alert.getMessage());

        Button close = new Button("Close");
        close.setOnAction(e -> stage.close());

        VBox vBox = new VBox(title, message, close);
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox, 150, 100);
        scene.getStylesheets().add(Theme.getInstance().getStylesheet());
        stage.setScene(scene);
        stage.show();
    }

    @Override
    /**
     * Checks for any alerts in that tick (start to end)
     * @param 0 the observable to observe
     * @param arg the Datetime objects to observe at
     */
    public void update(Observable o, Object arg) {
        DateTime[] times = (DateTime[]) arg;
        DateTime start = times[0];
        DateTime end = times[1];

        ArrayList<Alert> alerts = new ArrayList<>();
        for (CalendarSystem cs : calendarSystems) {
            alerts.addAll(cs.getAlertSystem().getAlerts(cs.getEventSystem().getListOfEvents(),
                    cs.getEventSystem().getListOfGenerators(), start, end));
        }

        for (Alert a : alerts) {
            alertPopup(a);
        }
    }
}
