/*Course bean to be used by JSF page
 *Implements Joinable interface for 
 *using in SQL Join queries
 */

package com.student.Models;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean@RequestScoped

public class Course implements Joinable{
	
	private String cid;
	private String cname;
	private int duration;

	public Course() {
	}
	
	public Course(String id, String name, int duration) {
		this.cid = id;
		this.cname = name;
		this.duration = duration;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
}
