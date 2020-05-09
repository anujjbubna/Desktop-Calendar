package CalendarGUI;

import CalendarCore.calendar.CalendarSystem;
import CalendarCore.exceptions.CalendarInTheSystemException;
import CalendarCore.users.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

class CalendarSystemsGui extends Observable {
    private ArrayList<CalendarSystem> selected;

    CalendarSystemsGui(Observer observer) {
        selected = new ArrayList<>();
        addObserver(observer);
    }

    /**
     * @param currentUser      The current logged in user
     * @param colorCalenderMap The Map of CalenderSystem to their associated JavaFX colors.
     * @return A Control panel for CalenderSystems (Creation/Enabling/Disabling)
     */
    VBox getPane(User currentUser, HashMap<String, Color> colorCalenderMap) {
        VBox vBox = new VBox();

        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.setSpacing(5);

        Label label = new Label("My Calendars");
        label.setUnderline(true);

        // Draws the existing calenders
        VBox calenders = new VBox();
        for (CalendarSystem cs : currentUser.getListOfCalendarSystem()) {
            String s = cs.getNameOfCalendar();

            CheckBox box = new CheckBox(s);
            // Colors the background of the text
            box.setBackground(new Background(new BackgroundFill(colorCalenderMap.get(cs.getNameOfCalendar()),
                    CornerRadii.EMPTY, Insets.EMPTY)));

            box.setOnAction(event -> {
                if (selected.contains(cs)) {
                    selected.remove(cs);
                } else {
                    selected.add(cs);
                }

                // Notifies observers to redraw as some calenders are now enabled/disabled
                setChanged();
                notifyObservers();
            });

            //The default calender cannot be disabled
            if (s.equalsIgnoreCase(currentUser.getUserName())) {
                box.setSelected(true);
                box.setDisable(true);
                selected.add(cs);
            }

            calenders.getChildren().add(box);
        }

        TextField calName = new TextField();
        calName.setPromptText("Calender Name");
        ColorPicker cp = new ColorPicker();
        cp.setValue(Color.LIGHTBLUE);

        Button add = new Button("+");
        add.setOnAction(e -> {
            if (calName.getText().equalsIgnoreCase("")) {
                return;
            }

            CalendarSystem calSys = new CalendarSystem(calName.getText());
            try {
                currentUser.createNewCalendarSystem(calSys);

                // Adds the custom color
                colorCalenderMap.putIfAbsent(calSys.getNameOfCalendar(), cp.getValue());

                CheckBox box = new CheckBox(calName.getText());
                box.setBackground(new Background(new BackgroundFill(colorCalenderMap.get(calSys.getNameOfCalendar()),
                        CornerRadii.EMPTY, Insets.EMPTY)));
                box.setOnAction(event -> {
                    if (selected.contains(calSys)) {
                        selected.remove(calSys);
                    } else {
                        selected.add(calSys);
                    }

                    setChanged();
                    notifyObservers();
                });
                calenders.getChildren().add(box);
                calName.setText("");
            } catch (CalendarInTheSystemException e1) {
                // Fail silently
            }
        });

        HBox addHBox = new HBox(calName, cp, add);


        vBox.getChildren().addAll(label, calenders, addHBox);

        return vBox;
    }

    /**
     * @return The currently selected(enabled) CalenderSystems
     */
    ArrayList<CalendarSystem> getSelected() {
        return selected;
    }
}
