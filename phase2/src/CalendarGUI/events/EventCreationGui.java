package CalendarGUI.events;

import CalendarCore.calendar.CalendarClock;
import CalendarCore.calendar.CalendarSystem;
import CalendarCore.dates.DateTime;
import CalendarCore.events.Event;
import CalendarCore.events.SeriesEvent;
import CalendarCore.events.SeriesGenerator;
import CalendarCore.exceptions.EventNotInSystemException;
import CalendarCore.exceptions.InvalidInputException;
import CalendarCore.users.Users;
import CalendarGUI.CalGui;
import CalendarGUI.alerts.AlertVisualizerGui;
import CalendarGUI.customJavafxNodes.CheckBoxRow;
import CalendarGUI.customJavafxNodes.DateTimePickerGui;
import CalendarGUI.memos.MemoCreatorGui;
import CalendarGUI.theme.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;


public class EventCreationGui extends Observable {
    private Stage stage;

    private Users users;
    private ArrayList<CalendarSystem> calendarSystems;
    private CalendarSystem currentCalendar;

    private String eventTitle;
    private DateTime start;
    private DateTime end;

    private Event event;

    private SeriesGenerator.FREQUENCY[] frequency;
    private DateTime[] endDate;
    private int count;

    private ArrayList<String> tagList;

    // This says whether we're editing or making a new event;
    private boolean isEditing;

    private boolean endsAfterCount;


    /**
     * Run the Event Creation Gui
     */
    public void showAndWait() {
        stage.showAndWait();
    }

    /**
     * A EventCreationGui for existing events -> Edits
     *
     * @param calGui          The CalGui which calls this
     * @param users           The list of all users
     * @param calendarSystems The list of all <user>s enabled CalenderSystems
     * @param event           The existing event
     */
    public EventCreationGui(CalGui calGui, Users users, ArrayList<CalendarSystem> calendarSystems, Event event) {
        this.addObserver(calGui);

        stage = new Stage();
        stage.setTitle("Event Creator");
        stage.setResizable(false);

        this.users = users;
        isEditing = true;

        this.calendarSystems = calendarSystems;
        // All users have a default calender
        currentCalendar = calendarSystems.get(0);
        this.event = event;

        // Checks is there is an individual event versus having a series created byt a generator
        if (event instanceof SeriesEvent && ((SeriesEvent) event).getGenerator() != null) {
            SeriesGenerator sGen = ((SeriesEvent) event).getGenerator();
            eventTitle = sGen.getTemplate().getTitle();
            start = sGen.getCreatedOn();
            end = sGen.getEndDate();
            tagList = sGen.getTemplate().getTags();

            seriesEvent();
        } else {
            eventTitle = event.getTitle();
            start = event.getStart();
            end = event.getEnd();
            tagList = event.getTags();

            singleEvent();
        }
    }

    /**
     * A EventCreationGui for existing events -> Creates
     *
     * @param calGui          The CalGui which calls this
     * @param users           The list of all users
     * @param calendarSystems The list of all <user>s enabled CalenderSystems
     */
    public EventCreationGui(CalGui calGui, Users users, ArrayList<CalendarSystem> calendarSystems) {
        this.addObserver(calGui);

        stage = new Stage();
        stage.setTitle("Event Creator");
        stage.setResizable(false);

        this.users = users;
        isEditing = false;

        this.calendarSystems = calendarSystems;
        currentCalendar = calendarSystems.get(0);

        eventTitle = "EventName";
        // Set the start day to 'today'
        CalendarClock calendarClock = new CalendarClock();
        start = calendarClock.getTime();
        end = start.clone().nextDate();
        tagList = new ArrayList<>();

        singleEvent();
    }

