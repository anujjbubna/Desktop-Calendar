package CalendarGUI.customJavafxNodes;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class CheckBoxRow extends HBox {
    private ArrayList<String> selected;

    /**
     * A horizontal row of checkboxes
     *
     * @param items The different check boxes to include
     */
    public CheckBoxRow(String... items) {
        selected = new ArrayList<>();

        setAlignment(Pos.CENTER);
        setSpacing(5);

        for (String s : items) {
            CheckBox box = new CheckBox(s);
            box.setOnAction(event -> {
                if (selected.contains(s)) {
                    selected.remove(s);
                } else {
                    selected.add(s);
                }
            });
            getChildren().add(box);

        }
    }

    /**
     * @return The currently selected checkboxes
     */
    public ArrayList<String> getSelected() {
        return selected;
    }
}
