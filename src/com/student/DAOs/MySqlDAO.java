package com.student.DAOs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

//DAO to MySQL Database
public class MySqlDAO{

	DataSource mysqlDS;
	ResultSet result;
	Statement statement;
	Connection connection;
	
	//No param constructor to create connection
	public MySqlDAO() throws Exception{
		Context context = new InitialContext();
		String jndiName = "java:comp/env/jdbc/studentDB";
		mysqlDS = (DataSource) context.lookup(jndiName);
		connection = mysqlDS.getConnection();
	}
	//Method takes query as argument and executes it on DB connection
	//Returns ResultSet object for client
	public ResultSet executeQuery(String query) throws SQLException{
		connection = mysqlDS.getConnection();
		statement = connection.createStatement();
		result = statement.executeQuery(query);
		return result;
	}
	//Method takes update query as argument and executes it on DB connection
	public void executeUpdate(String update) throws SQLException {
		connection = mysqlDS.getConnection();
		statement = connection.createStatement();
		statement.executeUpdate(update);
	}
	//Method to close ResultSet, Statement and Connection
	//to prevent any problems
	public void closeConnection(){
		try {
			if(result != null)result.close();
			if(statement != null)statement.close();
			if(connection != null)connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

