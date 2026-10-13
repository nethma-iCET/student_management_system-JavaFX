package controllers.student;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddStudentController {

    Connection connection = DBConnection.getInstance().getConnection();

    public void  addStudent(){
        System.out.println(connection);

        String SQL = "INSERT INTO students(student_id, student_name, address, email, birthday, nic, batch) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, 02);
            preparedStatement.setString(2, "Kamal");
            preparedStatement.setString(3, "Panadura");
            preparedStatement.setString(4, "kamal@sample@gmail.com");
            preparedStatement.setString(5, "2021-10-12");
            preparedStatement.setString(6, "2001212123");
            preparedStatement.setString(7, "123");

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
