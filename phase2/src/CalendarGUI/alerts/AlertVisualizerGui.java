package CalendarGUI.alerts;

import CalendarCore.dates.DateTime;
import CalendarCore.alerts.Alert;
import CalendarCore.alerts.AlertGenerator;
import CalendarCore.alerts.AlertSystem;
import CalendarCore.events.Event;
import CalendarGUI.customJavafxNodes.DateTimePickerGui;
import CalendarGUI.theme.Theme;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class AlertVisualizerGui {
    private Event event;
    private AlertSystem aSys;
    private ArrayList<Alert> alertList;
    private VBox aList;

    public AlertVisualizerGui(AlertSystem aSys, Event event) {
        this.aSys = aSys;
        this.event = event;

        if (event == null) {
            this.alertList = new ArrayList<>();
        } else {
            this.alertList = event.getAlerts();
        }
    }

    /**
     * @return A scrollpane containing all of the alerts for an event
     */
    public ScrollPane getVisualization() {
        VBox alerts = new VBox();
        alerts.setAlignment(Pos.CENTER);

        Label a = new Label("Alerts:");
        a.setStyle("-fx-underline: true;");
        Label add = new Label(Character.toString((char) (0x2295))); // Unicode character 2295
        add.setOnMouseClicked(e -> {
            createAlert();
            updateAlertVisualization();
        });
        add.setCursor(Cursor.OPEN_HAND);

        aList = new VBox();
        updateAlertVisualization();
        alerts.getChildren().addAll(a, add, aList);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(alerts);
        scrollPane.setMaxWidth(300);
        return scrollPane;
    }

    /**
     * Updates the alerts currently being displayed in the EventCreationGui
     */
    private void updateAlertVisualization() {
        aList.getChildren().clear();

        for (Alert alert : alertList) {
            Label t = new Label(alert.toString());
            t.setOnMouseClicked(e -> {
                createAlert(alert);
                updateAlertVisualization();
            });
            t.setOnMouseEntered(e -> t.setCursor(Cursor.OPEN_HAND));
            aList.getChildren().add(t);
        }
    }

    /**
     * The Alert Creation popup
     *
     * @param alerts 0 or 1 alert. 0 = New alert Creation. 1 = Editing
     */
    private void createAlert(Alert... alerts) {
        Stage stage = new Stage();

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);

        Label tMessage = new Label("Message: ");
        TextField message = new TextField();
        HBox messageHBox = new HBox(tMessage, message);
        messageHBox.setAlignment(Pos.CENTER);
        messageHBox.setSpacing(30);

        DateTime[] start = new DateTime[1];
        Label tDate = new Label("Alert Time");
        Label date = new Label("Choose Date");
        date.setOnMouseClicked(e -> {
            DateTimePickerGui dateTimePickerGui = new DateTimePickerGui(300);
            dateTimePickerGui.showAndWait();
            date.setText(dateTimePickerGui.getDateTime().toString());
            start[0] = dateTimePickerGui.getDateTime();
        });
        date.setOnMouseEntered(e -> date.setCursor(Cursor.OPEN_HAND));
        HBox dateHBox = new HBox(tDate, date);
        dateHBox.setAlignment(Pos.CENTER);
        dateHBox.setSpacing(30);

        ToggleGroup alertType = new ToggleGroup();
        RadioButton single = new RadioButton("Single Alert");
        single.setToggleGroup(alertType);
        RadioButton series = new RadioButton("Countdown Alert");
        series.setToggleGroup(alertType);
        HBox radioButtons = new HBox(single, series);
        radioButtons.setAlignment(Pos.CENTER);
        radioButtons.setSpacing(30);

        ToggleGroup frequency = new ToggleGroup();
        RadioButton quad = new RadioButton("Quadhourly");
        RadioButton bi = new RadioButton("Bihourly");
        RadioButton hour = new RadioButton("Hourly");
        RadioButton day = new RadioButton("Daily");
        RadioButton week = new RadioButton("Weekly");

        quad.setToggleGroup(frequency);
        bi.setToggleGroup(frequency);
        hour.setToggleGroup(frequency);
        day.setToggleGroup(frequency);
        week.setToggleGroup(frequency);
        HBox freqHBox = new HBox(quad, bi, hour, day, week);

        single.setOnAction(e -> {
            tDate.setText("Alert Time");
            vBox.getChildren().remove(freqHBox);
        });
        series.setOnAction(e -> {
            tDate.setText("Start Time");
            vBox.getChildren().add(freqHBox);
        });

        Button save = new Button("Save Alert");
        save.setOnMouseClicked(e -> {
            if (event == null) {
                return;
            }

            if (((RadioButton) alertType.getSelectedToggle()).getText().equalsIgnoreCase("Single Alert")) {
                aSys.addAlert(event, message.getText(), start[0]);
            } else {
                AlertGenerator.FREQUENCY freq;
                switch (((RadioButton) frequency.getSelectedToggle()).getText()) {
                    case "Quadhourly":
                        freq = AlertGenerator.FREQUENCY.QUADHOURLY;
                        break;
                    case "Bihourly":
                        freq = AlertGenerator.FREQUENCY.BIHOURLY;
                        break;
                    case "Hourly":
                        freq = AlertGenerator.FREQUENCY.HOURLY;
                        break;
                    case "Daily":
                        freq = AlertGenerator.FREQUENCY.DAILY;
                        break;
                    case "Weekly":
                        freq = AlertGenerator.FREQUENCY.WEEKLY;
                        break;
                    default:
                        freq = AlertGenerator.FREQUENCY.DAILY;
                }

                aSys.addSeriesAlert(event, message.getText(), freq, start[0]);
            }
            stage.close();
        });

        // A delete button which is only visible if the event has already been created (alerts.length == 1)
        Button delete = new Button("Delete");
        delete.setStyle("-fx-text-fill: red");
        delete.setOnAction(e -> {
            event.getAlerts().remove(alerts[0]);
            stage.close();
        });

        vBox.getChildren().addAll(messageHBox, dateHBox, radioButtons, save);

        // Editing an alert
        if (alerts.length == 1) {
            Alert a = alerts[0];
            message.setText(a.getMessage());
            start[0] = a.getTime();
            date.setText(start[0].toString());
            vBox.getChildren().add(delete);
        }


        Scene scene = new Scene(vBox, 500, 400);
        scene.getStylesheets().add(Theme.getInstance().getStylesheet());
        stage.setScene(scene);
        stage.showAndWait();
    }
}
