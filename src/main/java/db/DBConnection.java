package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection;
    private static DBConnection instance;

    private DBConnection(){
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/student_management_system_v1",
                    "root",
                    "12345");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static DBConnection getInstance(){
        if(instance == null){
            instance = new DBConnection();
        }

        return instance;
    }

    public Connection getConnection(){
        return connection;
    }


}
