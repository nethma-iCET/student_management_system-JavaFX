package controllers.student;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.*;
import scene.SceneManager;

import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;

public class UpdateStudentPageController implements Initializable {

    UpdateStudentController updateStudentController = new UpdateStudentController();

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
        Map<String, String> student = updateStudentController.searchStudent(searchKey);
        if (student != null) {
            txtStudentId.setText(student.get("student_id"));
            txtStudentName.setText(student.get("student_name"));
            txtNic.setText(student.get("nic"));
            txtEmail.setText(student.get("email"));

            String bday = student.get("birthday");
            if (bday != null && !bday.isEmpty()) {
                try {
                    dateBirthday.setValue(LocalDate.parse(bday));
                } catch (Exception ignored) {
                }
            } else {
                dateBirthday.setValue(null);
            }

            cmbBatch.setValue(student.get("batch"));
            txtAddress.setText(student.get("address"));
        } else {
            new Alert(Alert.AlertType.WARNING, "No student found with ID or Name: " + searchKey).show();
        }
    }

    @FXML
    void updateStudentDetailsOnAction(ActionEvent event) {
        String studentId = txtStudentId.getText();
        String name = txtStudentName.getText();
        String nic = txtNic.getText();
        LocalDate birthdayDate = dateBirthday.getValue();
        String batch = cmbBatch.getValue();
        String address = txtAddress.getText();

        if (studentId == null || studentId.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please search and select a student first!").show();
            return;
        }

        if (name == null || name.trim().isEmpty() ||
            nic == null || nic.trim().isEmpty() ||
            birthdayDate == null ||
            batch == null || batch.trim().isEmpty() ||
            address == null || address.trim().isEmpty()) {

            new Alert(Alert.AlertType.WARNING, "Please fill in all required fields!").show();
            return;
        }

        try {
            boolean isUpdated = updateStudentController.updateStudent(
                    studentId.trim(),
                    name.trim(),
                    nic.trim(),
                    birthdayDate.toString(),
                    batch.trim(),
                    address.trim()
            );

            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "Student Details Updated Successfully!").show();
                clearFields();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update student details!").show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    @FXML
    void updateStudentOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/update_student_page.fxml");
    }

    @FXML
    void viewStudentsOnAction(ActionEvent event) {

    }

    private void clearFields() {
        txtSearchStudentId.clear();
        txtStudentId.clear();
        txtStudentName.clear();
        txtNic.clear();
        txtEmail.clear();
        dateBirthday.setValue(null);
        cmbBatch.setValue(null);
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

            ObservableList<String> suggestions = updateStudentController.getStudentSuggestions(newValue.trim());
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

    public void loadBatches() {
        ObservableList<String> batches = updateStudentController.getBatches();
        cmbBatch.setItems(batches);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBatches();
        setupSearchSuggestions();
    }
}

