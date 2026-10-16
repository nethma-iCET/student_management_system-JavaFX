package controllers.student;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.*;
import scene.SceneManager;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class DeleteStudentPageController implements Initializable {

    DeleteStudentController deleteStudentController = new DeleteStudentController();

    @FXML
    private TextArea txtAddress;

    @FXML
    private TextField txtBatch;

    @FXML
    private TextField txtBirthday;

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

    private ContextMenu suggestionMenu = new ContextMenu();
    private boolean isSelectingSuggestion = false;

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
    void deleteStudentRecordOnAction(ActionEvent event) {
        String studentId = txtStudentId.getText();
        String studentName = txtStudentName.getText();

        if (studentId == null || studentId.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please search and select a student to delete!").show();
            return;
        }

        Alert confirmationAlert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete student: " + studentName + " (" + studentId + ")?\nThis action cannot be undone.",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText(null);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                boolean isDeleted = deleteStudentController.deleteStudent(studentId.trim());
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Student Record Deleted Successfully!").show();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete student!").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            }
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
    void searchStudentOnAction(ActionEvent event) {
        String query = txtSearchStudentId.getText();
        if (query == null || query.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a Student ID or Name to search!").show();
            return;
        }

        String searchKey = query.contains(" - ") ? query.split(" - ")[0].trim() : query.trim();
        loadStudentDetails(searchKey);
    }

    private void loadStudentDetails(String searchKey) {
        suggestionMenu.hide();
        Map<String, String> student = deleteStudentController.searchStudent(searchKey);
        if (student != null) {
            txtStudentId.setText(student.get("student_id"));
            txtStudentName.setText(student.get("student_name"));
            txtNic.setText(student.get("nic"));
            txtEmail.setText(student.get("email"));
            txtBirthday.setText(student.get("birthday"));
            txtBatch.setText(student.get("batch"));
            txtAddress.setText(student.get("address"));
        } else {
            new Alert(Alert.AlertType.WARNING, "No student found with ID or Name: " + searchKey).show();
        }
    }

    @FXML
    void updateStudentOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/update_student_page.fxml");
    }

    @FXML
    void viewStudentsOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/view_student_page.fxml");
    }

    private void clearFields() {
        txtSearchStudentId.clear();
        txtStudentId.clear();
        txtStudentName.clear();
        txtEmail.clear();
        txtNic.clear();
        txtBirthday.clear();
        txtBatch.clear();
        txtAddress.clear();
    }

    private void setupSearchSuggestions() {
        suggestionMenu.setAutoHide(true);

        txtSearchStudentId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isSelectingSuggestion) {
                return;
            }

            if (newValue == null || newValue.trim().isEmpty()) {
                suggestionMenu.hide();
                return;
            }

            ObservableList<String> suggestions = deleteStudentController.getStudentSuggestions(newValue.trim());
            if (suggestions.isEmpty()) {
                suggestionMenu.hide();
                return;
            }

            suggestionMenu.getItems().clear();
            for (String suggestion : suggestions) {
                MenuItem item = new MenuItem(suggestion);
                item.setOnAction(e -> {
                    isSelectingSuggestion = true;
                    String studentId = suggestion.contains(" - ") ? suggestion.split(" - ")[0].trim() : suggestion.trim();
                    txtSearchStudentId.setText(studentId);
                    loadStudentDetails(studentId);
                    isSelectingSuggestion = false;
                    suggestionMenu.hide();
                });
                suggestionMenu.getItems().add(item);
            }

            if (!suggestionMenu.isShowing() && txtSearchStudentId.getScene() != null && txtSearchStudentId.getScene().getWindow() != null) {
                suggestionMenu.show(txtSearchStudentId, Side.BOTTOM, 0, 0);
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSearchSuggestions();
    }
}

