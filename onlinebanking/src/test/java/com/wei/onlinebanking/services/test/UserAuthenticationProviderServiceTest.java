package com.wei.onlinebanking.services.test;

import static org.mockito.Mockito.*;

import javax.faces.context.FacesContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.wei.onlinebanking.domain.BankUser;
import com.wei.onlinebanking.services.impl.UserAuthenticationProviderServiceImpl;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class UserAuthenticationProviderServiceTest extends TestCase {

	@Mock
	private FacesContext facesContext;
	@Mock
	private AuthenticationManager authenticationManager;

	private UserAuthenticationProviderServiceImpl userAuthenticationProviderServiceImpl;

	@Before
	public void init() {

		userAuthenticationProviderServiceImpl = new UserAuthenticationProviderServiceImpl() {
			@Override
			protected FacesContext getFacesContext() {
				return facesContext;
			}
		};

		userAuthenticationProviderServiceImpl
				.setAuthenticationManager(authenticationManager);
	}

	@After
	public void cleanUp() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void testProcessUserAuthenticationSuccess() {
		BankUser user = new BankUser();
		user.setUserName("Ann");
		user.setPassword("123");
		Authentication auth = new TestingAuthenticationToken(
				user.getUserName(), user.getPassword());
		auth.setAuthenticated(true);
		SecurityContext securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(auth);
		SecurityContextHolder.setContext(securityContext);

		when(authenticationManager.authenticate(any(Authentication.class)))
				.thenReturn(auth);

		Boolean isAuthenticationSuccess = userAuthenticationProviderServiceImpl
				.processUserAuthentication(user);
		verify(authenticationManager, times(1)).authenticate(
				any(Authentication.class));
		assertTrue(isAuthenticationSuccess);
	}

	@Test
	public void testProcessUserAuthenticationFailure() {
		BankUser user = new BankUser();
		user.setUserName("Ann");
		user.setPassword("123");

		when(authenticationManager.authenticate(any(Authentication.class)))
				.thenThrow(new BadCredentialsException("badCredentials"));

		Boolean isAuthenticationSuccess = userAuthenticationProviderServiceImpl
				.processUserAuthentication(user);
		verify(authenticationManager, times(1)).authenticate(
				any(Authentication.class));
		assertFalse(isAuthenticationSuccess);
	}

}
