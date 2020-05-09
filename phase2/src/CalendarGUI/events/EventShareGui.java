package CalendarGUI.events;

import CalendarCore.calendar.CalendarSystem;
import CalendarCore.users.User;
import CalendarCore.users.Users;
import CalendarCore.events.Event;
import CalendarCore.events.SeriesEvent;
import CalendarCore.events.SeriesGenerator;
import CalendarCore.exceptions.CalendarNotInTheSystemException;
import CalendarCore.exceptions.InvalidInputException;
import CalendarGUI.theme.Theme;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

class EventShareGui {
    private Users users;
    private Event event;
    private SeriesGenerator generator;

    EventShareGui(Users users, Event e) {
        this.users = users;
        if (e instanceof SeriesEvent) {
            generator = ((SeriesEvent) e).getGenerator();
        } else {
            event = e;
        }

    }

    /**
     * A popup for sharing events with another user
     */
    void shareEvent() {
        Stage stage = new Stage();

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);

        Label title = new Label("Share Event:");

        Label shareWith = new Label("Share With: ");
        ChoiceBox<String> userChoices = new ChoiceBox<>();
        String[] strUsers = usersToString(users.getAllUsers());
        userChoices.getItems().addAll(strUsers);
        HBox whoHBox = new HBox(shareWith, userChoices);
        whoHBox.setAlignment(Pos.CENTER);

        Label whereTo = new Label("Insert Into: ");
        ChoiceBox<String> calenders = new ChoiceBox<>();
        userChoices.setOnAction(e -> {
            if (userChoices.getValue() == null) {
                return;
            }

            int i = 0;
            while (!userChoices.getValue().equalsIgnoreCase(strUsers[i])) {
                i++;
            }
            User selected = users.getAllUsers().get(i);

            calenders.getItems().clear();
            calenders.getItems().addAll(calendersToString(selected.getListOfCalendarSystem()));

        });
        HBox whereHBox = new HBox(whereTo, calenders);
        whereHBox.setAlignment(Pos.CENTER);

        Button share = new Button("Share");
        share.setOnAction(e -> {
            try {
                if (event != null) {
                    users.addSingleEvent(userChoices.getValue(), calenders.getValue(), event);
                } else {
                    users.addSeriesEvent(userChoices.getValue(), calenders.getValue(), generator);
                }
            } catch (CalendarNotInTheSystemException | InvalidInputException err) {
                err.printStackTrace();
            }
            stage.close();
        });


        vBox.getChildren().addAll(title, whoHBox, whereHBox, share);
        Scene scene = new Scene(vBox, 300, 300);
        scene.getStylesheets().addAll(Theme.getInstance().getStylesheet());
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * @param arrayList Users
     * @return Usernames as a array of strings
     */
    private String[] usersToString(ArrayList<User> arrayList) {
        String[] arr = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            arr[i] = arrayList.get(i).getUserName();
        }
        return arr;
    }

    /**
     * @param arrayList CalenderSystems
     * @return CalenderSystem names as a String array
     */
    private String[] calendersToString(ArrayList<CalendarSystem> arrayList) {
        String[] arr = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            arr[i] = arrayList.get(i).getNameOfCalendar();
        }
        return arr;
    }
}
