/*Student bean to be used by JSF page
 *Implements Joinable interface for 
 *using in SQL Join queries
 */


package com.student.Models;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean@RequestScoped

public class Student implements Joinable{
	
	private String sid;
	private String cid;
	private String name;
	private String address;
	
	public Student() {		
	}
	
	public Student(String sid, String cid, String name, String address) {		
		this.sid = sid;
		this.cid = cid;
		this.name = name;
		this.address = address;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
