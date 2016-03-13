package com.wei.onlinebanking.dao;

import java.util.List;

import com.wei.onlinebanking.commons.dao.GenericDao;
import com.wei.onlinebanking.domain.BankAccount;
import com.wei.onlinebanking.domain.BankUser;

public interface AccountDao extends GenericDao<BankAccount, Long> {

	// check the existence of an account id
	boolean checkExistence(Long accountId);

	// load account records of a given bank user
	List<BankAccount> loadAccountsByUser(BankUser bankUser);

}
