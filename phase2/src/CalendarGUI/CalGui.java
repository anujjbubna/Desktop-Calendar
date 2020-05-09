package CalendarGUI;

import CalendarCore.SaveHandler;
import CalendarCore.Searcher;
import CalendarCore.alerts.Alert;
import CalendarCore.calendar.CalendarClock;
import CalendarCore.calendar.CalendarSystem;
import CalendarCore.users.User;
import CalendarCore.users.Users;
import CalendarGUI.alerts.AlertTrackerGui;
import CalendarGUI.events.EventCreationGui;
import CalendarGUI.events.EventsVisualizerGui;
import CalendarGUI.memos.MemoGui;
import CalendarGUI.miscellaneous.ClockGui;
import CalendarGUI.miscellaneous.SearchGui;
import CalendarGUI.theme.Theme;
import CalendarGUI.userHandling.LoginGui;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicReference;

public class CalGui extends Application implements Observer {
    public Users users;
    public User currentUser;
    private ArrayList<CalendarSystem> calendarSystems;
    public HashMap<String, Color> colorCalenderMap;

    private CalendarClock clock;
    private BorderPane bp;
    private EventsVisualizerGui evg;
    private Searcher searcher;

    private SaveHandler saveHandler;

    private Stage primaryStage;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.clock = new CalendarClock();

        // Catches close requests and calls our close methods instead
        primaryStage.setOnCloseRequest(e -> close());

        // The All of the possible users
        users = new Users();

        // Handles saving to an external file
        saveHandler = new SaveHandler();

        // The current calender colors
        saveHandler.setColorPath("data/savedColors.ser");
        HashMap<String, Color> savedCalendarMap = saveHandler.readColorFile();
        if (savedCalendarMap != null) {
            colorCalenderMap = savedCalendarMap;
        } else {
            colorCalenderMap = new HashMap<>();
        }

        // Loads the saved list of users
        saveHandler.setUserPath("data/savedUsers.ser");
        users.setAllUsers(saveHandler.readUserFile());

        startLoginScene();

