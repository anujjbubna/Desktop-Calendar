package CalendarCore;

import java.io.Serializable;

import javafx.scene.paint.Color;

public class SerializableColor implements Serializable {
    private double r;
    private double g;
    private double b;
    private double a;

    public SerializableColor(Color color) {
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.a = color.getOpacity();
    }

    public SerializableColor(double red, double green, double blue, double alpha) {
        this.r = red;
        this.g = green;
        this.b = blue;
        this.a = alpha;
    }

    public Color getFXColor() {
        return new Color(r, g, b, a);
    }
}
