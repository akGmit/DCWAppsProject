package com.student.Controllers;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import com.student.DAOs.MySqlDAO;
import com.student.Models.Course;
import com.student.Models.Joinable;
import com.student.Models.Student;

@ManagedBean@ViewScoped
public class CourseController implements Serializable{

	private ResultSet result;
	private MySqlDAO mySqlDAO;
	private ArrayList<Course> courses;
	private List<Joinable> detailsOfCourse;
	private List<List<Joinable>> courseDetails;

	//Course Controller no args constructor for MYSQL DB connection
	public CourseController(){
		try {
			mySqlDAO = new MySqlDAO();		
		} catch(SQLException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error: Cannot connect to MySQL Database"));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(mySqlDAO != null)mySqlDAO.closeConnection();
		}
	}
	//Load courses from DB and add to courses list
	public void loadCourses() {
		courses = new ArrayList<>();
		try {
			result = mySqlDAO.executeQuery("Select * from course");
			while(result.next() != false) {
				courses.add(new Course(result.getString("cID"), result.getString("cName"), result.getInt("duration")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
		} finally {
			if(mySqlDAO != null)mySqlDAO.closeConnection();
		}
	}
	//GETTER for courses list
	public ArrayList<Course> getCourses(){
		return courses;
	}
	//ADD Course method to database and refresh page after
	//Catching exceptions to deal with SQL errors
	public void addCourse(Course c){
		try {
			mySqlDAO.executeUpdate("insert into course values('"+c.getCid()+"','"+c.getCname()+"',"+c.getDuration()+")");
			redirect("list_courses.xhtml");
		} catch (SQLIntegrityConstraintViolationException e) {
			String error;
			if(e.getErrorCode() == 1062) 
				error = "Error: Course "+ c.getCid() + " already exists";
			else 
				error = e.getLocalizedMessage();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(error));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(mySqlDAO != null)mySqlDAO.closeConnection();
		}
	}

	//Load course details for course student details page
	//Sending JOIN query to database and dealing with results
	public void loadCourseDetails(){
		String cid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cid");
		detailsOfCourse = new ArrayList<>();
		courseDetails = new ArrayList<>();
		try {
			result = mySqlDAO.executeQuery("select * from course c join student s on c.cid = s.cid where c.cid = '" + cid + "'");
			while(result.next() != false) {
				detailsOfCourse.add(new Student(result.getString("sid"), result.getString("cid"), result.getString("name"), result.getString("address")));
				detailsOfCourse.add(new Course(result.getString("cid"), result.getString("cname"), result.getInt("duration")));
				courseDetails.add(detailsOfCourse);
				detailsOfCourse = new ArrayList<>();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(mySqlDAO != null)mySqlDAO.closeConnection();
		}
	}

	//GETTTER for cousreDetails list
	public List<List<Joinable>> getCourseDetails() {		
		return courseDetails;
	}
	
	//Delete course from MMYSQL DB
	//Catching errors 
	public void deleteCourse(String c) {
		try {	
			mySqlDAO.executeUpdate("DELETE from course where cid = '"+ c +"'");
			redirect("list_courses.xhtml");
		} catch (SQLIntegrityConstraintViolationException e) {
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage("Error: Cannot delete course "+ c+" as there are associated students"));
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(mySqlDAO != null)mySqlDAO.closeConnection();
		}
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


