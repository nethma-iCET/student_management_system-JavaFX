package controllers.subject;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import scene.SceneManager;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageSubjectPageController implements Initializable {

    private final ManageSubjectController manageSubjectController = new ManageSubjectController();

    @FXML
    private ComboBox<Integer> cmbCreditHours;

    @FXML
    private TableColumn<Map<String, String>, String> colCreatedAt;

    @FXML
    private TableColumn<Map<String, String>, String> colCreditHours;

    @FXML
    private TableColumn<Map<String, String>, String> colSubjectId;

    @FXML
    private TableColumn<Map<String, String>, String> colSubjectName;

    @FXML
    private Label lblSubjectCount;

    @FXML
    private TableView<Map<String, String>> tblSubjects;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtSubjectId;

    @FXML
    private TextField txtSubjectName;

    private ObservableList<Map<String, String>> masterSubjectList = FXCollections.observableArrayList();
    private FilteredList<Map<String, String>> filteredSubjectList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeColumns();
        loadCreditHoursOptions();
        setupFiltering();
        setupTableSelection();
        loadData();
    }

    private void initializeColumns() {
        colSubjectId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("subject_id", "")));
        colSubjectName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("subject_name", "")));
        colCreditHours.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("credit_hours", "")));
        colCreatedAt.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("created_at", "")));
    }

    private void loadCreditHoursOptions() {
        ObservableList<Integer> credits = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6);
        cmbCreditHours.setItems(credits);
        cmbCreditHours.setValue(3);
    }

    private void setupFiltering() {
        filteredSubjectList = new FilteredList<>(masterSubjectList, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredSubjectList.setPredicate(subject -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lower = newValue.trim().toLowerCase();
                String id = subject.getOrDefault("subject_id", "").toLowerCase();
                String name = subject.getOrDefault("subject_name", "").toLowerCase();
                return id.contains(lower) || name.contains(lower);
            });
            updateCountLabel();
        });

        tblSubjects.setItems(filteredSubjectList);
    }

    private void setupTableSelection() {
        tblSubjects.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtSubjectId.setText(newSelection.getOrDefault("subject_id", ""));
                txtSubjectName.setText(newSelection.getOrDefault("subject_name", ""));
                try {
                    int credits = Integer.parseInt(newSelection.getOrDefault("credit_hours", "3"));
                    cmbCreditHours.setValue(credits);
                } catch (NumberFormatException e) {
                    cmbCreditHours.setValue(3);
                }
            }
        });
    }

    private void loadData() {
        ObservableList<Map<String, String>> list = manageSubjectController.getAllSubjects();
        masterSubjectList.setAll(list);
        updateCountLabel();
    }

    private void updateCountLabel() {
        int visibleCount = filteredSubjectList != null ? filteredSubjectList.size() : masterSubjectList.size();
        int totalCount = masterSubjectList.size();
        if (visibleCount == totalCount) {
            lblSubjectCount.setText("Total: " + totalCount + " Subjects");
        } else {
            lblSubjectCount.setText("Showing: " + visibleCount + " of " + totalCount + " Subjects");
        }
    }

    @FXML
    void generateIdOnAction(ActionEvent event) {
        String nextId = manageSubjectController.generateNextSubjectId();
        txtSubjectId.setText(nextId);
    }

    @FXML
    void saveSubjectOnAction(ActionEvent event) {
        String subjectId = txtSubjectId.getText();
        String subjectName = txtSubjectName.getText();
        Integer creditHours = cmbCreditHours.getValue();

        if (subjectId == null || subjectId.trim().isEmpty() ||
            subjectName == null || subjectName.trim().isEmpty() ||
            creditHours == null) {
            new Alert(Alert.AlertType.WARNING, "Please enter Subject ID, Name, and select Credit Hours!").show();
            return;
        }

        try {
            boolean isAdded = manageSubjectController.addSubject(subjectId.trim(), subjectName.trim(), creditHours);
            if (isAdded) {
                new Alert(Alert.AlertType.INFORMATION, "Subject added successfully!").show();
                clearFields();
                loadData();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to add subject!").show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error adding subject: " + e.getMessage()).show();
        }
    }

    @FXML
    void updateSubjectOnAction(ActionEvent event) {
        String subjectId = txtSubjectId.getText();
        String subjectName = txtSubjectName.getText();
        Integer creditHours = cmbCreditHours.getValue();

        if (subjectId == null || subjectId.trim().isEmpty() ||
            subjectName == null || subjectName.trim().isEmpty() ||
            creditHours == null) {
            new Alert(Alert.AlertType.WARNING, "Please select or specify a Subject to update!").show();
            return;
        }

        try {
            boolean isUpdated = manageSubjectController.updateSubject(subjectId.trim(), subjectName.trim(), creditHours);
            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "Subject updated successfully!").show();
                clearFields();
                loadData();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update subject! Subject ID may not exist.").show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error updating subject: " + e.getMessage()).show();
        }
    }

    @FXML
    void deleteSubjectOnAction(ActionEvent event) {
        String subjectId = txtSubjectId.getText();

        if (subjectId == null || subjectId.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select or enter the Subject ID to delete!").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Subject Deletion");
        confirmation.setHeaderText("Delete Subject (" + subjectId.trim() + ")?");
        confirmation.setContentText("Warning: Deleting this subject will also delete all associated student marks records permanently!");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = manageSubjectController.deleteSubject(subjectId.trim());
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Subject deleted successfully!").show();
                    clearFields();
                    loadData();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete subject! Subject ID not found.").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error deleting subject: " + e.getMessage()).show();
            }
        }
    }

    @FXML
    void clearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void refreshTableOnAction(ActionEvent event) {
        loadData();
    }

    private void clearFields() {
        txtSubjectId.clear();
        txtSubjectName.clear();
        cmbCreditHours.setValue(3);
        tblSubjects.getSelectionModel().clearSelection();
    }

    // Sidebar navigation
    @FXML
    void dashboardOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/main_dashboard.fxml");
    }

    @FXML
    void addStudentOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/add_student_page.fxml");
    }

    @FXML
    void updateStudentOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/update_student_page.fxml");
    }

    @FXML
    void deleteStudentOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/delete_student_page.fxml");
    }

    @FXML
    void viewStudentsOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/view_student_page.fxml");
    }

    @FXML
    void addMarksOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/add_marks_page.fxml");
    }

    @FXML
    void manageSubjectsOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/manage_subjects_page.fxml");
    }

    @FXML
    void logoutOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/login_page.fxml");
    }
}
