package CalendarCore.users;

import CalendarCore.events.Event;
import CalendarCore.events.SeriesGenerator;
import CalendarCore.exceptions.CalendarNotInTheSystemException;
import CalendarCore.exceptions.InvalidInputException;
import CalendarCore.exceptions.UserExistsException;

import java.util.ArrayList;

public class Users {

    // The list of all users and all events respectively.
    private ArrayList<User> allUsers;

    public Users(){
        allUsers = new ArrayList<>();
    }


    public ArrayList<User> getAllUsers(){
        return allUsers;
    }

    public void createNewUser(User user) throws UserExistsException {
        for (User u: allUsers){
            if (user.getUserName().equals(u.getUserName())){
                throw new UserExistsException();
            }
        }

        allUsers.add(user);
    }

    /**
     * Logs the user in
     * @param username - username of the user.
     * @param password - password of the user.
     * @return A User instance
     * @throws InvalidInputException if the input is invalid
     */
    public User login(String username, String password) throws InvalidInputException {
        for (User u:allUsers){
            if(u.login(username, password)){
                return u;
            }
        }
        throw new InvalidInputException();
    }

    /**
     *
     * @param username - username of the user.
     * @param password - password of the user.
     * @return true if the username and password are valid.
     */
    public boolean validUser(String username, String password){
        return username.length() >= 1 && password.length() >= 1;
    }

    public void setAllUsers(ArrayList<User> allUsers) {
        this.allUsers = allUsers;
    }



    /**
     * add a single event to a particular user's calendar
     * @param username the username that you want to share the event to
     * @param calendar the calendar name you want to share the event to
     * @param e the event you want to share
     * @throws CalendarNotInTheSystemException if the calendar name is not valid
     * @throws InvalidInputException if the user does not exist
     */

    public void addSingleEvent(String username, String calendar, Event e) throws CalendarNotInTheSystemException, InvalidInputException{
        boolean shared = false;
        for (User u: allUsers){
            if (u.getUserName().equals(username)){
                try {
                    u.addSingleEvent(calendar, e);
                    shared = true;
                }catch (CalendarNotInTheSystemException e1){
                    throw new CalendarNotInTheSystemException();
                }
            }
        }
        if (!shared){
            throw new InvalidInputException();
        }
    }

    /**
     * add a series event to a particular user's calendar
     * @param username the username that you want to share the event to
     * @param calendar the calendar name you want to share the event to
     * @param sg the series generator of the series event you want to share
     * @throws CalendarNotInTheSystemException if the calendar name is not valid
     * @throws InvalidInputException if the user does not exist
     */
    public void addSeriesEvent(String username, String calendar, SeriesGenerator sg) throws CalendarNotInTheSystemException, InvalidInputException{
        boolean shared = false;
        for (User u: allUsers){
            if (u.getUserName().equals(username)){
                try {
                    u.addSeriesEvent(calendar, sg);
                    shared = true;
                }catch (CalendarNotInTheSystemException e1){
                    throw new CalendarNotInTheSystemException();
                }
            }
        }
        if (!shared){
            throw new InvalidInputException();
        }
    }
}

