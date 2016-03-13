package com.wei.onlinebanking.services.impl;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.wei.onlinebanking.domain.BankUser;
import com.wei.onlinebanking.services.UserAuthenticationProviderService;

public class UserAuthenticationProviderServiceImpl implements
		UserAuthenticationProviderService {

	private AuthenticationManager authenticationManager;

	// Process bank user authentication
	public boolean processUserAuthentication(BankUser bankUser) {

		try {
			Authentication request = new UsernamePasswordAuthenticationToken(
					bankUser.getUserName(), bankUser.getPassword());
			Authentication result = authenticationManager.authenticate(request);
			SecurityContextHolder.getContext().setAuthentication(result);
			return true;
		} catch (AuthenticationException exc) {
			FacesMessage message = constructErrorMessage(exc.getMessage(), "Sorry!"); 
			getFacesContext().addMessage(null, message);

			return false;
		}

	}

	// getter and setter
	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(
			AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	// message constructor
	protected FacesMessage constructErrorMessage(String message, String detail) {
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, message, detail);
	}

	// wrap static method call
	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	
}
