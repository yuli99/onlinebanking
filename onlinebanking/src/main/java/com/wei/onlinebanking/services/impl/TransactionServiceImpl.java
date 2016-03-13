package com.wei.onlinebanking.services.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wei.onlinebanking.dao.AccountDao;
import com.wei.onlinebanking.dao.TransactionDao;
import com.wei.onlinebanking.domain.BankAccount;
import com.wei.onlinebanking.domain.TransactionRec;
import com.wei.onlinebanking.dto.TranRecByAccount;
import com.wei.onlinebanking.dto.TranRecSearch;
import com.wei.onlinebanking.dto.TransactionDetail;
import com.wei.onlinebanking.services.TransactionService;

public class TransactionServiceImpl implements TransactionService {

	private TransactionDao transactionDao;
	private AccountDao accountDao;
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// create withdraw record and save it to database
	public boolean createWithdrawRec(TransactionDetail tranDetail) {

		BankAccount account = tranDetail.getAccountFrom();

		if (account == null) {
			FacesMessage message = constructErrorMessage(
					String.format(getMessageBundle()
							.getString("nullAccountMsg")), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		if (!passwordEncoder.matches(tranDetail.getSecurityPin(),
				account.getAccountPin())) {
			FacesMessage message = constructErrorMessage(
					String.format(getMessageBundle().getString("wrongPinMsg")),
					null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		if (tranDetail.getAmount().compareTo(account.getAccountBalance()) > 0) {
			FacesMessage message = constructErrorMessage(
					String.format(getMessageBundle().getString(
							"exceedBalanceMsg"), account.getId().toString()), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		TransactionRec transactionRec = new TransactionRec();
		transactionRec.setAccountFrom(account);
		transactionRec.setAccountTo(null);
		transactionRec.setTransactionType("Withdraw");
		transactionRec.setTransactionAmount(tranDetail.getAmount());
		transactionRec.setTransactionTime(new Timestamp(Calendar.getInstance()
				.getTime().getTime()));
		account.setAccountBalance(account.getAccountBalance().subtract(
				tranDetail.getAmount()));

		try {
			transactionDao.save(transactionRec);
			accountDao.update(account);
		} catch (Exception exc) {
			FacesMessage message = constructFatalMessage(exc.getMessage(), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		tranDetail.setId(transactionRec.getId());
		tranDetail.setType(transactionRec.getTransactionType());
		tranDetail.setTranTime(transactionRec.getTransactionTime());
		tranDetail.setSecurityPin(null);
		return true;
	}

	// create deposit record and save it to database
	public boolean createDepositRec(TransactionDetail tranDetail) {

		BankAccount account = tranDetail.getAccountFrom();

		if (account == null) {
			FacesMessage message = constructErrorMessage(
					String.format(getMessageBundle()
							.getString("nullAccountMsg")), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		if (!passwordEncoder.matches(tranDetail.getSecurityPin(),
				account.getAccountPin())) {
			FacesMessage message = constructErrorMessage(
					String.format(getMessageBundle().getString("wrongPinMsg")),
					null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		TransactionRec transactionRec = new TransactionRec();
		transactionRec.setAccountFrom(account);
		transactionRec.setAccountTo(null);
		transactionRec.setTransactionType("Deposit");
		transactionRec.setTransactionAmount(tranDetail.getAmount());
		transactionRec.setTransactionTime(new Timestamp(Calendar.getInstance()
				.getTime().getTime()));
		account.setAccountBalance(account.getAccountBalance().add(
				tranDetail.getAmount()));

		try {
			transactionDao.save(transactionRec);
			accountDao.update(account);
		} catch (Exception exc) {
			FacesMessage message = constructFatalMessage(exc.getMessage(), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		tranDetail.setId(transactionRec.getId());
		tranDetail.setType(transactionRec.getTransactionType());
		tranDetail.setTranTime(transactionRec.getTransactionTime());
		tranDetail.setSecurityPin(null);
		return true;
	}

	// create transfer record and save it to database
	public boolean createTransferRec(TransactionDetail tranDetail) {

		BankAccount accountFrom = tranDetail.getAccountFrom();
		Long idTo = Long.valueOf(tranDetail.getIdTo());

		if (accountFrom == null) {
			FacesMessage message = constructErrorMessage(
					String.format(getMessageBundle()
							.getString("nullAccountMsg")), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		if (!accountDao.checkExistence(idTo)) {
			FacesMessage message = constructErrorMessage(String.format(
					getMessageBundle().getString("accountInvalidMsg"), idTo),
					null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		if (idTo.equals(accountFrom.getId())) {
			FacesMessage message = constructErrorMessage(
					String.format(getMessageBundle()
							.getString("sameAccountMsg")), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		if (!passwordEncoder.matches(tranDetail.getSecurityPin(),
				accountFrom.getAccountPin())) {
			FacesMessage message = constructErrorMessage(
					String.format(getMessageBundle().getString("wrongPinMsg")),
					null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		if (tranDetail.getAmount().compareTo(accountFrom.getAccountBalance()) > 0) {
			FacesMessage message = constructErrorMessage(
					String.format(getMessageBundle().getString(
							"exceedBalanceMsg"), accountFrom.getId().toString()), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		BankAccount accountTo = accountDao.findById(idTo);
		TransactionRec transactionRec = new TransactionRec();
		transactionRec.setAccountFrom(accountFrom);
		transactionRec.setAccountTo(accountTo);
		transactionRec.setTransactionType("Transfer");
		transactionRec.setTransactionAmount(tranDetail.getAmount());
		transactionRec.setTransactionTime(new Timestamp(Calendar.getInstance()
				.getTime().getTime()));
		accountFrom.setAccountBalance(accountFrom.getAccountBalance().subtract(
				tranDetail.getAmount()));
		accountTo.setAccountBalance(accountTo.getAccountBalance().add(
				tranDetail.getAmount()));

		try {
			transactionDao.save(transactionRec);
			accountDao.update(accountFrom);
			accountDao.update(accountTo);
		} catch (Exception exc) {
			FacesMessage message = constructFatalMessage(exc.getMessage(), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		tranDetail.setId(transactionRec.getId());
		tranDetail.setType(transactionRec.getTransactionType());
		tranDetail.setTranTime(transactionRec.getTransactionTime());
		tranDetail.setSecurityPin(null);
		return true;
	}

	// load transaction record by id
	public TransactionRec loadRecordById(Long tranId) {
		return transactionDao.findById(tranId);
	}

	// load transaction records by account and given time range
	public List<TranRecByAccount> loadRecordsByAccountAndTime(
			TranRecSearch tranRecSearch) {

		// use endDatePlusOne for comparison to include the records on the endDate
		Calendar c = Calendar.getInstance();
		c.setTime(tranRecSearch.getEndDate());
		c.add(Calendar.DATE, 1);
		Date endDatePlusOne = c.getTime();

		if (tranRecSearch.getStartDate().after(tranRecSearch.getEndDate())
				|| tranRecSearch.getAccount().getDateOfCreated()
						.after(endDatePlusOne)) {
			return null;
		}

		List<TransactionRec> records = transactionDao
				.loadRecordsByAccount(tranRecSearch.getAccount());
		List<TranRecByAccount> recordsToView = new ArrayList<>();

		for (TransactionRec rec : records) {

			if (rec.getTransactionTime().before(tranRecSearch.getStartDate())
					|| rec.getTransactionTime().after(endDatePlusOne)) {
				continue;
			}

			TranRecByAccount recView = new TranRecByAccount();
			recView.setId(rec.getId());
			recView.setTranTime(rec.getTransactionTime());
			recView.setTranAmount(rec.getTransactionAmount());

			if (rec.getTransactionType().equals("Transfer")) {
				if (rec.getAccountFrom().equals(tranRecSearch.getAccount())) {
					recView.setTranNote("Send to Account ID "
							+ rec.getAccountTo().getId().toString());
				} else {
					recView.setTranNote("Receive from Account ID "
							+ rec.getAccountFrom().getId().toString());
				}
			} else {
				recView.setTranNote(rec.getTransactionType() + " Money");
			}

			recordsToView.add(recView);
		}

		return recordsToView;
	}
	
	// clean data model of money transaction
	public void cleanDataModel(TransactionDetail tranDetail) {
		if (tranDetail != null) {
			tranDetail.setAccountFrom(null);
			tranDetail.setIdTo(null);
			tranDetail.setAmount(null);
			tranDetail.setSecurityPin(null);
		}
	}

	// getters and setters
	public TransactionDao getTransactionDao() {
		return transactionDao;
	}

	public void setTransactionDao(TransactionDao transactionDao) {
		this.transactionDao = transactionDao;
	}

	public AccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public BCryptPasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(BCryptPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	// message constructors
	protected FacesMessage constructErrorMessage(String message, String detail) {
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, message, detail);
	}

	protected FacesMessage constructFatalMessage(String message, String detail) {
		return new FacesMessage(FacesMessage.SEVERITY_FATAL, message, detail);
	}

	// wrap static method calls
	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	protected ResourceBundle getMessageBundle() {
		return ResourceBundle.getBundle("messages");
	}

}
