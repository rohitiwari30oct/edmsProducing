package com.edms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Quota {

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int sn;

	@Column(nullable = false)
	private String userId;

	@Column(nullable = false)
	private double quotaUsed;
	
	@Column(nullable = false)
	private double quotaRemains;

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public double getQuotaUsed() {
		return quotaUsed;
	}

	public void setQuotaUsed(double quotaUsed) {
		this.quotaUsed = quotaUsed;
	}

	public double getQuotaRemains() {
		return quotaRemains;
	}

	public void setQuotaRemains(double quotaRemains) {
		this.quotaRemains = quotaRemains;
	}

}
