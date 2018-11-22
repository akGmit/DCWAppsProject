package com.student.DAOs;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.exceptions.Neo4jException;
import com.student.Models.Student;
import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jDAO {
	
	Driver driver;
	Session session;
	
	public Neo4jDAO(){
	}
	//Add student node 
	public void addStudent(Student s) throws Neo4jException {
		getConnection();
		
		session.writeTransaction(new TransactionWork<String>() {

			@Override
			public String execute(Transaction tx) {
				tx.run("CREATE (:STUDENT {name: {name}, address: {address}})",
					parameters("name", s.getName(), "address", s.getAddress()));
				return null;
			}
		});
		closeConnection();
	}
	//Delete student node
	public void deleteStudent(String name) throws Neo4jException{
		getConnection();
		
		session.writeTransaction(new TransactionWork<String>() {

			@Override
			public String execute(Transaction tx) {
				tx.run("MATCH(s:STUDENT) where s.name = {studentName} delete s" ,parameters("studentName", name));
				return null;
			}
		});
		closeConnection();
	}
	//Get neo4j connection
	private void getConnection() throws Neo4jException{
		driver =  GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("vvvvv", "vvvvv"));
		session = driver.session();
	}
	
	public void closeConnection() throws Neo4jException{
		session.close();
		driver.close();
	}
}
