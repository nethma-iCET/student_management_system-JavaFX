package controllers.student;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import scene.SceneManager;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ViewStudentPageController implements Initializable {

    ViewStudentController viewStudentController = new ViewStudentController();

    @FXML
    private ComboBox<String> cmbFilterBatch;

    @FXML
    private ComboBox<String> cmbFilterMode;

    @FXML
    private TableColumn<Map<String, String>, String> colAddress;

    @FXML
    private TableColumn<Map<String, String>, String> colBatch;

    @FXML
    private TableColumn<Map<String, String>, String> colBirthday;

    @FXML
    private TableColumn<Map<String, String>, String> colEmail;

    @FXML
    private TableColumn<Map<String, String>, String> colLectureMode;

    @FXML
    private TableColumn<Map<String, String>, String> colNic;

    @FXML
    private TableColumn<Map<String, String>, String> colStudentId;

    @FXML
    private TableColumn<Map<String, String>, String> colStudentName;

    @FXML
    private Label lblStudentCount;

    @FXML
    private TableView<Map<String, String>> tblStudents;

    @FXML
    private TextField txtSearch;

    private ObservableList<Map<String, String>> masterStudentList = FXCollections.observableArrayList();
    private FilteredList<Map<String, String>> filteredStudentList;

    @FXML
    void addMarksOnAction(ActionEvent event) {

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

    }

    @FXML
    void refreshTableOnAction(ActionEvent event) {
        loadData();
    }

    @FXML
    void resetFilterOnAction(ActionEvent event) {
        txtSearch.clear();
        cmbFilterBatch.setValue("All Batches");
        cmbFilterMode.setValue("All Modes");
    }

    @FXML
    void updateStudentOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/update_student_page.fxml");
    }

    @FXML
    void viewStudentsOnAction(ActionEvent event) {
        SceneManager.switchPage("/view/view_student_page.fxml");
    }

    private void initializeColumns() {
        colStudentId.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("student_id", "")));
        colStudentName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("student_name", "")));
        colNic.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("nic", "")));
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("email", "")));
        colBirthday.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("birthday", "")));
        colBatch.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("batch", "")));
        colLectureMode.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("lecture_mode", "")));
        colAddress.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOrDefault("address", "")));
    }

    private void loadFilterOptions() {
        // Load batches filter
        ObservableList<String> batches = FXCollections.observableArrayList();
        batches.add("All Batches");
        batches.addAll(viewStudentController.getBatches());
        cmbFilterBatch.setItems(batches);
        cmbFilterBatch.setValue("All Batches");

        // Load lecture modes filter
        ObservableList<String> modes = FXCollections.observableArrayList();
        modes.add("All Modes");
        modes.addAll(viewStudentController.getLectureModes());
        cmbFilterMode.setItems(modes);
        cmbFilterMode.setValue("All Modes");
    }

    private void setupFiltering() {
        filteredStudentList = new FilteredList<>(masterStudentList, p -> true);

        Runnable applyFilter = () -> {
            String searchText = txtSearch.getText();
            String selectedBatch = cmbFilterBatch.getValue();
            String selectedMode = cmbFilterMode.getValue();

            filteredStudentList.setPredicate(student -> {
                // 1. Text filter (ID, Name, NIC, Email)
                if (searchText != null && !searchText.trim().isEmpty()) {
                    String lower = searchText.trim().toLowerCase();
                    String id = student.getOrDefault("student_id", "").toLowerCase();
                    String name = student.getOrDefault("student_name", "").toLowerCase();
                    String nic = student.getOrDefault("nic", "").toLowerCase();
                    String email = student.getOrDefault("email", "").toLowerCase();

                    if (!id.contains(lower) && !name.contains(lower) && !nic.contains(lower) && !email.contains(lower)) {
                        return false;
                    }
                }

                // 2. Batch filter
                if (selectedBatch != null && !selectedBatch.equals("All Batches") && !selectedBatch.trim().isEmpty()) {
                    String batch = student.getOrDefault("batch", "");
                    if (!batch.equalsIgnoreCase(selectedBatch.trim())) {
                        return false;
                    }
                }

                // 3. Lecture Mode filter
                if (selectedMode != null && !selectedMode.equals("All Modes") && !selectedMode.trim().isEmpty()) {
                    String modeCode = selectedMode.contains("-") ? selectedMode.split("-")[0].trim() : selectedMode.trim();
                    String studentMode = student.getOrDefault("lecture_mode", "").trim();
                    if (!studentMode.equalsIgnoreCase(modeCode)) {
                        return false;
                    }
                }

                return true;
            });

            updateCountLabel();
        };

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        cmbFilterBatch.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());
        cmbFilterMode.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter.run());

        tblStudents.setItems(filteredStudentList);
    }

    private void loadData() {
        ObservableList<Map<String, String>> studentsFromDb = viewStudentController.getAllStudents();
        masterStudentList.setAll(studentsFromDb);
        updateCountLabel();
    }

    private void updateCountLabel() {
        int visibleCount = filteredStudentList != null ? filteredStudentList.size() : masterStudentList.size();
        int totalCount = masterStudentList.size();
        if (visibleCount == totalCount) {
            lblStudentCount.setText("Total: " + totalCount + " Students");
        } else {
            lblStudentCount.setText("Showing: " + visibleCount + " of " + totalCount + " Students");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeColumns();
        loadFilterOptions();
        setupFiltering();
        loadData();
    }
}
