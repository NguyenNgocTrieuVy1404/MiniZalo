package client;

import java.sql.SQLException;

public class Register {
    private DatabaseConnection dbConnection;

    public Register(String url, String user, String password) throws SQLException {
        this.dbConnection = new DatabaseConnection(url, user, password);
    }

    public boolean registerUser(String username, String password, String displayName) throws SQLException {
        return dbConnection.registerUser(username, password, displayName);
    }

    public void closeConnection() throws SQLException {
        dbConnection.close();
    }
}
