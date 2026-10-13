package controllers.student;


import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import scene.SceneManager;

import java.net.URL;
import java.util.ResourceBundle;

public class AddStudentPageController implements Initializable {

    @FXML
    private ComboBox<String> cmbBatch;

    @FXML
    private DatePicker dateBirthday;

    @FXML
    private TextArea txtAddress;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtNic;

    @FXML
    private TextField txtStudentId;

    @FXML
    private TextField txtStudentName;

    @FXML
    void addMarksOnAction(ActionEvent event) {

    }

    @FXML
    void addStudentOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/add_student_page.fxml");
    }

    @FXML
    void clearOnAction(ActionEvent event) {

    }

    @FXML
    void dashboardOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/main_dashboard.fxml");

    }

    @FXML
    void deleteStudentOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/delete_student_page.fxml");
    }

    @FXML
    void logoutOnAction(ActionEvent event) {

    }

    @FXML
    void manageSubjectsOnAction(ActionEvent event) {

    }

    @FXML
    void saveStudentOnAction(ActionEvent event) {

    }

    @FXML
    void updateStudentOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/update_student_page.fxml");
    }

    @FXML
    void viewStudentsOnAction(ActionEvent event) {

    }
    // load batch details into the comboBox
    ObservableList<String> batches = FXCollections.observableArrayList(
            "iCM120",
            "iCD121",
            "iCD122",
            "iCM123",
            "iCD124",
            "iCD125"
    );

    public void loadBatches(){
        cmbBatch.setItems(batches);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBatches();
    }
    //----------------------------------------
}
