package com.wei.onlinebanking.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.wei.onlinebanking.commons.dao.GenericJpaDao;
import com.wei.onlinebanking.domain.BankAccount;
import com.wei.onlinebanking.domain.BankUser;

public class AccountJpaDao extends GenericJpaDao<BankAccount, Long> implements
		AccountDao {

	public AccountJpaDao() {
		super(BankAccount.class);
	}

	// check the existence of an account id
	public boolean checkExistence(Long accountId) {

		Query query = getEntityManager().createQuery(
				"select count(*) from " + getPersistentClass().getSimpleName()
						+ " a where a.id = :accountId").setParameter(
				"accountId", accountId);

		Long count = (Long) query.getSingleResult();

		return count > 0;
	}

	// load account records of a given bank user
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BankAccount> loadAccountsByUser(BankUser bankUser) {
		Assert.notNull(bankUser);

		Query query = getEntityManager().createQuery(
				"select a from " + getPersistentClass().getSimpleName()
						+ " a where a.bankUser = :user").setParameter("user",
				bankUser);

		return query.getResultList();
	}

}
