package controllers.subject;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ManageSubjectController {

    Connection connection = DBConnection.getInstance().getConnection();

    public ObservableList<Map<String, String>> getAllSubjects() {
        ObservableList<Map<String, String>> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM subjects ORDER BY subject_id ASC";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("subject_id", resultSet.getString("subject_id"));
                row.put("subject_name", resultSet.getString("subject_name"));
                row.put("credit_hours", String.valueOf(resultSet.getInt("credit_hours")));
                row.put("created_at", resultSet.getString("created_at"));
                list.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public boolean addSubject(String subjectId, String subjectName, int creditHours) {
        String sql = "INSERT INTO subjects (subject_id, subject_name, credit_hours) VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, subjectId);
            preparedStatement.setString(2, subjectName);
            preparedStatement.setInt(3, creditHours);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateSubject(String subjectId, String subjectName, int creditHours) {
        String sql = "UPDATE subjects SET subject_name = ?, credit_hours = ? WHERE subject_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, subjectName);
            preparedStatement.setInt(2, creditHours);
            preparedStatement.setString(3, subjectId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteSubject(String subjectId) {
        String sql = "DELETE FROM subjects WHERE subject_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, subjectId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateNextSubjectId() {
        String sql = "SELECT subject_id FROM subjects WHERE subject_id LIKE 'SUB-%' ORDER BY subject_id DESC LIMIT 1";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String lastId = resultSet.getString("subject_id");
                if (lastId != null && lastId.startsWith("SUB-")) {
                    String numPart = lastId.replace("SUB-", "").replaceAll("[^0-9]", "");
                    if (!numPart.isEmpty()) {
                        int nextNum = Integer.parseInt(numPart) + 1;
                        return String.format("SUB-%03d", nextNum);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "SUB-001";
    }
}
