package com.student.Controllers;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import org.neo4j.driver.v1.exceptions.Neo4jException;
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;

import com.student.DAOs.MySqlDAO;
import com.student.DAOs.Neo4jDAO;
import com.student.Models.Course;
import com.student.Models.Joinable;
import com.student.Models.Student;

@ManagedBean@ViewScoped
public class StudentController implements Serializable{
	
	private MySqlDAO mySqlDAO;
	private Neo4jDAO neo4jDAO;
	private ArrayList<Student> students;
	private ResultSet result;
	private List<List<Joinable>> studentDetails;
	
	//Student Controller no arguments constructor, creating Mysql DB connection
	public StudentController() {
		try {
			mySqlDAO = new MySqlDAO();
		} catch(SQLException e) {
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Error: Cannot connect to MySQL Database"));
		} catch (Exception e) {
		} finally {
			if(mySqlDAO != null)mySqlDAO.closeConnection();
		}
	}
	
	//Load students from MYSQL DB
	public void loadStudents() {
		students = new ArrayList<>();
		try {
			result = mySqlDAO.executeQuery("Select * from student");
			while(result.next() != false) {
				students.add(new Student(result.getString("sid"), result.getString("cid"), result.getString("name"), result.getString("address")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
		} finally {
			if(mySqlDAO != null)mySqlDAO.closeConnection();
		}
	}
	
	//GETTER method for students list to populate data table on page
	public ArrayList<Student> getStudents(){	
		return students;
	}
	
	//ADD student method
	//Catching exceptions to deal wit SQL ERrrors
	public void addStudent(Student s) {
		try {
			mySqlDAO.executeUpdate("insert into student values('"+s.getSid()+"','"+s.getCid()+"','"+s.getName()+"','"+s.getAddress()+"')");
			new Neo4jDAO().addStudent(s);
			redirect("list_students.xhtml");
		} catch (SQLIntegrityConstraintViolationException e) {
			String error;
			if(e.getErrorCode() == 1452) 
				error = "Error: Course "+ s.getCid() + " does not exist";
			else 
				error = e.getLocalizedMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(error));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ServiceUnavailableException e) {
			System.out.println("neo");
			String message = "Warrnig: Student " + s.getName() + " has not been added to Neo4j DB as it is offline";
			redirect("list_students.xhtml?message=" + message);
		} finally {
			if(mySqlDAO != null)mySqlDAO.closeConnection();
		}
	}
	
	//DELETE Student 
	public void deleteStudent(String name) {
		try {
			new Neo4jDAO().deleteStudent(name);
			mySqlDAO.executeUpdate("delete from student where name = '"+name+"'");
			redirect("list_students.xhtml");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch(Neo4jException e) {
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage("Error: Student " + name + " has not been deleted from any database as he/she has relationship in Neo4j"));
		}finally {
			if(mySqlDAO != null)mySqlDAO.closeConnection();
		}
	}
	
	//LOAD student details for Student Details page
	//Sending join query to MYSQL DB and saving results to studentDetails list
	public void loadStudentDetails() {
		List<Joinable> details = new ArrayList<>();
		studentDetails = new ArrayList<>();
		String name = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("name");
		try {
			result = mySqlDAO.executeQuery("select * from student s join course c on s.cid = c.cid where s.name = '"+name+"'");
			while(result.next() != false) {
				details.add(new Student(result.getString("sid"), result.getString("cid"), result.getString("name"), result.getString("address")));
				details.add(new Course(result.getString("cid"), result.getString("cname"), result.getInt("duration")));
			}
			studentDetails.add(details);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(mySqlDAO != null)mySqlDAO.closeConnection();
		}	
	}
	
	//GETTER for studentDetails list to use in Course Student Details page
	public List<List<Joinable>> getStudentDetails(){
		return studentDetails;
	}
	
	//Method to redirect to pages using FacesContext and getting current instance of page
	private void redirect(String page) {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(page);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
