package com.wei.onlinebanking.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.wei.onlinebanking.commons.dao.GenericJpaDao;
import com.wei.onlinebanking.domain.BankAccount;
import com.wei.onlinebanking.domain.TransactionRec;

public class TransactionJpaDao extends GenericJpaDao<TransactionRec, Long>
		implements TransactionDao {

	public TransactionJpaDao() {
		super(TransactionRec.class);
	}

	// load transaction records by bank account
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<TransactionRec> loadRecordsByAccount(BankAccount account) {

		Query query = getEntityManager()
				.createQuery(
						"select t from "
								+ getPersistentClass().getSimpleName()
								+ " t where t.accountFrom = :account or t.accountTo = :account")
				.setParameter("account", account);

		return query.getResultList();
	}

}
