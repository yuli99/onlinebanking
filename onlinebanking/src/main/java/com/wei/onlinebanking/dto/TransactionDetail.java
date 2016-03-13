package com.wei.onlinebanking.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import com.wei.onlinebanking.domain.BankAccount;

public class TransactionDetail implements Serializable {
	private static final long serialVersionUID = 1187551678020372276L;

	private Long id;
	private BankAccount accountFrom;
	private String idTo;
	private BigDecimal amount;
	private String securityPin;
	private String type;
	private Timestamp tranTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BankAccount getAccountFrom() {
		return accountFrom;
	}

	public void setAccountFrom(BankAccount accountFrom) {
		this.accountFrom = accountFrom;
	}

	public String getIdTo() {
		return idTo;
	}

	public void setIdTo(String idTo) {
		this.idTo = idTo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getSecurityPin() {
		return securityPin;
	}

	public void setSecurityPin(String securityPin) {
		this.securityPin = securityPin;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Timestamp getTranTime() {
		return tranTime;
	}

	public void setTranTime(Timestamp tranTime) {
		this.tranTime = tranTime;
	}

}
