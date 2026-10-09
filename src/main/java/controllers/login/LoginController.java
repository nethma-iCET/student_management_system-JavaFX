package controllers.login;

import db.DBConnection;

import java.sql.*;

public class LoginController {

    Connection connection = DBConnection.getConnection();

    public boolean checkUserNameandPassword(String userName, String password) {

        String SQL = "SELECT * FROM users;";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){

                String dbUsername = resultSet.getString("username");
                String dbPassword = resultSet.getString("password");

                if( userName.equals(dbUsername) && password.equals(dbPassword) ){
                    return true;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}
