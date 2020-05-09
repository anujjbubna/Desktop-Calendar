package CalendarCore;

import CalendarCore.users.User;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveHandler {

    private String userPath;
    private String colorPath;


    /**
     * Sets the path color information will be read from and written to.
     * @param colorPath The path to the file.
     */
    public void setColorPath(String colorPath) {
        this.colorPath = colorPath;
    }

    /**
     * Sets the path user information will be read from and written to.
     * @param userPath The path to the file.
     */
    public void setUserPath(String userPath) {
        this.userPath = userPath;
    }

    /**
     * Attempts to read a list of users from the file specified by userPath.
     * @return the list of users found in the file or an empty list if none were found
     */
    public ArrayList<User> readUserFile() {
        try {
            FileInputStream calFile = new FileInputStream(userPath);
            ObjectInputStream objReader = new ObjectInputStream(calFile);

            // Get the number of saved cal_core.users.Users.
            Integer numSavedUsers = (Integer) objReader.readObject();

            ArrayList<User> loadedUsers = new ArrayList<>();
            for (int i = 0; i < numSavedUsers; i++) {
                User u = (User) objReader.readObject();
                loadedUsers.add(u);
            }
            objReader.close();

            return loadedUsers;
        } catch (FileNotFoundException e) {
            System.out.println("Error reading saved calendar file.");
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * Attempts to write a list of users to the file specified by userPath.
     * @param allUsers the list of all users to be written to the file.
     */
    public void saveUserFile(ArrayList<User> allUsers) {
        try {
            FileOutputStream writer = new FileOutputStream(userPath);
            ObjectOutputStream objWriter = new ObjectOutputStream(writer);

            // Before writing any users, write how many there will be.
            Integer numUsers = allUsers.size();
            objWriter.writeObject(numUsers);

            for (User u : allUsers) {
                objWriter.writeObject(u);
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing users to file.");
        }

    }

    /**
     * Attempts to read a color hashmap from the file specified by colorPath.
     * @return the Hashmap saved in the file or null if it wasn't able to be read or there was nothing in it.
     */
    public HashMap<String, Color> readColorFile() {
        try {
            FileInputStream calFile = new FileInputStream(colorPath);
            ObjectInputStream objReader = new ObjectInputStream(calFile);

            HashMap<String, SerializableColor> colorMap = (HashMap<String, SerializableColor>) objReader.readObject();

            HashMap<String, Color> toReturn = new HashMap<>();
            for (HashMap.Entry<String, SerializableColor> entry : colorMap.entrySet()) {
                String key = entry.getKey();
                SerializableColor value = entry.getValue();
                toReturn.put(key, value.getFXColor());
            }

            objReader.close();

            return toReturn;
        } catch (FileNotFoundException e) {
            System.out.println("Error reading saved color file.");
        } catch (EOFException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Attempts to write a color hashmap to the file specified by userPath.
     * @param colorMap color hashmap to save
     */
    public void saveColorFile(HashMap<String, Color> colorMap) {
        try {
            // First convert the HashMap to be Serializable
            HashMap<String, SerializableColor> toSave = new HashMap<>();
            for (HashMap.Entry<String, Color> entry : colorMap.entrySet()) {
                String key = entry.getKey();
                Color value = entry.getValue();
                toSave.put(key, new SerializableColor(value));
            }

            FileOutputStream writer = new FileOutputStream(colorPath);
            ObjectOutputStream objWriter = new ObjectOutputStream(writer);

            objWriter.writeObject(toSave);

            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing users to file.");
        }

    }

}
