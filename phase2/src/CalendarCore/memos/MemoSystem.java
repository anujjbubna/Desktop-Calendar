package CalendarCore.memos;

import java.io.Serializable;
import java.util.ArrayList;

public class MemoSystem implements Serializable {

    private ArrayList<Memo> listOfMemo = new ArrayList<>();

    /**
     * Creates a memo
     *
     * @param description The description of memo
     */
    public void createMemo(String description) {
        Memo memo = new Memo(description);
        listOfMemo.add(memo);
    }

    /**
     * Deletes a memo for the event
     *
     * @param eventID The ID of the event
     */
    public void removeEventInMemo(long eventID) {
        Memo m = getMemo(eventID);
        if (m != null) {
            m.removeEventID(eventID);
        }
    }

    /**
     * Deletes a Memo
     *
     * @param m The memo to delete
     */
    public void deleteMemo(Memo m) {
        listOfMemo.remove(m);
    }

    /**
     * Add event to an existing memo
     *
     * @param memo    to which eventID is added
     * @param eventID added to memo
     */
    public void addEventToMemo(Memo memo, Long eventID) {
        memo.addEvent(eventID);
    }

    /**
     * @return the list of memo in user's memoSystem
     */
    public ArrayList<Memo> getListOfMemo() {
        return listOfMemo;
    }

    /**
     * @param eventID The id of the event
     * @return Memo that relates to the eventID or None if the eventID does not have a memo
     */
    public Memo getMemo(long eventID) {
        for (Memo m : listOfMemo) {
            if (m.getListOfEventID().contains(eventID)) {
                return m;
            }
        }
        return null;
    }

    public Memo getMemoFromText(String memoText) {
        for (Memo m : listOfMemo) {
            if (m.toString().equals(memoText)) {
                return m;
            }
        }
        return null;
    }
}
