package com.edms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Group {

	@Id
	@Column(nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int sn;
	
	@Column(nullable=false)
	private String groupName;
	
	@Column
	private String[] users;
	
	@Column
	private int no_of_users;

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String[] getUsers() {
		return users;
	}

	public void setUsers(String[] users) {
		this.users = users;
	}

	public int getNo_of_users() {
		return no_of_users;
	}

	public void setNo_of_users(int no_of_users) {
		this.no_of_users = no_of_users;
	}
	
	
	
	
}
