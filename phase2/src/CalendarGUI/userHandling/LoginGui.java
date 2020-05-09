package CalendarGUI.userHandling;

import CalendarCore.exceptions.InvalidInputException;
import CalendarCore.users.User;
import CalendarCore.users.Users;
import CalendarGUI.CalGui;
import CalendarGUI.theme.Theme;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LoginGui {
    private Users users;
    private TextField uName;
    private PasswordField uPass;
    private User currentUser;

    public LoginGui(Users users) {
        this.users = users;
    }

    // Returns the login scene
    public Scene getLogin(CalGui calGui) {
        Label title = new Label("Login User");

        uName = new TextField();
        uName.setPromptText("Username");
        uPass = new PasswordField();
        uPass.setPromptText("Password");

        Button submit = new Button("Login");
        submit.setOnAction(event -> {
            if (users.validUser(uName.getText(), uPass.getText())) {
                try {
                    currentUser = users.login(uName.getText(), uPass.getText());
                    calGui.currentUser = currentUser;
                    calGui.startCalenderScene();
                    calGui.colorCalenderMap.put(currentUser.getDefaultCalendar().getNameOfCalendar()
                            , Color.LIGHTSEAGREEN);
                } catch (InvalidInputException e) {
                    //Fail silently
                }
            }
        });

        Button create = new Button("Create New User");
        create.setOnAction(event -> {
            NewUserGui ngu = new NewUserGui(users);
            ngu.run();
        });


        VBox elements = new VBox(title, uName, uPass, submit, create);
        elements.setAlignment(Pos.CENTER);

        Scene scene = new Scene(elements, 200, 200);
        scene.getStylesheets().add(Theme.getInstance().getStylesheet());
        return scene;
    }
}
