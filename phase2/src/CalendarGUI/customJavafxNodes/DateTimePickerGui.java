package CalendarGUI.customJavafxNodes;

import CalendarCore.calendar.CalendarClock;
import CalendarCore.dates.DateTime;
import CalendarGUI.theme.Theme;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;


public class DateTimePickerGui extends Stage {
    private Label hour;
    private Label min;
    private DateTime dateTime;

    private int origin;
    private DatePicker dp;
    private Pane pane;

    enum Type {HOUR, MINUTE}

    /**
     * Draws a circle of numbers
     *
     * @param radius   The radius of the clock
     * @param count    The number of ticks on the clock
     * @param fontSize The size of the ticks
     * @param type     Either HOUR or MINUTE
     */
    private void timePicker(int radius, int count, int fontSize, Type type) {
        for (int i = 1; i <= count; i++) {
            //The angle from the y axis
            double theta = (i - 1) * (2 * Math.PI / count) - (Math.PI / 2.0);

            // The x,y coordinates of the number
            double sin = (Math.sin(theta) * radius);
            double cos = (Math.cos(theta) * radius);

            double x, y;
            if (90 < theta && theta < 180) {
                y = origin - sin;
            } else {
                y = origin + sin;
            }

            if (theta > 180) {
                x = origin - cos;
            } else {
                x = origin + cos;
            }

            Label val = new Label();
            val.setAccessibleHelp(Integer.toString(i - 1));

            // Only display the i%5 numbers as digits
            if (type == Type.HOUR || (i - 1) % 5 == 0) {
                val.setText(Integer.toString(i - 1));
            } else {
                val.setText(".");
            }

            val.setFont(Font.font("Verdana", fontSize));
            val.relocate(x, y);
            val.setOnMouseEntered(event -> val.setCursor(Cursor.OPEN_HAND));

            // Sets the selected hour/minute as changes the colors appropriately
            val.setOnMouseClicked(event -> {
                if (Theme.getInstance().getStylesheet().contains("DarkMode.css")) {
                    if (type == Type.HOUR) {
                        if (hour != null) {
                            hour.setTextFill(Color.WHITE);
                        }
                        hour = val;
                        hour.setTextFill(Color.DODGERBLUE);

                    } else {
                        if (min != null) {
                            min.setTextFill(Color.WHITE);
                        }
                        min = val;
                        min.setTextFill(Color.DODGERBLUE);
                    }
                } else {
                    if (type == Type.HOUR) {
                        if (hour != null) {
                            hour.setTextFill(Color.BLACK);
                        }
                        hour = val;
                        hour.setTextFill(Color.DODGERBLUE);

                    } else {
                        if (min != null) {
                            min.setTextFill(Color.BLACK);
                        }
                        min = val;
                        min.setTextFill(Color.DODGERBLUE);
                    }
                }
            });

            pane.getChildren().add(val);
        }
    }

    /**
     * A popup for choosing a DateTime
     *
     * @param radius The radius of the clock
     */
    public DateTimePickerGui(int radius) {
        super();
        this.setTitle("Time Picker");
        this.setResizable(false);
        this.setAlwaysOnTop(true);

        // Removes the close button
        this.initStyle(StageStyle.UNDECORATED);

        pane = new Pane();

        origin = radius;
        // Makes the hour circle
        timePicker(radius, 24, 20, Type.HOUR);

        //Makes the minute circle
        timePicker(radius - 30, 60, 15, Type.MINUTE);

        dp = new DatePicker();
        dp.relocate(origin - 70, origin - 60);
        dp.setCenterShape(false);


        pane.getChildren().addAll(dp);

        Button submit = new Button("Submit");
        submit.relocate(origin - 70, origin);
        submit.setOnAction(event -> {
            int parsedHour, parsedMin;
            try {
                parsedHour = Integer.parseInt(hour.getAccessibleHelp());
                parsedMin = Integer.parseInt(min.getAccessibleHelp());
            } catch (NullPointerException e) {
                return;
            }

            if (dp.getValue() != null && parsedHour != -1 && parsedMin != -1) {
                setDateTime(parsedHour, parsedMin);
                close();
            }
        });

        Button now = new Button("Now");
        now.relocate(origin - 70, origin + 40);

        now.setOnAction(event -> {
            CalendarClock calendarClock = new CalendarClock();
            dateTime = calendarClock.getTime();
            close();
        });

        pane.getChildren().addAll(now, submit);

        Scene scene = new Scene(pane, radius * 2.1, radius * 2.1);
        scene.getStylesheets().add(Theme.getInstance().getStylesheet());
        this.setScene(scene);
    }

    /**
     * Sets the dateTime parameter
     *
     * @param hour The hour to set
     * @param min  The minute to set
     */
    private void setDateTime(int hour, int min) {
        LocalDate ld = dp.getValue();
        String day = String.format("%02d/%02d/%04d", ld.getDayOfMonth(), ld.getMonthValue(), ld.getYear());

        String time;
        if (hour >= 12) {
            time = String.format("%02d:%02d:pm", hour - 12, min);
        } else {
            time = String.format("%02d:%02d:am", hour, min);

        }
        dateTime = new DateTime(day, time);
    }

    public DateTime getDateTime() {
        return dateTime;
    }
}
