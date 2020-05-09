package CalendarGUI.miscellaneous;

import CalendarCore.calendar.CalendarClock;
import CalendarCore.dates.DateTime;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.Observable;
import java.util.concurrent.atomic.AtomicInteger;

public class ClockGui extends Observable {
    private Label time;
    private Label multiplier;

    private Timeline clockUpdate;
    private CalendarClock calClock;
    private final String[] options = {"sec", "min", "hour", "day", "week"};

    public ClockGui(CalendarClock calClock) {
        time = new Label();
        this.calClock = calClock;

        // The index of options which is currently in use
        AtomicInteger n = new AtomicInteger(0);
        multiplier = new Label("1 tick = 1 " + options[0]);
        multiplier.setOnMouseClicked(event -> {
            // n cycles to after "week" we go to "sec"
            int x = n.incrementAndGet() % options.length;
            multiplier.setText("1 tick = 1 " + options[x]);
            changeSpeed(options[x]);
        });

        multiplier.setOnMouseEntered(e -> multiplier.setCursor(Cursor.OPEN_HAND));

        changeSpeed(options[0]);
    }

    /**
     * @return The controls for changing time
     */
    public HBox getTimePanel() {
        HBox control = new HBox(time, multiplier);
        control.setSpacing(25);
        control.setAlignment(Pos.CENTER);
        return control;
    }

    /**
     * Changes the speed that time runs
     *
     * @param option One of "sec", "min", "hour", "day", "week"
     */
    private void changeSpeed(String option) {
        this.stop();

        // The DateTimes now and next
        DateTime[] times = new DateTime[2];

        clockUpdate = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            time.setText(calClock.getTime().toString());

            times[0] = calClock.getTime();

            calClock.increment(option, 1);
            times[1] = calClock.getTime();

            // Tell the observers to call their update method
            setChanged();
            notifyObservers(times);

        }));
        clockUpdate.setCycleCount(Animation.INDEFINITE);
        clockUpdate.play();
    }

    /**
     * Stops the Timeline
     */
    public void stop() {
        if (clockUpdate != null) {
            clockUpdate.stop();
        }
    }
}