    /**
     * A single event creation scene
     */
    private void singleEvent() {
        Label title = new Label("Event Creator");
        title.setFont(Font.font("Verdana", 25));
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-underline: true;");

        Button swap = new Button("Series Event");
        swap.setOnAction(e -> seriesEvent());

        HBox eventTitle = new HBox();
        Label titlePrompt = new Label("Event Title: ");
        TextField titleField = new TextField(this.eventTitle);
        eventTitle.setAlignment(Pos.CENTER);
        eventTitle.getChildren().addAll(titlePrompt, titleField);


        Label startDateTime = new Label(start.toString());
        startDateTime.setOnMouseClicked(e -> {
            DateTimePickerGui sPicker = new DateTimePickerGui(300);
            sPicker.showAndWait();
            startDateTime.setText(sPicker.getDateTime().toString());
            start = sPicker.getDateTime();
        });
        startDateTime.setOnMouseEntered(e -> startDateTime.setCursor(Cursor.HAND));


        Label endDateTime = new Label(end.toString());
        endDateTime.setOnMouseClicked(e -> {
            DateTimePickerGui ePicker = new DateTimePickerGui(300);
            ePicker.showAndWait();
            endDateTime.setText(ePicker.getDateTime().toString());
            end = ePicker.getDateTime();
        });
        endDateTime.setOnMouseEntered(e -> endDateTime.setCursor(Cursor.HAND));

        HBox tags = new HBox();
        Label t = new Label("Tags:");
        TextField tagField = new TextField();
        tagField.setPromptText("tag1, tag2, tag3");

        tagField.setText(String.join(", ", tagList));
        tags.getChildren().addAll(t, tagField);
        tags.setAlignment(Pos.TOP_CENTER);

        AlertVisualizerGui avg = new AlertVisualizerGui(currentCalendar.getEventSystem().getAlertSystem(), event);

        MemoCreatorGui mcg = new MemoCreatorGui(currentCalendar.getMemoSystem(), event);
        Button memo = new Button("Add/Remove Memo");
        memo.setOnAction(e -> mcg.memoGui());

        Button save = new Button("Save Event");
        save.setOnMouseClicked(e -> {
            this.eventTitle = titleField.getText();

            this.tagList.clear();
            this.tagList = new ArrayList<>(Arrays.asList(tagField.getText().split(",(\\s)*")));

            if (isEditing) {
                saveSingleEvent();
            } else {
                createSingleEvent();
            }
            close();
        });

        Button delete = deleteEvent();

        Button share = shareEvent();

        TextField seriesName = new TextField();
        seriesName.setPromptText("Series Name");
        Button toSeries = new Button("Manually Add To Series");
        toSeries.setOnAction(e -> {
            if (seriesName.getText().equals("")) {
                return;
            }

            SeriesEvent temp = event.toSeries(seriesName.getText());
            temp.getTags().addAll(Arrays.asList(tagField.getText().split(",(\\s)*")));
            try {
                currentCalendar.getEventSystem().deleteEvent(event.getId(), currentCalendar.getMemoSystem());
            } catch (EventNotInSystemException ex) {
                ex.printStackTrace();
            }
            currentCalendar.getEventSystem().addSingleEvent(temp);

            close();
        });
        HBox seriesConversionHBox = new HBox(seriesName, toSeries);
        seriesConversionHBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10);
        // Only allow switching between Series and Single when it is a new event
        if (isEditing) {
            layout.getChildren().addAll(title, eventTitle, startDateTime, endDateTime, avg.getVisualization(),
                    tags, memo, save, delete, share, seriesConversionHBox);
        } else
            layout.getChildren().addAll(title, swap, eventTitle, startDateTime, endDateTime, tags, calendarPicker(), save);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setAlignment(Pos.CENTER);

