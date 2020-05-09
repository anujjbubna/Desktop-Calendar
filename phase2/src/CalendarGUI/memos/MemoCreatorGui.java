package CalendarGUI.memos;

import CalendarCore.events.Event;
import CalendarCore.memos.Memo;
import CalendarCore.memos.MemoSystem;
import CalendarGUI.theme.Theme;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MemoCreatorGui {
    private MemoSystem mSys;
    private Event event;
    private Memo memo;
    private boolean isNew;
    private Stage stage;

    public MemoCreatorGui(MemoSystem mSys, Event event) {
        this.mSys = mSys;
        this.event = event;

        // Prevents the Memo System from trying to edit a non-created event
        if (event == null) {
            return;
        }

        // Loads in the old memo
        if (mSys.getMemo(event.getId()) == null) {
            isNew = true;
        } else {
            isNew = false;
            memo = mSys.getMemo(event.getId());
        }

        this.stage = new Stage();
    }

    /**
     * A popup for adding events to a PRE-CREATED memo
     */
    public void memoGui() {
        HBox memos = new HBox();
        Label memoLabel = new Label("Add to memo");
        ChoiceBox<Object> memoChoice = new ChoiceBox<>();
        memoChoice.getItems().addAll(mSys.getListOfMemo().toArray());
        memos.getChildren().addAll(memoLabel, memoChoice);

        Button button = new Button();
        if (isNew) {
            button.setText("Add Event to Memo");
        } else {
            button.setText("Remove Event From Memo");
        }

        button.setOnMouseClicked(e -> {
            if (isNew) {
                if (memoChoice.getValue() == null) {
                    return;
                }
                memo = (Memo) memoChoice.getValue();
                mSys.addEventToMemo(memo, event.getId());
            } else {
                mSys.removeEventInMemo(event.getId());
            }
            stage.close();
        });

        VBox vBox = new VBox();
        if (isNew) {
            vBox.getChildren().addAll(memos, button);
        } else {
            vBox.getChildren().addAll(button);
        }

        Scene scene = new Scene(vBox, 500, 400);
        scene.getStylesheets().add(Theme.getInstance().getStylesheet());
        stage.setScene(scene);
        stage.showAndWait();
    }
}
