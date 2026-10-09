package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection(){

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/student_management_system_v1",
                    "root",
                    "12345");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }
}
