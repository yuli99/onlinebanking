package com.wei.onlinebanking.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.component.inputtext.InputText;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wei.onlinebanking.dao.UserDao;
import com.wei.onlinebanking.domain.BankUser;
import com.wei.onlinebanking.services.UserService;

public class UserServiceImpl implements UserService, UserDetailsService {

	private UserDao userDao;
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// create new bank user and save it to database
	public boolean createUser(BankUser bankUser) {

		String pass = bankUser.getPassword();

		if (!userDao.checkAvailable(bankUser.getUserName())) {
			FacesMessage message = constructErrorMessage(String.format(
					getMessageBundle().getString("userExistsMsg"),
					bankUser.getUserName()), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		bankUser.setPassword(passwordEncoder.encode(pass));

		try {
			userDao.save(bankUser);
		} catch (Exception exc) {
			FacesMessage message = constructFatalMessage(exc.getMessage(), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		// set uncoded password back for authentication use
		bankUser.setPassword(pass);
		return true;
	}

	// update user profile in database
	public boolean updateUserInfo(BankUser bankUser) {

		try {
			userDao.update(bankUser);
		} catch (Exception exc) {
			FacesMessage message = constructFatalMessage(exc.getMessage(), null);
			getFacesContext().addMessage(null, message);
			return false;
		}

		return true;
	}

	// load user record by user name
	public BankUser loadUserByUserName(String userName) {
		return	userDao.loadUserByUserName(userName);
	}

	// check user name availability for UI Ajax use
	public boolean checkAvailable(AjaxBehaviorEvent event) {

		InputText inputText = (InputText) event.getSource();
		String userName = (String) inputText.getValue();

		boolean available = userDao.checkAvailable(userName);

		if (!available) {
			FacesMessage message = constructErrorMessage(null, String.format(
					getMessageBundle().getString("userExistsMsg"), userName));
			getFacesContext().addMessage(event.getComponent().getClientId(),
					message);
		} else {
			FacesMessage message = constructInfoMessage(null, String.format(
					getMessageBundle().getString("nameAvailableMsg"), userName));
			getFacesContext().addMessage(event.getComponent().getClientId(),
					message);
		}

		return available;
	}

	// construct UserDetails instance for spring security
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {

		BankUser bankUser = userDao.loadUserByUserName(userName);

		if (bankUser == null) {
			throw new UsernameNotFoundException(String.format(
					getMessageBundle().getString("badCredentials"), userName));
		}

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		User userDetails = new User(bankUser.getUserName(),
				bankUser.getPassword(), authorities);

		return userDetails;
	}

	// getters and setters
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public BCryptPasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setCrypto(BCryptPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	// message constructors
	protected FacesMessage constructErrorMessage(String message, String detail) {
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, message, detail);
	}

	protected FacesMessage constructInfoMessage(String message, String detail) {
		return new FacesMessage(FacesMessage.SEVERITY_INFO, message, detail);
	}

	protected FacesMessage constructFatalMessage(String message, String detail) {
		return new FacesMessage(FacesMessage.SEVERITY_FATAL, message, detail);
	}

	// wrap static method calls
	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	protected ResourceBundle getMessageBundle() {
		return ResourceBundle.getBundle("messages");
	}

}
