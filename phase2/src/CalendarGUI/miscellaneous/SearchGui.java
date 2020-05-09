package CalendarGUI.miscellaneous;

import CalendarCore.calendar.CalendarClock;
import CalendarCore.calendar.CalendarSystem;
import CalendarCore.dates.DateTime;
import CalendarCore.Searcher;
import CalendarCore.events.Event;
import CalendarCore.memos.Memo;
import CalendarCore.users.Users;
import CalendarGUI.CalGui;
import CalendarGUI.events.EventCreationGui;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.util.ArrayList;

public class SearchGui {
    private Searcher searcher;
    private ArrayList<CalendarSystem> calendarSystems;
    private CalGui calGui;
    private Users users;

    public SearchGui(Searcher searcher, ArrayList<CalendarSystem> calendarSystems, CalGui calGui, Users users) {
        this.searcher = searcher;
        this.calendarSystems = calendarSystems;
        this.calGui = calGui;
        this.users = users;
    }

    /**
     * Returns the VBox which contains the search controls
     *
     * @return The VBox
     */
    public VBox run() {
        Label title = new Label("Search");

        Label text = new Label("Search by");
        ChoiceBox<String> choice = new ChoiceBox<>();
        choice.getItems().addAll("Start Date", "End Date", "Memo Text", "Tag");

        Button search = new Button("Search");

        VBox events = new VBox();

        HBox control = new HBox(text, choice);
        TextField input = new TextField();
        DatePicker datePicker = new DatePicker();

        search.setOnAction(event -> {
            events.getChildren().clear();
            ArrayList<Event> lst = new ArrayList<>();
            switch (choice.getValue()) {
                case "Start Date":
                    if (datePicker.getValue() != null) {
                        DateTime start = convertDate(datePicker.getValue());
                        for (CalendarSystem cs : calendarSystems) {
                            lst.addAll(searcher.eventsFromStartDatetime(cs.getEventSystem().getEventsBetween(start,
                                    start), start));
                        }
                    }
                    fill(events, lst);
                    break;
                case "End Date":
                    if (datePicker.getValue() != null) {
                        DateTime end = convertDate(datePicker.getValue());
                        for (CalendarSystem cs : calendarSystems) {
                            lst.addAll(searcher.eventsFromEndDatetime(cs.getEventSystem().getEventsBetween(end, end),
                                    end));
                        }
                    }
                    fill(events, lst);
                    break;
                case "Memo Text":
                    if (input.getText().length() > 0) {
                        for (CalendarSystem cs : calendarSystems) {
                            Memo m = cs.getMemoSystem().getMemoFromText(input.getText());
                            if (m != null) {
                                for (long i : m.getListOfEventID()) {
                                    lst.add(cs.getEventSystem().getEventByID(i));
                                }
                            }
                        }
                        fill(events, lst);
                    }
                    break;
                case "Tag":
                    for (CalendarSystem cs : calendarSystems) {
                        CalendarClock calendarClock = new CalendarClock();
                        // Creating start and end times for the method getEventsBetween()
                        DateTime start = calendarClock.getTime();
                        DateTime end = calendarClock.getTime();
                        long offset = (long) (12 * 31 * 24 * 60 * 60);
                        start.setTimeInMillis(start.getTimeInMillis() - offset);
                        end.setTimeInMillis(end.getTimeInMillis() + offset);


                        // This will only get generated events from a 2 year window around today.
                        lst.addAll(searcher.getEventByTag(cs.getEventSystem().getEventsBetween(start, end),
                                input.getText()));
                    }
                    fill(events, lst);
            }
        });

        choice.setOnAction(event -> {
            if (choice.getValue().equalsIgnoreCase("Tag") |
                    choice.getValue().equalsIgnoreCase("Memo Text")) {
                control.getChildren().clear();
                control.getChildren().addAll(text, choice, input, search);
            } else {
                control.getChildren().clear();
                control.getChildren().addAll(text, choice, datePicker, search);
            }
        });

        VBox searchPane = new VBox(title, control, events);
        searchPane.setAlignment(Pos.TOP_LEFT);

        return searchPane;
    }

    /**
     * Fills a VBox with events that can be clicked on
     *
     * @param vBox   The VBox to fill
     * @param events The events to insert
     */
    private void fill(VBox vBox, ArrayList<Event> events) {
        for (Event e : events) {
            Rectangle rec = new Rectangle(300, 25, Color.TURQUOISE);
            rec.setStroke(Color.BLACK);
            Label t = new Label(e.getTitle());
            StackPane s = new StackPane();
            s.getChildren().addAll(rec, t);
            s.setOnMouseClicked(event -> {
                EventCreationGui ecg = new EventCreationGui(calGui, users, calendarSystems, e);
                ecg.showAndWait();
            });
            vBox.getChildren().add(s);
        }
    }

    /**
     * Converts LocalDates to DateTimes
     *
     * @param l A LocalDate object of time t
     * @return A DateTime object of time t
     */
    private DateTime convertDate(LocalDate l) {
        return new DateTime(String.format("%02d/%02d/%04d", l.getDayOfMonth(), l.getMonthValue(), l.getYear()));
    }
}