        Scene single = new Scene(layout, 500, 500);
        single.getStylesheets().add(Theme.getInstance().getStylesheet());
        stage.setScene(single);
    }

    /**
     * A series event creation scene
     */
    private void seriesEvent() {
        Label title = new Label("Event Creator");
        title.setFont(Font.font("Verdana", 25));
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-underline: true;");

        Button swap = new Button("Monthly and Yearly Recurring Event");
        swap.setOnAction(e -> statutoryEvent());

        HBox eventTitle = new HBox();
        Label titlePrompt = new Label("Series Title: ");
        TextField titleField = new TextField(this.eventTitle);
        eventTitle.setAlignment(Pos.CENTER);
        eventTitle.getChildren().addAll(titlePrompt, titleField);


        Label startDateTime = new Label("Start: " + start.toString());
        startDateTime.setOnMouseClicked(e -> {
            DateTimePickerGui sPicker = new DateTimePickerGui(300);
            sPicker.showAndWait();
            startDateTime.setText("Start: " + sPicker.getDateTime().toString());
            start = sPicker.getDateTime();
        });
        startDateTime.setOnMouseEntered(e -> startDateTime.setCursor(Cursor.HAND));

        Label endDateTime = new Label("End Time: " + end.toString());
        endDateTime.setOnMouseClicked(e -> {
            DateTimePickerGui ePicker = new DateTimePickerGui(300);
            ePicker.showAndWait();
            endDateTime.setText("End Time: " + ePicker.getDateTime().toString());
            end = ePicker.getDateTime();
        });
        endDateTime.setOnMouseEntered(e -> endDateTime.setCursor(Cursor.HAND));

        VBox frequency = new VBox();
        frequency.setAlignment(Pos.CENTER);
        ChoiceBox<String> occurrences = new ChoiceBox<>();
        occurrences.getItems().addAll("Daily", "Weekly", "Monthly", "Yearly");
        CheckBoxRow cbr = new CheckBoxRow("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        occurrences.setOnAction(event -> {
            if (occurrences.getValue().equalsIgnoreCase("Weekly")) {
                frequency.getChildren().add(cbr);
            } else {
                frequency.getChildren().remove(cbr);
            }
        });
        frequency.getChildren().addAll(occurrences);


        ChoiceBox<String> choices = new ChoiceBox<>();
        choices.getItems().addAll("Ends On", "Ends After");

        VBox endChoice = new VBox(choices);
        endChoice.setAlignment(Pos.CENTER);

        final Label[] endDate = {new Label("Start: " + start.toString())};

        endDate[0].setOnMouseClicked(e -> {
            DateTimePickerGui sPicker = new DateTimePickerGui(300);
            sPicker.showAndWait();
            startDateTime.setText("Start: " + sPicker.getDateTime().toString());
            this.endDate = new DateTime[]{sPicker.getDateTime()};

        });

        TextField num = new TextField("1");
        Label text = new Label("occurrences");
        HBox numOccur = new HBox(num, text);
        numOccur.setAlignment(Pos.CENTER);

        choices.setOnAction(event -> {
            if (choices.getValue().equalsIgnoreCase("Ends On")) {
                endChoice.getChildren().removeAll(numOccur);
                endChoice.getChildren().add(endDate[0]);
                endsAfterCount = false;
            } else {
                endChoice.getChildren().removeAll(endDate[0]);
                endChoice.getChildren().add(numOccur);
                endsAfterCount = true;
            }
        });

        VBox recurrenceChoices = new VBox();
        recurrenceChoices.getChildren().addAll(frequency, endChoice);


        HBox tags = new HBox();
        Label t = new Label("Tags:");
        TextField tagField = new TextField();
        tagField.setPromptText("tag1, tag2, tag3");

        tagField.setText(String.join(", ", tagList));
        tags.getChildren().addAll(t, tagField);
        tags.setAlignment(Pos.TOP_CENTER);

        AlertVisualizerGui avg = new AlertVisualizerGui(currentCalendar.getEventSystem().getAlertSystem(), event);

        MemoCreatorGui mcg = new MemoCreatorGui(currentCalendar.getMemoSystem(), event);
        Button memo = new Button("Add/Remove Memo");
        memo.setOnAction(e -> mcg.memoGui());

        Button save = new Button("Save Event");
        save.setOnMouseClicked(e -> {
            this.eventTitle = titleField.getText();

            this.tagList = new ArrayList<>(Arrays.asList(tagField.getText().split(",(\\s)*")));

            // Get the rate at which the event will occur
            switch (occurrences.getValue()) {
                case "Daily":
                    this.frequency = new SeriesGenerator.FREQUENCY[]{SeriesGenerator.FREQUENCY.RECUR_DAILY};
                    break;
                case "Weekly":
                    this.frequency = new SeriesGenerator.FREQUENCY[cbr.getSelected().size()];
                    for (int i = 0; i < cbr.getSelected().size(); i++) {
                        switch (cbr.getSelected().get(i)) {
                            case "Mon":
                                this.frequency[i] = SeriesGenerator.FREQUENCY.RECUR_WEEKLY_MON;
                                break;
                            case "Tue":
                                this.frequency[i] = SeriesGenerator.FREQUENCY.RECUR_WEEKLY_TUE;
                                break;
                            case "Wed":
                                this.frequency[i] = SeriesGenerator.FREQUENCY.RECUR_WEEKLY_WED;
                                break;
                            case "Thu":
                                this.frequency[i] = SeriesGenerator.FREQUENCY.RECUR_WEEKLY_THU;
                                break;
                            case "Fri":
                                this.frequency[i] = SeriesGenerator.FREQUENCY.RECUR_WEEKLY_FRI;
                                break;
                            case "Sat":
                                this.frequency[i] = SeriesGenerator.FREQUENCY.RECUR_WEEKLY_SAT;
                                break;
                            case "Sun":
                                this.frequency[i] = SeriesGenerator.FREQUENCY.RECUR_WEEKLY_SUN;
                                break;
                            default:
                                System.out.println(cbr.getSelected().get(i));
                        }
                    }
                    break;
                case "Monthly":
                    this.frequency = new SeriesGenerator.FREQUENCY[]{SeriesGenerator.FREQUENCY.RECUR_MONTHLY};
                    break;
                case "Yearly":
                    this.frequency = new SeriesGenerator.FREQUENCY[]{SeriesGenerator.FREQUENCY.RECUR_YEARLY};
                    break;
            }

            //Get the value from the number of recurrences remaining
            count = Integer.parseInt(num.getText());

            if (isEditing) {
                saveSeriesEvent();
            } else {
                createSeriesEvent();
            }
            close();
        });

        Button delete = deleteEvent();

        Button share = shareEvent();

        VBox layout = new VBox(10);
        if (isEditing) {
            layout.getChildren().addAll(title, eventTitle, startDateTime, endDateTime,
                    recurrenceChoices, avg.getVisualization(), tags, memo, save, delete, share);
        } else
            layout.getChildren().addAll(title, swap, eventTitle, startDateTime, endDateTime,
                    recurrenceChoices, tags, calendarPicker(), save);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setAlignment(Pos.CENTER);
        Scene series = new Scene(layout, 500, 500);
        series.getStylesheets().add(Theme.getInstance().getStylesheet());
        stage.setScene(series);
    }

    /**
     * A Statutory event creation scene
     */
    private void statutoryEvent() {
        Label title = new Label("Event Creator");
        title.setFont(Font.font("Verdana", 25));
        title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-underline: true;");

        Button swap = new Button("Single Event");
        swap.setOnAction(e -> singleEvent());


        TextField titleField = new TextField(this.eventTitle);
        Label message1 = new Label(" will recur every ");
        ChoiceBox<String> when = new ChoiceBox<>();
        when.getItems().addAll("month", "year");

        ChoiceBox<String> week = new ChoiceBox<>();
        week.getItems().addAll("1st", "2nd", "3rd", "4th", "5th");

        Label message2 = new Label("It will occur on the ");
        Label message3 = new Label(" of ");
        Label message4 = new Label("the month");
        ChoiceBox<String> months = new ChoiceBox<>();
        months.getItems().addAll("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        Label blank = new Label("___________");


        ChoiceBox<String> day = new ChoiceBox<>();
        day.getItems().addAll("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        VBox questions = new VBox();
        HBox lineOne = new HBox();
        lineOne.setAlignment(Pos.CENTER);
        lineOne.getChildren().addAll(message1, titleField, when);

        HBox lineTwo = new HBox();
        lineTwo.setAlignment(Pos.CENTER);
        lineTwo.getChildren().addAll(message2, week, day, message3, blank);

        when.setOnAction(e -> {
            if (when.getValue().equalsIgnoreCase("year")) {
                if (lineTwo.getChildren().contains(blank)) {
                    lineTwo.getChildren().remove(blank);
                } else {
                    lineTwo.getChildren().remove(message4);
                }
                lineTwo.getChildren().add(months);
            } else {
                if (lineTwo.getChildren().contains(blank)) {
                    lineTwo.getChildren().remove(blank);
                } else {
                    lineTwo.getChildren().remove(months);
                }
                lineTwo.getChildren().add(message4);
            }

        });

        questions.getChildren().addAll(lineOne, lineTwo);


        Button save = new Button("Save Event");
        save.setOnMouseClicked(e -> {
            int d;
            switch (day.getValue()) {
                case "Sun":
                    d = 0;
                    break;
                case "Mon":
                    d = 1;
                    break;
                case "Tue":
                    d = 2;
                    break;
                case "Wed":
                    d = 3;
                    break;
                case "Thu":
                    d = 4;
                    break;
                case "Fri":
                    d = 5;
                    break;
                case "Sat":
                    d = 6;
                    break;
                default:
                    d = -1;
            }

            int w;
            switch (week.getValue()) {
                case "1st":
                    w = 1;
                    break;
                case "2nd":
                    w = 2;
                    break;
                case "3rd":
                    w = 3;
                    break;
                case "4th":
                    w = 4;
                    break;
                case "5th":
                    w = 5;
                    break;
                default:
                    w = -1;
            }

            int m;
            if (when.getValue().equalsIgnoreCase("month")) {
                // The event occurs monthly
                m = 100;
            } else {
                switch (months.getValue()) {
                    case "Jan":
                        m = 1;
                        break;
                    case "Feb":
                        m = 2;
                        break;
                    case "Mar":
                        m = 3;
                        break;
                    case "Apr":
                        m = 4;
                        break;
                    case "May":
                        m = 5;
                        break;
                    case "Jun":
                        m = 6;
                        break;
                    case "Jul":
                        m = 7;
                        break;
                    case "Aug":
                        m = 8;
                        break;
                    case "Sep":
                        m = 9;
                        break;
                    case "Oct":
                        m = 10;
                        break;
                    case "Nov":
                        m = 11;
                        break;
                    case "Dec":
                        m = 12;
                        break;
                    default:
                        m = -1;
                }
            }
            if (d != -1 && m != -1 && w != -1 && !titleField.getText().equalsIgnoreCase("")) {
                createStatutoryEvent(titleField.getText(), d, w, m);
                close();
            }
        });


        VBox layout = new VBox(title, swap, titleField, questions, calendarPicker(), save);
        layout.setPadding(new Insets(10, 10, 10, 10));
        layout.setAlignment(Pos.CENTER);

        Scene statutory = new Scene(layout, 500, 500);
        statutory.getStylesheets().add(Theme.getInstance().getStylesheet());
        stage.setScene(statutory);
    }

    /**
     * Creates a new Statutory event
     *
     * @param title The event title
     * @param day   The day of the week the event occurs
     * @param week  The week the event occurs
     * @param month The month the event occurs
     */
    private void createStatutoryEvent(String title, int day, int week, int month) {
        currentCalendar.getEventSystem().createStatutoryEvent(title, day, week, month);
    }

    /**
     * Creates a new SeriesEvent
     */
    private void createSeriesEvent() {
        long id;
        if (endsAfterCount) {
            id = currentCalendar.getEventSystem().createSeriesEvent(eventTitle, String.format("Series:%s",
                    eventTitle), start, end, frequency, count);
        } else {
            id = currentCalendar.getEventSystem().createSeriesEvent(eventTitle, String.format("Series:%s",
                    eventTitle), start, end, frequency, endDate[0]);
        }

        // Adds tags to the generator's template
        try {
            SeriesGenerator sg = currentCalendar.getEventSystem().getGeneratorByID(id);
            sg.getTemplate().addTags(tagList);
        } catch (InvalidInputException e) {
            // Fail silently
        }

    }

    /**
     * Creates a new Event
     */
    private void createSingleEvent() {
        // Creates a new event and returns its id number
        long id = currentCalendar.getEventSystem().createSingleEvent(eventTitle, start, end);

        Event e = currentCalendar.getEventSystem().getEventByID(id);
        e.getTags().clear();
        e.addTags(tagList);
    }

    /**
     * Saves a SeriesEvent
     */
    private void saveSeriesEvent() {
        SeriesGenerator sGen = ((SeriesEvent) event).getGenerator();
        sGen.getTemplate().setTitle(eventTitle);
        sGen.getTemplate().setStart(start);
        sGen.getTemplate().setEnd(end);
        sGen.setFrequency(frequency);

        Event template = sGen.getTemplate();
        template.getTags().clear();
        template.addTags(tagList);

        if (endsAfterCount) {
            sGen.setEndCount(count);
        } else {
            sGen.setEndDate(endDate[0]);
        }
    }

    /**
     * Save a Event
     */
    private void saveSingleEvent() {
        event.setTitle(eventTitle);
        event.setStart(start);
        event.setEnd(end);

        event.getTags().clear();
        event.getTags().addAll(tagList);
    }

    /**
     * Button to delete Events
     *
     * @return The button
     */
    private Button deleteEvent() {
        Button button = new Button("Delete");
        button.setStyle("-fx-text-fill: red");
        button.setOnAction(f -> {
            try {
                currentCalendar.getEventSystem().deleteEvent(event.getId(), currentCalendar.getMemoSystem());
            } catch (EventNotInSystemException e) {
                e.printStackTrace();
            }
            close();
        });
        return button;
    }

    /**
     * Button to share event with another user
     *
     * @return The Button
     */
    private Button shareEvent() {
        Button share = new Button("Share");
        share.setOnAction(e -> {
            EventShareGui esg = new EventShareGui(users, event);
            esg.shareEvent();
        });
        return share;
    }

    /**
     * Closes the EventCreationGui. Notifies CalGui to redraw events and memos
     */
    public void close() {
        setChanged();
        notifyObservers();
        stage.close();

    }

    /**
     * @return A VBox for picking a CalenderSystem in which to create the event
     */
    private VBox calendarPicker() {
        Label title = new Label("Calendar");
        title.setUnderline(true);

        String[] calendersInString = calendersToString();

        ChoiceBox<String> calenders = new ChoiceBox<>();
        calenders.getItems().addAll(calendersInString);
        calenders.setOnAction(e -> {
            if (calenders.getValue() == null) {
                return;
            }

            int i = 0;
            while (!calenders.getValue().equalsIgnoreCase(calendersInString[i])) {
                i++;
            }
            currentCalendar = calendarSystems.get(i);
        });
        VBox vBox = new VBox(title, calenders);
        vBox.setAlignment(Pos.CENTER);

        return vBox;
    }

    /**
     * @return A string array of CalenderSystem names
     */
    private String[] calendersToString() {
        String[] arr = new String[calendarSystems.size()];
        for (int i = 0; i < calendarSystems.size(); i++) {
            arr[i] = calendarSystems.get(i).getNameOfCalendar();
        }
        return arr;
    }
}