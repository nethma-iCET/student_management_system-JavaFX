package controllers.student;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import scene.SceneManager;

public class UpdateStudentPageController {

    @FXML
    private ComboBox<?> cmbBatch;

    @FXML
    private DatePicker dateBirthday;

    @FXML
    private TextArea txtAddress;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtNic;

    @FXML
    private TextField txtSearchStudentId;

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
    void searchStudentOnAction(ActionEvent event) {

    }

    @FXML
    void updateStudentDetailsOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/update_student_page.fxml");
    }

    @FXML
    void updateStudentOnAction(ActionEvent event) {

    }

    @FXML
    void viewStudentsOnAction(ActionEvent event) {

    }

}
