package CalendarGUI.events;

import CalendarCore.calendar.CalendarSystem;
import CalendarCore.dates.DateTime;
import CalendarCore.events.Event;
import CalendarCore.events.EventSystem;
import CalendarCore.users.Users;
import CalendarGUI.CalGui;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class EventsVisualizerGui {
    private CalGui calGui;

    private Users users;
    private ArrayList<CalendarSystem> calendarSystems;
    private HashMap<String, Color> colorCalenderMap;

    public EventsVisualizerGui(CalGui calGui, Users users, ArrayList<CalendarSystem> calendarSystems,
                               HashMap<String, Color> colorCalenderMap) {
        this.calGui = calGui;
        this.users = users;
        this.calendarSystems = calendarSystems;
        this.colorCalenderMap = colorCalenderMap;
    }

    /**
     * A GridPane showing 1 month of events staring on the first of the month followed by 5 weeks
     *
     * @param startDate The first day of the month
     * @return The month
     */
    private GridPane drawMonth(DateTime startDate) {
        GridPane grid = new GridPane();
        for (int w = 0; w < 5; w++) {
            for (int d = 0; d < 7; d++) {
                VBox events = new VBox();
                events.setAlignment(Pos.CENTER);
                events.getChildren().add(new Label(startDate.toFmtString("E dd/MM/yyyy")));

                for (CalendarSystem cs : calendarSystems) {
                    EventSystem eventSystem = cs.getEventSystem();
                    for (Event e : eventSystem.getEventsOnDate(startDate)) {
                        events.getChildren().add(singleEvent(e, colorCalenderMap.get(cs.getNameOfCalendar()),
                                190, 30));
                    }
                }
                ScrollPane sp = new ScrollPane();
                sp.setPrefWidth(200);
                sp.setPrefHeight(300);
                sp.setContent(events);
                grid.add(sp, d, w);
                startDate = startDate.nextDate();
            }
        }
        return grid;
    }

    /**
     * A GridPane showing 1 week of events staring on the monday
     *
     * @param startDate The first day of the week (Sunday)
     * @return The week
     */
    private GridPane drawWeek(DateTime startDate) {
        startDate = getFirstDay("week", startDate);
        GridPane grid = new GridPane();
        for (int d = 0; d < 7; d++) {
            VBox events = new VBox();
            events.setAlignment(Pos.CENTER);
            events.getChildren().add(new Label(startDate.toFmtString("E dd/MM/yyyy")));
            for (CalendarSystem cs : calendarSystems) {
                EventSystem eventSystem = cs.getEventSystem();
                for (Event e : eventSystem.getEventsOnDate(startDate)) {
                    events.getChildren().add(singleEvent(e, colorCalenderMap.get(cs.getNameOfCalendar()),
                            190, 30));
                }
            }

            ScrollPane sp = new ScrollPane();
            sp.setPrefWidth(200);
            sp.setPrefHeight(1500);
            sp.setContent(events);
            grid.add(sp, d, 0);
            startDate = startDate.nextDate();
        }
        return grid;
    }

    /**
     * @param events The events to draw
     * @return A ScrollPane of all the events
     */
    private ScrollPane drawList(ArrayList<Event> events) {
        ScrollPane sp = new ScrollPane();
        sp.setPrefHeight(1500);
        sp.setPrefWidth(1400);
        VBox vBox = new VBox();
        for (Event e : events) {
            vBox.getChildren().add(singleEvent(e, Color.LIGHTBLUE, 1350, 40));
        }
        sp.setContent(vBox);
        return sp;
    }

    /**
     * A Factory Design Pattern for getting the centre visual for displaying events
     *
     * @param startDate The start date
     * @param field     Either "day", "week", "month", "past", "future", "current" or "" = All events
     * @return Returns the visual for the centre
     */
    public Node drawEvents(DateTime startDate, String field) {
        ArrayList<Event> lst = new ArrayList<>();

        switch (field) {
            case "day":
                for (CalendarSystem cs : calendarSystems) {
                    lst.addAll(cs.getEventSystem().getEventsOnDate(startDate));
                }
                return drawList(lst);
            case "week":
                return weekControlPanel(startDate);
            case "month":
                return monthControlPanel(startDate);
            case "past":
                for (CalendarSystem cs : calendarSystems) {
                    lst.addAll(cs.getEventSystem().getPastEvents());
                }
                return drawList(lst);
            case "future":
                for (CalendarSystem cs : calendarSystems) {
                    lst.addAll(cs.getEventSystem().getUpcomingEvents());
                }
                return drawList(lst);
            case "current":
                for (CalendarSystem cs : calendarSystems) {
                    lst.addAll(cs.getEventSystem().getOngoingEvents());
                }
                return drawList(lst);
            default:
                DateTime beginning = new DateTime();
                beginning.setTimeInMillis(0);
                DateTime end = new DateTime();

                for (CalendarSystem cs : calendarSystems) {
                    lst.addAll(cs.getEventSystem().getEventsBetween(beginning, end));
                }
                return drawList(lst);
        }
    }

    /**
     * @param field "week" or "month
     * @param date  The date
     * @return The first of the month or the Sunday of the week respectively
     */
    private DateTime getFirstDay(String field, DateTime date) {
        DateTime start = date.clone();
        switch (field) {
            case "week":
                while (start.get(Calendar.DAY_OF_WEEK) != 1) {
                    start = start.prevDate();
                }
                break;
            case "month":
                while (start.get(Calendar.DATE) != 1) {
                    start = start.prevDate();
                }
                break;
        }
        return start;
    }

    /**
     * @param e      The event
     * @param c      The color for said event
     * @param width  The box width
     * @param height The box height
     * @return A clickable event which is but in other JavaFX nodes
     */
    private StackPane singleEvent(Event e, Color c, int width, int height) {
        Rectangle rec = new Rectangle(width, height, c);
        rec.setStroke(Color.BLACK);
        Label t = new Label(e.getTitle());
        StackPane s = new StackPane();
        s.getChildren().addAll(rec, t);
        s.setOnMouseClicked(event -> {
            EventCreationGui ecg = new EventCreationGui(calGui, users, calendarSystems, e);
            ecg.showAndWait();
        });
        return s;
    }

    /**
     * @param startDate The start date of the month
     * @return A VBox for displaying month as well as its controls
     */
    private VBox monthControlPanel(DateTime startDate) {
        AtomicInteger num = new AtomicInteger(0);

        VBox month = new VBox();

        Button back = new Button("<");
        Button next = new Button(">");
        Label current = new Label(startDate.toSimpleString());
        HBox controls = new HBox(back, next, current);
        controls.setAlignment(Pos.CENTER);

        back.setOnAction(event -> {
            int n = num.decrementAndGet();
            DateTime date = getFirstDay("month", startDate.offsetMonths(n));
            current.setText(date.toSimpleString());
            month.getChildren().clear();
            month.getChildren().addAll(controls, drawMonth(date));
        });

        next.setOnAction(event -> {
            int n = num.incrementAndGet();
            DateTime date = getFirstDay("month", startDate.offsetMonths(n));
            current.setText(date.toSimpleString());
            month.getChildren().clear();
            month.getChildren().addAll(controls, drawMonth(date));
        });

        month.getChildren().addAll(controls, drawMonth(getFirstDay("month", startDate)));

        return month;
    }

    /**
     * @param startDate The start date of the week
     * @return A VBox for displaying week as well as its controls
     */
    private VBox weekControlPanel(DateTime startDate) {
        AtomicInteger num = new AtomicInteger(0);

        VBox week = new VBox();

        Button back = new Button("<");
        Button next = new Button(">");
        Label current = new Label(startDate.toSimpleString());
        HBox controls = new HBox(back, next, current);
        controls.setAlignment(Pos.CENTER);

        back.setOnAction(event -> {
            int n = num.decrementAndGet();
            DateTime date = getFirstDay("week", startDate.offsetWeeks(n));
            current.setText(date.toSimpleString());
            week.getChildren().clear();
            week.getChildren().addAll(controls, drawWeek(date));
        });

        next.setOnAction(event -> {
            int n = num.incrementAndGet();
            DateTime date = getFirstDay("week", startDate.offsetWeeks(n));
            current.setText(date.toSimpleString());
            week.getChildren().clear();
            week.getChildren().addAll(controls, drawWeek(date));
        });

        week.getChildren().addAll(controls, drawWeek(getFirstDay("week", startDate)));
        return week;
    }
}
