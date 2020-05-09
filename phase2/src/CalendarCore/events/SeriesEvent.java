package CalendarCore.events;


import CalendarCore.dates.DateTime;

import java.io.Serializable;

public class SeriesEvent extends Event implements Serializable {
    /**
     * The name of this series
     */
    private String seriesName;

    /**
     * The SeriesGenerator for this series.
     */
    private SeriesGenerator generator;

    /**
     * @param seriesName The name of the series
     * @param title      The name of the event
     * @param start      The start date and time for the event
     * @param end        The end date and time for the event
     */
    public SeriesEvent(String seriesName, String title, DateTime start, DateTime end, SeriesGenerator generator) {
        super(title, start, end);
        this.seriesName = seriesName;
        this.generator = generator;

        // Adds in the id
        if (generator != null) {
            this.id = generator.getId();
        } else {
            id = seriesName.hashCode() + start.getTimeInMillis();
        }
    }

    /**
     * @return The String representation of this SeriesEvent
     */
    @Override
    public String toString() {
        return seriesName + " - " + super.toString();
    }


    /**
     * @return The generator which created this series. If the Series was Linked manually return null
     */
    public SeriesGenerator getGenerator() {
        if (this.generator != null) {
            return generator;
        }
        return null;
    }

    /**
     * @return The name of the series
     */
    public String getSeriesName() {
        return seriesName;
    }
}
