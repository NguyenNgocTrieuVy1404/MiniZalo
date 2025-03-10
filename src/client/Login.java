package client;

import java.sql.SQLException;

public class Login {
    private DatabaseConnection dbConnection;

    public Login(String url, String user, String password) throws SQLException {
        this.dbConnection = new DatabaseConnection(url, user, password);
    }

    public boolean authenticate(String username, String password) throws SQLException {
        return dbConnection.authenticate(username, password);
    }

    public String getDisplayName(String username) throws SQLException {
        return dbConnection.getDisplayName(username);
    }

    public void closeConnection() throws SQLException {
        dbConnection.close();
    }
}
