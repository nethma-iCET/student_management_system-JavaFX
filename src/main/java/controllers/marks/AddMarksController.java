package controllers.marks;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AddMarksController {

    private final Connection connection = DBConnection.getInstance().getConnection();

    public ObservableList<Map<String, String>> getAllMarks() {
        ObservableList<Map<String, String>> list = FXCollections.observableArrayList();
        String sql = "SELECT m.mark_id, m.student_id, s.student_name, m.subject_id, sub.subject_name, m.marks, m.exam_date, m.created_at " +
                "FROM marks m " +
                "LEFT JOIN students s ON m.student_id = s.student_id " +
                "LEFT JOIN subjects sub ON m.subject_id = sub.subject_id " +
                "ORDER BY m.mark_id DESC";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                row.put("mark_id", String.valueOf(rs.getInt("mark_id")));
                row.put("student_id", rs.getString("student_id"));
                row.put("student_name", rs.getString("student_name") != null ? rs.getString("student_name") : "N/A");
                row.put("subject_id", rs.getString("subject_id"));
                row.put("subject_name", rs.getString("subject_name") != null ? rs.getString("subject_name") : rs.getString("subject_id"));
                
                double markVal = rs.getDouble("marks");
                row.put("marks", String.format("%.2f", markVal));
                row.put("grade", calculateGrade(markVal));
                row.put("exam_date", rs.getString("exam_date") != null ? rs.getString("exam_date") : "-");
                row.put("created_at", rs.getString("created_at") != null ? rs.getString("created_at") : "-");

                list.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public boolean addMark(String studentId, String subjectId, double marks, String examDate) {
        String sql = "INSERT INTO marks (student_id, subject_id, marks, exam_date) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, studentId);
            preparedStatement.setString(2, subjectId);
            preparedStatement.setDouble(3, marks);
            if (examDate != null && !examDate.trim().isEmpty()) {
                preparedStatement.setString(4, examDate);
            } else {
                preparedStatement.setNull(4, java.sql.Types.DATE);
            }

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateMark(int markId, String studentId, String subjectId, double marks, String examDate) {
        String sql = "UPDATE marks SET student_id = ?, subject_id = ?, marks = ?, exam_date = ? WHERE mark_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, studentId);
            preparedStatement.setString(2, subjectId);
            preparedStatement.setDouble(3, marks);
            if (examDate != null && !examDate.trim().isEmpty()) {
                preparedStatement.setString(4, examDate);
            } else {
                preparedStatement.setNull(4, java.sql.Types.DATE);
            }
            preparedStatement.setInt(5, markId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteMark(int markId) {
        String sql = "DELETE FROM marks WHERE mark_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, markId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableList<String> getStudentSuggestions(String query) {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT student_id, student_name FROM students WHERE student_id LIKE ? OR student_name LIKE ? ORDER BY student_id ASC LIMIT 10";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + query + "%");
            preparedStatement.setString(2, "%" + query + "%");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("student_id") + " - " + rs.getString("student_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public Map<String, String> getStudentById(String studentId) {
        String sql = "SELECT student_id, student_name, batch FROM students WHERE student_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, studentId);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                Map<String, String> student = new HashMap<>();
                student.put("student_id", rs.getString("student_id"));
                student.put("student_name", rs.getString("student_name"));
                student.put("batch", rs.getString("batch"));
                return student;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public ObservableList<String> getAllSubjects() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT subject_id, subject_name FROM subjects ORDER BY subject_id ASC";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                list.add(rs.getString("subject_id") + " - " + rs.getString("subject_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public String calculateGrade(double mark) {
        if (mark >= 75.0) {
            return "A";
        } else if (mark >= 65.0) {
            return "B";
        } else if (mark >= 50.0) {
            return "C";
        } else if (mark >= 35.0) {
            return "S";
        } else {
            return "F";
        }
    }
}
