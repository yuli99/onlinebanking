package com.wei.onlinebanking.services.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.component.inputtext.InputText;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wei.onlinebanking.dao.AccountDao;
import com.wei.onlinebanking.domain.BankAccount;
import com.wei.onlinebanking.domain.BankUser;
import com.wei.onlinebanking.services.AccountService;

public class AccountServiceImpl implements AccountService {

	private AccountDao accountDao;
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// create new bank account and save it to database
	public boolean createAccount(BankAccount account, BankUser bankUser) {

		account.setBankUser(bankUser);
		account.setAccountPin(passwordEncoder.encode(account.getAccountPin()));
		account.setAccountBalance(BigDecimal.ZERO);
		account.setDateOfCreated(new Timestamp(Calendar.getInstance().getTime()
				.getTime()));

		try {
			accountDao.save(account);
		} catch (Exception exc) {
			FacesMessage message = constructFatalMessage(exc.getMessage(), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		return true;
	}

	// update account balance
	public boolean updateAccount(BankAccount account) {

		try {
			accountDao.update(account);
		} catch (Exception exc) {
			FacesMessage message = constructFatalMessage(exc.getMessage(), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		return true;
	}

	// load account by id
	public BankAccount loadAccountById(Long accountId) {
		return accountDao.findById(accountId);
	}

	// load account records from database by user
	public List<BankAccount> loadAccountsByUser(BankUser bankUser) {
		return accountDao.loadAccountsByUser(bankUser);
	}

	// clean data model of bank account
	public void cleanDataModel(BankAccount account) {
		if (account != null) {
			account.setId(null);
			account.setAccountType(null);
			account.setAccountPin(null);
		}
	}

	// check account id existence for UI Ajax use
	public boolean checkExistence(AjaxBehaviorEvent event) {

		InputText inputText = (InputText) event.getSource();
		Long id = Long.valueOf((String) inputText.getValue());

		boolean exist = accountDao.checkExistence(id);

		if (!exist) {
			FacesMessage message = constructErrorMessage(null, String.format(
					getMessageBundle().getString("accountInvalidMsg"), id));
			getFacesContext().addMessage(event.getComponent().getClientId(),
					message);
		} else {
			FacesMessage message = constructInfoMessage(null, String.format(
					getMessageBundle().getString("accountExistsMsg"), id));
			getFacesContext().addMessage(event.getComponent().getClientId(),
					message);
		}

		return exist;
	}

	// getters and setters
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

	protected FacesMessage constructInfoMessage(String message, String detail) {
		return new FacesMessage(FacesMessage.SEVERITY_INFO, message, detail);
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
