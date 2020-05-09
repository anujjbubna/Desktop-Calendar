package CalendarCore.memos;

import java.io.Serializable;
import java.util.ArrayList;

public class Memo implements Serializable {

    private String description;

    private ArrayList<Long> listOfEventID = new ArrayList<>();

    /**
     * @param description the description of the memo
     */
    public Memo(String description) {
        this.description = description;
    }


    /**
     * @return the list of event ID that this memo relates to
     */
    public ArrayList<Long> getListOfEventID() {
        return listOfEventID;
    }

    public void addEvent(Long eventID) {
        listOfEventID.add(eventID);
    }

    /**
     * @return The String representation of this Memo
     */
    @Override
    public String toString() {
        return description;
    }

    /**
     * Removes an event from the listOfEventID
     *
     * @param eventID eventID to be removed
     */
    public void removeEventID(long eventID) {
        listOfEventID.remove(eventID);
    }

}
