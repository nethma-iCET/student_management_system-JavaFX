package controllers.student;


import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import scene.SceneManager;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddStudentPageController implements Initializable {

    AddStudentController addStudentController = new AddStudentController();

    @FXML
    private ComboBox<String> cmbBatch;

    @FXML
    private ComboBox<String> cmbLectureMode;

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
        clearFields();
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
    void generateStudentIdOnAction(ActionEvent event) {
        String lectureMode = cmbLectureMode.getValue();
        String batch = cmbBatch.getValue();

        if (lectureMode == null || lectureMode.trim().isEmpty() || batch == null || batch.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select Lecture Mode and Batch to generate Student ID!").show();
            return;
        }

        try {
            String studentId = addStudentController.generateStudentId(lectureMode, batch);
            txtStudentId.setText(studentId);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate Student ID: " + e.getMessage()).show();
        }
    }

    @FXML
    void logoutOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/login_page.fxml");
    }

    @FXML
    void manageSubjectsOnAction(ActionEvent event) {

    }

    @FXML
    void saveStudentOnAction(ActionEvent event) {
        String studentId = txtStudentId.getText();
        String name = txtStudentName.getText();
        String nic = txtNic.getText();
        String email = txtEmail.getText();
        LocalDate birthdayDate = dateBirthday.getValue();
        String lectureMode = cmbLectureMode.getValue();
        String batch = cmbBatch.getValue();
        String address = txtAddress.getText();

        if (name == null || name.trim().isEmpty() ||
            nic == null || nic.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            birthdayDate == null ||
            lectureMode == null || lectureMode.trim().isEmpty() ||
            batch == null || batch.trim().isEmpty() ||
            address == null || address.trim().isEmpty()) {

            new Alert(Alert.AlertType.WARNING, "Please fill in all required fields!").show();
            return;
        }

        if (studentId == null || studentId.trim().isEmpty()) {
            studentId = addStudentController.generateStudentId(lectureMode, batch);
            txtStudentId.setText(studentId);
        }

        String modeCode = lectureMode.contains("-") ? lectureMode.split("-")[0].trim() : lectureMode.trim();

        try {
            boolean isAdded = addStudentController.addStudent(
                    studentId.trim(),
                    name.trim(),
                    nic.trim(),
                    email.trim(),
                    birthdayDate.toString(),
                    batch.trim(),
                    modeCode,
                    address.trim()
            );

            if (isAdded) {
                new Alert(Alert.AlertType.INFORMATION, "Student Added Successfully!\nStudent ID: " + studentId).show();
                clearFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to Add Student!").show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    private void clearFields() {
        txtStudentName.clear();
        txtNic.clear();
        txtEmail.clear();
        dateBirthday.setValue(null);
        cmbLectureMode.setValue(null);
        cmbBatch.setValue(null);
        txtAddress.clear();
        txtStudentId.clear();
    }

    @FXML
    void updateStudentOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/update_student_page.fxml");
    }

    @FXML
    void viewStudentsOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/view_student_page.fxml");
    }

    public void loadLectureModes() {
        ObservableList<String> lectureModes = addStudentController.getLectureModes();
        cmbLectureMode.setItems(lectureModes);
    }

    public void loadBatches() {
        ObservableList<String> batches = addStudentController.getBatches();
        cmbBatch.setItems(batches);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadLectureModes();
        loadBatches();
    }
    //----------------------------------------
}
