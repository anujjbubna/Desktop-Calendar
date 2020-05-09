package CalendarGUI.userHandling;

import CalendarCore.users.User;
import CalendarCore.users.Users;
import CalendarCore.exceptions.UserExistsException;
import CalendarGUI.theme.Theme;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class NewUserGui {
    private Users users;
    private TextField uName;
    private PasswordField uPass;


    public NewUserGui(Users users) {
        this.users = users;
    }

    /**
     * A popup for creating a new user
     */
    public void run() {
        Stage primaryStage = new Stage();
        Label title = new Label("Create New User");

        uName = new TextField();
        uName.setPromptText("Username");
        uPass = new PasswordField();
        uPass.setPromptText("Password");

        Button submit = new Button("Submit");
        submit.setOnAction(event -> {
            if (users.validUser(uName.getText(), uPass.getText())) {
                try {
                    users.createNewUser(new User(uName.getText(), uPass.getText()));
                    primaryStage.close();
                } catch (UserExistsException e) {
                    // Fail Silently
                }
            }
        });

        VBox elements = new VBox(title, uName, uPass, submit);
        elements.setAlignment(Pos.CENTER);

        Scene scene = new Scene(elements, 200, 200);
        scene.getStylesheets().add(Theme.getInstance().getStylesheet());
        primaryStage.setScene(scene);
        primaryStage.showAndWait();
    }
}
