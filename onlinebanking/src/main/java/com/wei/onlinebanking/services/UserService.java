package com.wei.onlinebanking.services;

import javax.faces.event.AjaxBehaviorEvent;

import com.wei.onlinebanking.domain.BankUser;

public interface UserService {

	// create bank user and save it to database
	boolean createUser(BankUser bankUser);

	// update bank user profile in database
	boolean updateUserInfo(BankUser bankUser);

	// load user record by user name
	BankUser loadUserByUserName(String userName);

	// check user name availability for UI AJAX use
	boolean checkAvailable(AjaxBehaviorEvent event);

}