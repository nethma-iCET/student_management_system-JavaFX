package controllers.dashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import scene.SceneManager;

public class MainDashboardPageController {

    @FXML
    private TableColumn<?, ?> colCourse;

    @FXML
    private TableColumn<?, ?> colEmail;

    @FXML
    private TableColumn<?, ?> colStatus;

    @FXML
    private TableColumn<?, ?> colStudentId;

    @FXML
    private TableColumn<?, ?> colStudentName;

    @FXML
    private Label lblCurrentTime;

    @FXML
    private Label lblDbmsMarks;

    @FXML
    private Label lblGreeting;

    @FXML
    private Label lblPrfMarks;

    @FXML
    private Label lblTotalStudents;

    @FXML
    private Label lblTotalSubjects;

    @FXML
    private TableView<?> tblRecentStudents;

    @FXML
    void addMarksOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/add_marks_page.fxml");
    }

    @FXML
    void addStudentOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/add_student_page.fxml");
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
        SceneManager.switchPage("/view/login_page.fxml");
    }

    @FXML
    void manageSubjectsOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/manage_subjects_page.fxml");
    }

    @FXML
    void updateStudentOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/update_student_page.fxml");
    }

    @FXML
    void viewStudentsOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/view_student_page.fxml");
    }

}
