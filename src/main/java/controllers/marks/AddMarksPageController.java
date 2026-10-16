package controllers.marks;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.*;
import scene.SceneManager;

import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddMarksPageController implements Initializable {

    private final AddMarksController addMarksController = new AddMarksController();

    @FXML
    private ComboBox<String> cmbFilterSubject;

    @FXML
    private ComboBox<String> cmbSubject;

    @FXML
    private TableColumn<Map<String, String>, String> colExamDate;

    @FXML
    private TableColumn<Map<String, String>, String> colGrade;

    @FXML
    private TableColumn<Map<String, String>, String> colMarkId;

    @FXML
    private TableColumn<Map<String, String>, String> colMarks;

    @FXML
    private TableColumn<Map<String, String>, String> colStudentId;

    @FXML
    private TableColumn<Map<String, String>, String> colStudentName;

    @FXML
    private TableColumn<Map<String, String>, String> colSubject;

    @FXML
    private DatePicker dateExam;

    @FXML
    private Label lblGradePreview;

    @FXML
    private Label lblMarksCount;

    @FXML
    private TableView<Map<String, String>> tblMarks;

    @FXML
    private TextField txtMarks;

    @FXML
    private TextField txtSearchMarks;

    @FXML
    private TextField txtStudentId;

    @FXML
    private TextField txtStudentName;

    private Integer selectedMarkId = null;
    private final ContextMenu suggestionMenu = new ContextMenu();
    private boolean isSelectingSuggestion = false;

    private final ObservableList<Map<String, String>> masterMarksList = FXCollections.observableArrayList();
    private FilteredList<Map<String, String>> filteredMarksList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeColumns();
        loadSubjects();
        setupStudentSearchSuggestions();
        setupMarksInputListener();
        setupFiltering();
        setupTableSelection();
        dateExam.setValue(LocalDate.now());
        loadData();
    }

    private void initializeColumns() {
        colMarkId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("mark_id", "")));
        colStudentId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("student_id", "")));
        colStudentName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("student_name", "")));
        colSubject.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("subject_name", "")));
        colMarks.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("marks", "")));
        colGrade.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("grade", "")));
        colExamDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("exam_date", "")));
    }

    private void loadSubjects() {
        ObservableList<String> subjects = addMarksController.getAllSubjects();
        cmbSubject.setItems(subjects);

        ObservableList<String> filterSubjects = FXCollections.observableArrayList();
        filterSubjects.add("All Subjects");
        filterSubjects.addAll(subjects);
        cmbFilterSubject.setItems(filterSubjects);
        cmbFilterSubject.setValue("All Subjects");
    }

    private void setupStudentSearchSuggestions() {
        suggestionMenu.setAutoHide(true);

        txtStudentId.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isSelectingSuggestion) {
                return;
            }

            if (newValue == null || newValue.trim().isEmpty()) {
                suggestionMenu.hide();
                txtStudentName.clear();
                return;
            }

            // Check if exact ID matches directly
            Map<String, String> student = addMarksController.getStudentById(newValue.trim());
            if (student != null) {
                txtStudentName.setText(student.get("student_name"));
            } else {
                txtStudentName.clear();
            }

            // Fetch auto-complete suggestions
            ObservableList<String> suggestions = addMarksController.getStudentSuggestions(newValue.trim());
            if (suggestions.isEmpty()) {
                suggestionMenu.hide();
                return;
            }

            suggestionMenu.getItems().clear();
            for (String suggestion : suggestions) {
                MenuItem item = new MenuItem(suggestion);
                item.setOnAction(e -> {
                    isSelectingSuggestion = true;
                    String sId = suggestion.contains(" - ") ? suggestion.split(" - ")[0].trim() : suggestion.trim();
                    String sName = suggestion.contains(" - ") ? suggestion.split(" - ")[1].trim() : "";
                    txtStudentId.setText(sId);
                    txtStudentName.setText(sName);
                    isSelectingSuggestion = false;
                    suggestionMenu.hide();
                });
                suggestionMenu.getItems().add(item);
            }

            if (!suggestionMenu.isShowing() && txtStudentId.getScene() != null && txtStudentId.getScene().getWindow() != null) {
                suggestionMenu.show(txtStudentId, Side.BOTTOM, 0, 0);
            }
        });
    }

    private void setupMarksInputListener() {
        txtMarks.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                lblGradePreview.setText("Grade: -");
                return;
            }
            try {
                double val = Double.parseDouble(newValue.trim());
                if (val < 0 || val > 100) {
                    lblGradePreview.setText("Invalid (0-100)");
                } else {
                    String grade = addMarksController.calculateGrade(val);
                    lblGradePreview.setText("Grade: " + grade);
                }
            } catch (NumberFormatException e) {
                lblGradePreview.setText("Invalid Score");
            }
        });
    }

    private void setupFiltering() {
        filteredMarksList = new FilteredList<>(masterMarksList, p -> true);

        Runnable applyFilter = () -> {
            String query = txtSearchMarks.getText();
            String selectedSub = cmbFilterSubject.getValue();

            filteredMarksList.setPredicate(mark -> {
                // Text search (Student ID, Student Name, Subject Name)
                if (query != null && !query.trim().isEmpty()) {
                    String lower = query.trim().toLowerCase();
                    String sId = mark.getOrDefault("student_id", "").toLowerCase();
                    String sName = mark.getOrDefault("student_name", "").toLowerCase();
                    String subName = mark.getOrDefault("subject_name", "").toLowerCase();
                    String subId = mark.getOrDefault("subject_id", "").toLowerCase();

                    if (!sId.contains(lower) && !sName.contains(lower) && !subName.contains(lower) && !subId.contains(lower)) {
                        return false;
                    }
                }

                // Subject dropdown filter
                if (selectedSub != null && !selectedSub.equals("All Subjects") && !selectedSub.trim().isEmpty()) {
                    String subCode = selectedSub.contains(" - ") ? selectedSub.split(" - ")[0].trim() : selectedSub.trim();
                    String markSubId = mark.getOrDefault("subject_id", "").trim();
                    if (!markSubId.equalsIgnoreCase(subCode)) {
                        return false;
                    }
                }

                return true;
            });

            updateCountLabel();
        };

        txtSearchMarks.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        cmbFilterSubject.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());

        tblMarks.setItems(filteredMarksList);
    }

    private void setupTableSelection() {
        tblMarks.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    selectedMarkId = Integer.parseInt(newSelection.getOrDefault("mark_id", "0"));
                } catch (NumberFormatException e) {
                    selectedMarkId = null;
                }

                isSelectingSuggestion = true;
                txtStudentId.setText(newSelection.getOrDefault("student_id", ""));
                txtStudentName.setText(newSelection.getOrDefault("student_name", ""));
                isSelectingSuggestion = false;

                String subId = newSelection.getOrDefault("subject_id", "");
                for (String item : cmbSubject.getItems()) {
                    if (item.startsWith(subId)) {
                        cmbSubject.setValue(item);
                        break;
                    }
                }

                txtMarks.setText(newSelection.getOrDefault("marks", ""));

                String examDateStr = newSelection.getOrDefault("exam_date", "");
                if (examDateStr != null && !examDateStr.equals("-") && !examDateStr.isEmpty()) {
                    try {
                        dateExam.setValue(LocalDate.parse(examDateStr));
                    } catch (Exception e) {
                        dateExam.setValue(LocalDate.now());
                    }
                }
            }
        });
    }

    private void loadData() {
        ObservableList<Map<String, String>> list = addMarksController.getAllMarks();
        masterMarksList.setAll(list);
        updateCountLabel();
    }

    private void updateCountLabel() {
        int visibleCount = filteredMarksList != null ? filteredMarksList.size() : masterMarksList.size();
        int totalCount = masterMarksList.size();
        if (visibleCount == totalCount) {
            lblMarksCount.setText("Total: " + totalCount + " Records");
        } else {
            lblMarksCount.setText("Showing: " + visibleCount + " of " + totalCount + " Records");
        }
    }

    @FXML
    void saveMarkOnAction(ActionEvent event) {
        String studentId = txtStudentId.getText();
        String selectedSubject = cmbSubject.getValue();
        String marksText = txtMarks.getText();
        LocalDate examDate = dateExam.getValue();

        if (studentId == null || studentId.trim().isEmpty() ||
            selectedSubject == null || selectedSubject.trim().isEmpty() ||
            marksText == null || marksText.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in Student ID, select Subject, and enter Marks!").show();
            return;
        }

        // Verify student exists
        Map<String, String> student = addMarksController.getStudentById(studentId.trim());
        if (student == null) {
            new Alert(Alert.AlertType.WARNING, "Student ID (" + studentId.trim() + ") was not found in the database!").show();
            return;
        }

        double marksVal;
        try {
            marksVal = Double.parseDouble(marksText.trim());
            if (marksVal < 0 || marksVal > 100) {
                new Alert(Alert.AlertType.WARNING, "Marks must be between 0 and 100!").show();
                return;
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Invalid marks entered. Please enter a valid decimal number!").show();
            return;
        }

        String subjectId = selectedSubject.contains(" - ") ? selectedSubject.split(" - ")[0].trim() : selectedSubject.trim();
        String examDateStr = examDate != null ? examDate.toString() : null;

        try {
            boolean isAdded = addMarksController.addMark(studentId.trim(), subjectId, marksVal, examDateStr);
            if (isAdded) {
                new Alert(Alert.AlertType.INFORMATION, "Marks recorded successfully! (Grade: " + addMarksController.calculateGrade(marksVal) + ")").show();
                clearFields();
                loadData();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to record marks!").show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    @FXML
    void updateMarkOnAction(ActionEvent event) {
        if (selectedMarkId == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a mark record from the table to update!").show();
            return;
        }

        String studentId = txtStudentId.getText();
        String selectedSubject = cmbSubject.getValue();
        String marksText = txtMarks.getText();
        LocalDate examDate = dateExam.getValue();

        if (studentId == null || studentId.trim().isEmpty() ||
            selectedSubject == null || selectedSubject.trim().isEmpty() ||
            marksText == null || marksText.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all required fields!").show();
            return;
        }

        double marksVal;
        try {
            marksVal = Double.parseDouble(marksText.trim());
            if (marksVal < 0 || marksVal > 100) {
                new Alert(Alert.AlertType.WARNING, "Marks must be between 0 and 100!").show();
                return;
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.WARNING, "Invalid marks entered!").show();
            return;
        }

        String subjectId = selectedSubject.contains(" - ") ? selectedSubject.split(" - ")[0].trim() : selectedSubject.trim();
        String examDateStr = examDate != null ? examDate.toString() : null;

        try {
            boolean isUpdated = addMarksController.updateMark(selectedMarkId, studentId.trim(), subjectId, marksVal, examDateStr);
            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "Marks updated successfully!").show();
                clearFields();
                loadData();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update marks! Record not found.").show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error updating marks: " + e.getMessage()).show();
        }
    }

    @FXML
    void deleteMarkOnAction(ActionEvent event) {
        if (selectedMarkId == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a mark record from the table to delete!").show();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Mark Record #" + selectedMarkId + "?");
        confirmation.setContentText("Are you sure you want to permanently delete this score record?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean isDeleted = addMarksController.deleteMark(selectedMarkId);
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Mark record deleted successfully!").show();
                    clearFields();
                    loadData();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete record!").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error deleting mark: " + e.getMessage()).show();
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
        selectedMarkId = null;
        txtStudentId.clear();
        txtStudentName.clear();
        cmbSubject.setValue(null);
        txtMarks.clear();
        dateExam.setValue(LocalDate.now());
        lblGradePreview.setText("Grade: -");
        tblMarks.getSelectionModel().clearSelection();
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
