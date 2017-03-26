package project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connector {
	public Connection connection = null;
	public Statement statement = null;
	
	public Connector() throws Exception {
		String userName = "5530u60";
   		String password = "jure0kku";
        String url = "jdbc:mysql://georgia.eng.utah.edu/5530db60";
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        connection = DriverManager.getConnection(url, userName, password);
        statement = connection.createStatement();
	}
	
	public void kill() throws SQLException {
		closeStatement();
		closeConnection();
	}
	
	public void closeStatement() throws SQLException {
		if (statement != null) {
			statement.close();
		}
	}
	
	public void closeConnection() throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
}
