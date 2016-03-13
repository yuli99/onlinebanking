package com.wei.onlinebanking.dao;

import java.util.List;

import com.wei.onlinebanking.commons.dao.GenericDao;
import com.wei.onlinebanking.domain.BankAccount;
import com.wei.onlinebanking.domain.TransactionRec;

public interface TransactionDao extends GenericDao<TransactionRec, Long> {

	// load transaction records by bank account
	List<TransactionRec> loadRecordsByAccount(BankAccount account);

}
