package com.wei.onlinebanking.dao;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.util.Assert;

import com.wei.onlinebanking.commons.dao.GenericJpaDao;
import com.wei.onlinebanking.domain.BankUser;

public class UserJpaDao extends GenericJpaDao<BankUser, Long> implements
		UserDao {

	public UserJpaDao() {
		super(BankUser.class);
	}

	// check the availability of user name
	public boolean checkAvailable(String userName) {
		Assert.notNull(userName);

		Query query = getEntityManager().createQuery(
				"select count(*) from " + getPersistentClass().getSimpleName()
						+ " u where u.userName = :userName").setParameter(
				"userName", userName);

		Long count = (Long) query.getSingleResult();

		return count < 1;
	}

	// load user record by user name
	public BankUser loadUserByUserName(String userName) {
		Assert.notNull(userName);

		BankUser bankUser = null;

		Query query = getEntityManager().createQuery(
				"select u from " + getPersistentClass().getSimpleName()
						+ " u where u.userName = :userName").setParameter(
				"userName", userName);
		try {
			bankUser = (BankUser) query.getSingleResult();
		} catch (NoResultException exc) {
			exc.printStackTrace();
		}

		return bankUser;
	}

}