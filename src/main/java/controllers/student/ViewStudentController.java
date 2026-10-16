package controllers.student;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ViewStudentController {

    Connection connection = DBConnection.getInstance().getConnection();

    public ObservableList<Map<String, String>> getAllStudents() {
        ObservableList<Map<String, String>> studentList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM students ORDER BY student_id ASC";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("student_id", resultSet.getString("student_id") != null ? resultSet.getString("student_id") : "");
                row.put("student_name", resultSet.getString("student_name") != null ? resultSet.getString("student_name") : "");
                row.put("nic", resultSet.getString("nic") != null ? resultSet.getString("nic") : "");
                row.put("email", resultSet.getString("email") != null ? resultSet.getString("email") : "");
                row.put("birthday", resultSet.getString("birthday") != null ? resultSet.getString("birthday") : "");
                row.put("batch", resultSet.getString("batch") != null ? resultSet.getString("batch") : "");
                
                String mode = "";
                try {
                    mode = resultSet.getString("lecture_mode");
                } catch (SQLException ignored) {
                }
                row.put("lecture_mode", (mode != null && !mode.isEmpty()) ? mode : "-");

                row.put("address", resultSet.getString("address") != null ? resultSet.getString("address") : "");

                studentList.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return studentList;
    }

    public ObservableList<String> getBatches() {
        ObservableList<String> batchList = FXCollections.observableArrayList();
        String sql = "SELECT batch_id FROM batches ORDER BY batch_id ASC";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                batchList.add(resultSet.getString("batch_id"));
            }
        } catch (SQLException e) {
            System.err.println("Could not load batches from DB: " + e.getMessage());
        }

        if (batchList.isEmpty()) {
            batchList.addAll("110", "111", "112", "120", "121", "122", "iCM120", "iCD121", "iCD122", "iCM123", "iCD124", "iCD125");
        }
        return batchList;
    }

    public ObservableList<String> getLectureModes() {
        ObservableList<String> modeList = FXCollections.observableArrayList();
        String sql = "SELECT mode_id, mode_name FROM lecture_modes ORDER BY mode_id ASC";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String modeId = resultSet.getString("mode_id");
                String modeName = resultSet.getString("mode_name");
                modeList.add(modeId + " - " + modeName);
            }
        } catch (SQLException e) {
            System.err.println("Could not load lecture modes from DB: " + e.getMessage());
        }

        if (modeList.isEmpty()) {
            modeList.addAll("PR - Physical", "OL - Online");
        }
        return modeList;
    }
}
