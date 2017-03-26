package main.java.managers;

import java.sql.*;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by StudentNathan on 2/24/2017.
 */
public final class ConnectionManager {
	private ConnectionManager() {}

	private static String Database;
	private static boolean initialized = false;
	private static Connection con;

	private static ArrayList<PreparedStatement> OpenedStatements = new ArrayList<>();

	private static Stack<Savepoint> savepoints = new Stack<>();


	public static boolean isInitialized(){
		return initialized;
	}

	public static void init(String user, String password, String server, String database) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

		if(isInitialized()){
			System.err.println("Connection Already Created\n");
		}

		Database = database;

		try{
			Class.forName ("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection (server+"/"+database, user, password);
			con.setAutoCommit(false);
		} catch(Exception e) {
			System.err.println("Unable to open mysql jdbc connection. The error is as follows,\n");
			System.err.println(e.getMessage());
			throw e;
		}

		initialized = true;
	}

	public static void closeConnection(){
		if(!isInitialized()){
			return;
		}

		try {
			for(PreparedStatement statement: OpenedStatements){
				if(statement.isClosed())
					continue;
				statement.close();
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		OpenedStatements.clear();

		try {
			con.close();
			initialized = false;
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static PreparedStatement prepareStatement(String str) throws SQLException {
		PreparedStatement statement = con.prepareStatement(str);
		OpenedStatements.add(statement);
		return statement;
	}

	public static boolean inTransaction(){
		if(!initialized){
			return false;
		}

		try {
			if(con.getAutoCommit()){
				return false;
			}
		} catch (SQLException e) {
			return false;
		}

		if(savepoints.isEmpty()){
			return false;
		}

		return true;
	}

	public static void startTransaction() throws Exception {
		if(!initialized){
			throw new Exception("Connection Not Initialized");
		}

		if(con.getAutoCommit()){
			throw new Exception("Connection Cannot Be In Auto Commit Mode When Doing Transactions");
		}

		savepoints.push(con.setSavepoint());
	}

	public static void commit() throws Exception {
		if(!inTransaction()){
			throw new Exception("Not In A Transaction");
		}

		savepoints.pop();

		if(savepoints.isEmpty()){
			con.commit();
		}
	}

	public static void commitAll() throws Exception {
		if(!inTransaction()){
			throw new Exception("Not In A Transaction");
		}

		savepoints.empty();
		con.commit();
	}

	public static void rollback(){
		if(!inTransaction()){
			return;
		}
		try {
			con.rollback(savepoints.pop());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void rollbackAll() {
		while (!savepoints.isEmpty()){
			rollback();
		}
	}
}
