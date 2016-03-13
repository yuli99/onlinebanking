package com.wei.onlinebanking.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.wei.onlinebanking.commons.domain.BaseEntity;

@Entity
@Table(name = "account")
@AttributeOverride(name = "id", column = @Column(name = "accountId"))
public class BankAccount extends BaseEntity {
	private static final long serialVersionUID = -3314103333353564794L;

	@NotNull(message = "{accountType.notnull}")
	private String accountType;

	private BigDecimal accountBalance;
	private String accountPin;
	private Timestamp DateOfCreated;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "userId", nullable = false)
	private BankUser bankUser;

	@OneToMany(mappedBy = "accountFrom")
	private List<TransactionRec> tranHistoryPart1 = new ArrayList<>();

	@OneToMany(mappedBy = "accountTo")
	private List<TransactionRec> tranHistoryPart2 = new ArrayList<>();

	// getters and setters
	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public BigDecimal getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(BigDecimal accountBalance) {
		this.accountBalance = accountBalance;
	}

	public String getAccountPin() {
		return accountPin;
	}

	public void setAccountPin(String accountPin) {
		this.accountPin = accountPin;
	}

	public Timestamp getDateOfCreated() {
		return DateOfCreated;
	}

	public void setDateOfCreated(Timestamp dateOfCreated) {
		DateOfCreated = dateOfCreated;
	}

	public BankUser getBankUser() {
		return bankUser;
	}

	public void setBankUser(BankUser bankUser) {
		this.bankUser = bankUser;
	}

	public List<TransactionRec> getTranHistoryPart1() {
		return tranHistoryPart1;
	}

	public void setTranHistoryPart1(List<TransactionRec> tranHistoryPart1) {
		this.tranHistoryPart1 = tranHistoryPart1;
	}

	public List<TransactionRec> getTranHistoryPart2() {
		return tranHistoryPart2;
	}

	public void setTranHistoryPart2(List<TransactionRec> tranHistoryPart2) {
		this.tranHistoryPart2 = tranHistoryPart2;
	}

}
