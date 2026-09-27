package controllers.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginPageController {

    LoginController loginController = new LoginController();

    @FXML
    private Button btnLogIn;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUserName;

    @FXML
    void btnLoginOnAction(ActionEvent event) {
       if(loginController.checkUserNameandPassword(txtUserName.getText(), txtPassword.getText())){
           Stage stage = new Stage();
           try {
               stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/home_page.fxml"))));
           } catch (IOException e) {
               throw new RuntimeException(e);
           }
           stage.show();
       }
    }

}
