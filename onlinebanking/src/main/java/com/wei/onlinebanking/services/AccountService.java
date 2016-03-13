package com.wei.onlinebanking.services;

import java.util.List;

import javax.faces.event.AjaxBehaviorEvent;

import com.wei.onlinebanking.domain.BankAccount;
import com.wei.onlinebanking.domain.BankUser;

public interface AccountService {

	// create new bank account and save it to database
	boolean createAccount(BankAccount account, BankUser bankUser);

	// update account balance
	boolean updateAccount(BankAccount account);

	// load account by id
	BankAccount loadAccountById(Long accountId);

	// load account records from database by user
	List<BankAccount> loadAccountsByUser(BankUser bankUser);

	// clean data model of bank account
	void cleanDataModel(BankAccount account);

	// check account id existence for UI AJAX use
	boolean checkExistence(AjaxBehaviorEvent event);

}
