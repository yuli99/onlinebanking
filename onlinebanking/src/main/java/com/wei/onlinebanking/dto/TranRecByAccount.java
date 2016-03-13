package com.wei.onlinebanking.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class TranRecByAccount implements Serializable {
	private static final long serialVersionUID = -900343663689294713L;

	private Long id;
	private Timestamp tranTime;
	private BigDecimal tranAmount;
	private String tranNote;

	// getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getTranTime() {
		return tranTime;
	}

	public void setTranTime(Timestamp tranTime) {
		this.tranTime = tranTime;
	}

	public BigDecimal getTranAmount() {
		return tranAmount;
	}

	public void setTranAmount(BigDecimal tranAmount) {
		this.tranAmount = tranAmount;
	}

	public String getTranNote() {
		return tranNote;
	}

	public void setTranNote(String tranNote) {
		this.tranNote = tranNote;
	}

}
