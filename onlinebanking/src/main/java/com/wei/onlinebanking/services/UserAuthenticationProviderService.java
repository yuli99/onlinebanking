package com.wei.onlinebanking.services;

import com.wei.onlinebanking.domain.BankUser;

public interface UserAuthenticationProviderService {

	// process user authentication
	boolean processUserAuthentication(BankUser bankUser);

}