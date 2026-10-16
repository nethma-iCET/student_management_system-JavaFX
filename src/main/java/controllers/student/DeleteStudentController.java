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

public class DeleteStudentController {

    Connection connection = DBConnection.getInstance().getConnection();

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

    public boolean deleteStudent(String studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, studentId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
