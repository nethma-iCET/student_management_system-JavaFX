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

public class UpdateStudentController {

    Connection connection = DBConnection.getInstance().getConnection();

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

    public ObservableList<String> getStudentSuggestions(String query) {
        ObservableList<String> suggestions = FXCollections.observableArrayList();
        if (query == null || query.trim().isEmpty()) {
            return suggestions;
        }

        String sql = "SELECT student_id, student_name FROM students WHERE student_id LIKE ? OR student_name LIKE ? LIMIT 8";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            String pattern = "%" + query.trim() + "%";
            preparedStatement.setString(1, pattern);
            preparedStatement.setString(2, pattern);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("student_id");
                String name = resultSet.getString("student_name");
                suggestions.add(id + " - " + name);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return suggestions;
    }

    public Map<String, String> searchStudent(String query) {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }

        String trimmedQuery = query.trim();
        String sql = "SELECT * FROM students WHERE student_id = ? OR student_name = ? OR student_name LIKE ? LIMIT 1";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, trimmedQuery);
            preparedStatement.setString(2, trimmedQuery);
            preparedStatement.setString(3, "%" + trimmedQuery + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Map<String, String> studentData = new HashMap<>();
                studentData.put("student_id", resultSet.getString("student_id"));
                studentData.put("student_name", resultSet.getString("student_name"));
                studentData.put("nic", resultSet.getString("nic"));
                studentData.put("email", resultSet.getString("email"));
                studentData.put("birthday", resultSet.getString("birthday"));
                studentData.put("batch", resultSet.getString("batch"));
                studentData.put("address", resultSet.getString("address"));
                return studentData;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public boolean updateStudent(String studentId, String name, String nic, String birthday, String batch, String address) {
        String sql = "UPDATE students SET student_name = ?, nic = ?, birthday = ?, batch = ?, address = ? WHERE student_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, nic);
            preparedStatement.setString(3, birthday);
            preparedStatement.setString(4, batch);
            preparedStatement.setString(5, address);
            preparedStatement.setString(6, studentId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
