package controllers.student;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddStudentController {

    Connection connection = DBConnection.getInstance().getConnection();

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

    public String generateStudentId(String lectureMode, String batch) {

        String mode = lectureMode.contains("-") ? lectureMode.split("-")[0].trim() : lectureMode.trim();

        String year = String.valueOf(LocalDate.now().getYear()).substring(2);

        String batchNo = batch.replaceAll("[^0-9]", "");
        if (batchNo.isEmpty()) {
            batchNo = batch;
        }

        String prefix = mode + year + batchNo;

        String sql = "SELECT student_id FROM students WHERE student_id LIKE ? ORDER BY student_id DESC LIMIT 1";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, prefix + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String lastStudentId = resultSet.getString("student_id");
                if (lastStudentId != null && lastStudentId.length() > prefix.length()) {
                    String lastNumberStr = lastStudentId.substring(prefix.length());
                    try {
                        int nextNumber = Integer.parseInt(lastNumberStr) + 1;
                        return prefix + String.format("%03d", nextNumber);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            return prefix + "001";

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addStudent(String studentId, String name, String nic, String email, String birthday, String batch, String lectureMode, String address) {
        String SQL = "INSERT INTO students (student_id, student_name, nic, email, birthday, batch, lecture_mode, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, studentId);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, nic);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, birthday);
            preparedStatement.setString(6, batch);
            preparedStatement.setString(7, lectureMode);
            preparedStatement.setString(8, address);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
