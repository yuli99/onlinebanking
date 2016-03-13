package com.wei.onlinebanking.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.wei.onlinebanking.commons.domain.BaseEntity;

@Entity
@Table(name = "transactionrec")
@AttributeOverride(name = "id", column = @Column(name = "tranId"))
public class TransactionRec extends BaseEntity {
	private static final long serialVersionUID = 1151109711892109104L;

	private String transactionType;
	private BigDecimal transactionAmount;
	private Timestamp transactionTime;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "accountId_from", nullable = false)
	private BankAccount accountFrom;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "accountId_to")
	private BankAccount accountTo;

	// getters and setters
	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public Timestamp getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(Timestamp transactionTime) {
		this.transactionTime = transactionTime;
	}

	public BankAccount getAccountFrom() {
		return accountFrom;
	}

	public void setAccountFrom(BankAccount accountFrom) {
		this.accountFrom = accountFrom;
	}

	public BankAccount getAccountTo() {
		return accountTo;
	}

	public void setAccountTo(BankAccount accountTo) {
		this.accountTo = accountTo;
	}

}
