package com.wei.onlinebanking.services;

import java.util.List;

import com.wei.onlinebanking.domain.TransactionRec;
import com.wei.onlinebanking.dto.TranRecByAccount;
import com.wei.onlinebanking.dto.TranRecSearch;
import com.wei.onlinebanking.dto.TransactionDetail;

public interface TransactionService {

	// create withdraw record and save it to database
	boolean createWithdrawRec(TransactionDetail tranDetail);

	// create withdraw record and save it to database
	boolean createDepositRec(TransactionDetail tranDetail);

	// create transfer record and save it to database
	boolean createTransferRec(TransactionDetail tranDetail);

	// load transaction record by id
	TransactionRec loadRecordById(Long tranId);

	// load transaction records by account and given time range
	List<TranRecByAccount> loadRecordsByAccountAndTime(
			TranRecSearch tranRecSearch);

	// clean data model of money transaction
	void cleanDataModel(TransactionDetail tranDetail);
}