        primaryStage.show();
    }

    /**
     * Saves the data to an external file and then closes the window
     */
    public void close() {
        saveHandler.saveUserFile(users.getAllUsers());
        saveHandler.saveColorFile(colorCalenderMap);
        primaryStage.close();
    }

    /**
     * The login panel
     */
    private void startLoginScene() {
        // Logs in the current user
        LoginGui loginGui = new LoginGui(users);
        primaryStage.setScene(loginGui.getLogin(this));
    }

    /**
     * The main calender panel
     */
    public void startCalenderScene() {
        // The pane which holds all of the different gui nodes
        bp = new BorderPane();

        // Creates the control buttons on the left side of the screen
        bp.setLeft(controlButtons());

        // Creates the clock panel
        ClockGui clockGui = new ClockGui(clock);
        bp.setBottom(clockGui.getTimePanel());

        // Initialize the alert Tracker
        AlertTrackerGui aTracker = new AlertTrackerGui(calendarSystems);
        clockGui.addObserver(aTracker);

        // Shows the events in the centre of the screen
        evg = new EventsVisualizerGui(this, users, calendarSystems, colorCalenderMap);

        Button logout = new Button("Logout");
        logout.setOnAction(e -> {
            // Stops checking this users events for alerts
            clockGui.stop();
            startLoginScene();
        });

        Button help = new Button("Help");
        help.setOnAction(e -> {
            File pdfFile = new File("data/helpPage.pdf");
            if (!pdfFile.exists()) {
                System.out.println("The help feature is not working due to an incorrect file path");
            }
            getHostServices().showDocument(pdfFile.toURI().toString());
        });

        HBox top = new HBox(toggleView(evg), darkModePanel(), logout, exportControls(), help);
        top.setSpacing(100);
        bp.setTop(top);

        bp.setCenter(evg.drawEvents(clock.getTime(), "month"));

        searcher = new Searcher();
        bp.setRight(drawRight(searcher));

        scene = new Scene(bp);
        scene.getStylesheets().add(Theme.getInstance().getStylesheet());

        primaryStage.setScene(scene);
    }

    /**
     * @param evg The current EventVisualizerGui
     * @return An HBox containing the buttons associated with changing the view in the EventVisualizerGui
     */
    private HBox toggleView(EventsVisualizerGui evg) {
        ToggleGroup tg = new ToggleGroup();

        RadioButton month = new RadioButton("Month");
        month.setToggleGroup(tg);
        month.setOnAction(event -> bp.setCenter(evg.drawEvents(clock.getTime(), "month")));
        month.setSelected(true);
        month.fire();

        RadioButton week = new RadioButton("Week");
        week.setToggleGroup(tg);
        week.setOnAction(event -> bp.setCenter(evg.drawEvents(clock.getTime(), "week")));

        RadioButton day = new RadioButton("Day");
        day.setToggleGroup(tg);
        day.setOnAction(event -> bp.setCenter(evg.drawEvents(clock.getTime(), "day")));

        RadioButton past = new RadioButton("Past Events");
        past.setToggleGroup(tg);
        past.setOnAction(event -> bp.setCenter(evg.drawEvents(clock.getTime(), "past")));

        RadioButton current = new RadioButton("Current Events");
        current.setToggleGroup(tg);
        current.setOnAction(event -> bp.setCenter(evg.drawEvents(clock.getTime(), "current")));

        RadioButton future = new RadioButton("Future Events");
        future.setToggleGroup(tg);
        future.setOnAction(event -> bp.setCenter(evg.drawEvents(clock.getTime(), "future")));

        RadioButton all = new RadioButton("All Events");
        all.setToggleGroup(tg);
        all.setOnAction(event -> bp.setCenter(evg.drawEvents(clock.getTime(), "")));

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(month, week, day, past, current, future, all);
        return buttons;
    }

    /**
     * @return A HBox which contains the controls for changing the Theme
     */
    private HBox darkModePanel() {
        ToggleButton dark = new ToggleButton("Dark");
        ToggleButton light = new ToggleButton("Light");

        dark.setSelected(true);

        ToggleGroup toggleGroup = new ToggleGroup();
        dark.setToggleGroup(toggleGroup);
        light.setToggleGroup(toggleGroup);

        dark.setOnAction(event -> {
            scene.getStylesheets().clear();
            Theme.getInstance().change(0); // Dark mode
            scene.getStylesheets().add(Theme.getInstance().getStylesheet());
            update(null, null);
        });

        light.setOnAction(event -> {
            scene.getStylesheets().clear();
            Theme.getInstance().change(1); //Light mode
            scene.getStylesheets().add(Theme.getInstance().getStylesheet());
            update(null, null);
        });

        return new HBox(dark, light);
    }

    /**
     * @return The buttons on the left side which create events and calenders
     */
    private VBox controlButtons() {
        Button addEvent = new Button("Create Event");
        addEvent.setOnAction(event -> {
            EventCreationGui ecg = new EventCreationGui(this, users, calendarSystems);
            ecg.showAndWait();
        });

        CalendarSystemsGui calendarSystemsGui = new CalendarSystemsGui(this);
        // The currently enabled CalenderSystems
        calendarSystems = calendarSystemsGui.getSelected();

        VBox vBox = new VBox(30);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(addEvent, calendarSystemsGui.getPane(currentUser, colorCalenderMap));
        return vBox;
    }

    /**
     * @param searcher The searcher
     * @return A VBox on the right hand side which allows for viewing memos as well as searching through events
     * and alerts
     */
    private VBox drawRight(Searcher searcher) {
        MemoGui memoGui = new MemoGui(this, calendarSystems);

        SearchGui sGui = new SearchGui(searcher, calendarSystems, this, users);
        return new VBox(memoGui.run(), allAlerts(), sGui.run());
    }

    /**
     * @return A HBox on the top which allows for the exporting to events to an external file
     */
    private HBox exportControls() {
        ChoiceBox<String> toExport = new ChoiceBox<>();
        toExport.getItems().addAll("Hour", "Day", "Week", "Month");

        AtomicReference<String> path = new AtomicReference<>();
        Button getFile = new Button("Select Path");
        getFile.setOnMouseClicked(event -> {
            // Choose a file path
            DirectoryChooser dc = new DirectoryChooser();
            File file = dc.showDialog(primaryStage);
            if (file != null) {
                path.set(file.getAbsolutePath());
                getFile.setText(file.getName());
            }
        });

        Button export = new Button("Export");
        export.setOnAction(e -> {
            if (path.get().equalsIgnoreCase("") || toExport.getValue() == null) {
                return;
            }
            switch (toExport.getValue()) {
                case "Hour":
                    currentUser.exportHour(clock.getTime(), path.get());
                    break;
                case "Day":
                    currentUser.exportDay(clock.getTime(), path.get());
                    break;
                case "Week":
                    currentUser.exportWeek(clock.getTime(), path.get());
                    break;
                case "Month":
                    currentUser.exportMonth(clock.getTime(), path.get());
                    break;
            }

            getFile.setText("Select Path");
            path.set("");
        });

        return new HBox(toExport, getFile, export);
    }

    /**
     * @return A ScrollPane containing all of the Alerts
     */
    private ScrollPane allAlerts() {
        Label title = new Label("All Alerts");
        title.setUnderline(true);

        VBox alerts = new VBox(title);
        for (CalendarSystem cs : calendarSystems) {
            for (Alert a : cs.getAllAlerts()) {
                Label l = new Label(a.toString());
                alerts.getChildren().add(l);
            }
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefHeight(350);
        scrollPane.setPrefWidth(550);

        scrollPane.setContent(alerts);

        return scrollPane;
    }

    @Override
    /**
     * Redraws the events in the centre and redraws the right side (Memos and Searching)
     * Note. The centre is reset to the current month view
     * @param o the observable object to be observed
     * @param arg the Datetime objects
     */
    public void update(Observable o, Object arg) {
        bp.setCenter(evg.drawEvents(clock.getTime(), "month"));
        bp.setRight(drawRight(searcher));
    }
}

