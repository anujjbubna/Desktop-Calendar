package CalendarGUI.theme;

/**
 * A Singleton which returns the current Theme (A css stylesheet)
 */
public class Theme {
    private static Theme theme = new Theme();

    private String currentStylesheet;


    public static Theme getInstance() {
        return theme;
    }

    public Theme() {
        currentStylesheet = "CalendarGUI/theme/DarkMode.css";
    }


    public String getStylesheet() {
        return currentStylesheet;
    }

    /**
     * Sets the currentStylesheet
     *
     * @param n: 0 = Dark mode, 1 = Light mode
     */
    public void change(int n) {
        switch (n) {
            case 0:
                currentStylesheet = "CalendarGUI/theme/DarkMode.css";
                break;
            case 1:
                currentStylesheet = "CalendarGUI/theme/LightMode.css";
                break;
        }

    }
}
