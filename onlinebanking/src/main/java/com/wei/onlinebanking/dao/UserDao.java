package com.wei.onlinebanking.dao;

import com.wei.onlinebanking.commons.dao.GenericDao;
import com.wei.onlinebanking.domain.BankUser;

public interface UserDao extends GenericDao<BankUser, Long> {

	// check the availability of user name
	boolean checkAvailable(String userName);

	// load user record by user name
	BankUser loadUserByUserName(String userName);

}
