package CalendarGUI.memos;

import CalendarCore.calendar.CalendarSystem;
import CalendarCore.events.Event;
import CalendarCore.events.SeriesEvent;
import CalendarCore.memos.Memo;
import CalendarGUI.CalGui;
import CalendarGUI.theme.Theme;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Observable;


public class MemoGui extends Observable {
    private ArrayList<CalendarSystem> calendarSystems;
    private CalendarSystem currentCalendar;

    public MemoGui(CalGui calGui, ArrayList<CalendarSystem> calendarSystems) {
        this.calendarSystems = calendarSystems;
        addObserver(calGui);
    }

    /**
     * @return A VBox for creating/viewing memos
     */
    public VBox run() {
        Label title = new Label("Memos");

        Label label = new Label("New Memo: ");
        TextField description = new TextField();
        description.setPromptText("Memo Description");

        VBox memos = drawMemoListing();

        // Creates new memos
        String[] calendersInString = new String[calendarSystems.size()];
        for (int i = 0; i < calendarSystems.size(); i++) {
            calendersInString[i] = calendarSystems.get(i).getNameOfCalendar();
        }
        ChoiceBox<String> calenders = new ChoiceBox<>();
        calenders.getItems().addAll(calendersInString);
        calenders.setOnAction(e -> {
            if (calenders.getValue() == null) {
                return;
            }

            // Gets the currently selected calender
            int i = 0;
            while (!calenders.getValue().equalsIgnoreCase(calendersInString[i])) {
                i++;
            }
            currentCalendar = calendarSystems.get(i);
        });
        Button add = new Button("+");
        HBox addHBox = new HBox(label, description, calenders, add);
        add.setOnAction(e -> {
            String s = description.getText();
            if (s.equalsIgnoreCase("") | currentCalendar == null) {
                return;
            }
            description.setText("");
            currentCalendar.getMemoSystem().createMemo(s);
            memos.getChildren().clear();
            memos.getChildren().add(drawMemoListing());

        });
        addHBox.setAlignment(Pos.CENTER);

        // Scrollpane of the current memos
        ScrollPane scroll = new ScrollPane();
        scroll.getStylesheets().add(Theme.getInstance().getStylesheet());
        scroll.setContent(memos);
        scroll.setPrefHeight(400);
        scroll.setPrefWidth(550);

        VBox memoPane = new VBox(title, addHBox, scroll);
        memoPane.setAlignment(Pos.TOP_LEFT);

        return memoPane;
    }

    /**
     * @return A VBox with memos and all of the associated events
     */
    private VBox drawMemoListing() {
        VBox memos = new VBox();
        for (CalendarSystem cs : calendarSystems) {
            for (Memo m : cs.getMemoSystem().getListOfMemo()) {
                String s = String.format("%s \t %s", Character.toString((char) (0x2296)), m);
                Label memName = new Label(s);
                memName.setOnMouseEntered(e -> memName.setCursor(Cursor.OPEN_HAND));
                memName.setOnMouseClicked(e -> {
                    cs.getMemoSystem().deleteMemo(m);
                    setChanged();
                    notifyObservers();
                });

                VBox events = new VBox(memName);
                for (long id : m.getListOfEventID()) {
                    Event e = cs.getEventSystem().getEventByID(id);
                    Label l;
                    // Only single events are displayed with a DateTime
                    if (e.getStart().getTimeInMillis() != Long.MAX_VALUE && !(e instanceof SeriesEvent)) {
                        l = new Label("\t" + e.toString());
                    } else {
                        l = new Label("\t" + e.getTitle());
                    }
                    events.getChildren().add(l);
                }
                memos.getChildren().add(events);
            }
        }
        return memos;
    }
}
