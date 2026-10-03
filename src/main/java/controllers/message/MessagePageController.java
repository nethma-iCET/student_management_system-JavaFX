package controllers.message;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MessagePageController {

    @FXML
    private Button btnAction;

    @FXML
    private VBox dialogCard;

    @FXML
    private Label lblDescription;

    @FXML
    private Label lblIcon;

    @FXML
    private Label lblTitle;

    @FXML
    void closeOnAction(ActionEvent event) {

    }

}
