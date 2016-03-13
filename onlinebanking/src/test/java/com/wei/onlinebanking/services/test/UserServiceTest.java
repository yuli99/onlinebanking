package com.wei.onlinebanking.services.test;

import static org.mockito.Mockito.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.context.FacesContext;
import javax.persistence.PersistenceException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wei.onlinebanking.dao.UserDao;
import com.wei.onlinebanking.domain.BankUser;
import com.wei.onlinebanking.services.impl.UserServiceImpl;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest extends TestCase {

	@Mock
	private FacesContext facesContext;
	@Mock
	private UserDao userDao;
	private UserServiceImpl userServiceImpl;

	private BCryptPasswordEncoder passwordEncoder;

	private static final String USERNAME = "Ann";

	@Before
	public void init() {
		passwordEncoder = new BCryptPasswordEncoder();

		userServiceImpl = new UserServiceImpl() {
			@Override
			protected FacesContext getFacesContext() {
				return facesContext;
			}
		};

		userServiceImpl.setUserDao(userDao);
	}

	@Test
	public void testCreateUserSuccess() {
		BankUser newUser = constructNewUser(USERNAME);

		when(userDao.checkAvailable(newUser.getUserName())).thenReturn(true);
		when(userDao.save(newUser)).thenAnswer(new Answer<BankUser>() {
			@Override
			public BankUser answer(InvocationOnMock invocation)
					throws Throwable {
				BankUser user = (BankUser) invocation.getArguments()[0];
				user.setId(1L);
				return user;
			}
		});

		assertNull(newUser.getId());

		Boolean isCreateUserSuccess = userServiceImpl.createUser(newUser);
		verify(userDao, times(1)).checkAvailable(newUser.getUserName());
		verify(userDao, times(1)).save(newUser);
		assertTrue(isCreateUserSuccess);
		assertNotNull(newUser.getId());
		assertEquals("123", newUser.getPassword());
	}

	@Test
	public void testCreateUserInvalidUserName() {
		BankUser newUser = constructNewUser(USERNAME);

		when(userDao.checkAvailable(newUser.getUserName())).thenReturn(false);

		Boolean isCreateUserSuccess = userServiceImpl.createUser(newUser);
		verify(userDao, times(1)).checkAvailable(newUser.getUserName());
		assertFalse(isCreateUserSuccess);
		assertNull(newUser.getId());
		assertEquals("123", newUser.getPassword());
	}

	@Test
	public void testCreateUserFailToSave() {
		BankUser newUser = constructNewUser(USERNAME);

		when(userDao.checkAvailable(newUser.getUserName())).thenReturn(true);
		when(userDao.save(newUser)).thenThrow(new PersistenceException());

		assertEquals("123", newUser.getPassword());

		Boolean isCreateUserSuccess = userServiceImpl.createUser(newUser);
		verify(userDao, times(1)).checkAvailable(newUser.getUserName());
		verify(userDao, times(1)).save(newUser);
		assertFalse(isCreateUserSuccess);
		assertNull(newUser.getId());
		assertTrue(passwordEncoder.matches("123", newUser.getPassword()));
	}

	@Test
	public void testUpdateUserInfoSuccess() {
		BankUser savedUser = constructSavedUser(USERNAME);

		when(userDao.update(savedUser)).thenReturn(savedUser);

		Boolean isUpdateUserInfoSuccess = userServiceImpl
				.updateUserInfo(savedUser);
		verify(userDao, times(1)).update(savedUser);
		assertTrue(isUpdateUserInfoSuccess);
	}

	@Test
	public void testUpdateUserInfoFailToUpdate() {
		BankUser savedUser = constructSavedUser(USERNAME);

		when(userDao.update(savedUser)).thenThrow(new PersistenceException());

		Boolean isUpdateUserInfoSuccess = userServiceImpl
				.updateUserInfo(savedUser);
		verify(userDao, times(1)).update(savedUser);
		assertFalse(isUpdateUserInfoSuccess);
	}

	@Test
	public void testLoadUserByUserName() {
		BankUser savedUser = constructSavedUser(USERNAME);

		when(userDao.loadUserByUserName(USERNAME)).thenReturn(savedUser);

		BankUser user = userServiceImpl.loadUserByUserName(USERNAME);
		verify(userDao, times(1)).loadUserByUserName(USERNAME);
		assertEquals(savedUser, user);
	}

	// construct saved bank user with not null user Id
	private BankUser constructSavedUser(String userName) {
		BankUser user = constructNewUser(USERNAME);
		user.setId(1L);
		user.setPassword(passwordEncoder.encode("123"));
		return user;
	}

	// construct new bank user without setting a user Id
	private BankUser constructNewUser(String userName) {
		BankUser newUser = new BankUser();
		newUser.setUserName(userName);
		newUser.setPassword("123");
		newUser.setFirstName(userName);
		newUser.setLastName("Green");
		newUser.setMiddleInitial("A");
		newUser.setGender("F");

		Date dateOfBirth = new GregorianCalendar(1980, Calendar.FEBRUARY, 10)
				.getTime();
		newUser.setDateOfBirth(dateOfBirth);

		newUser.setStreet("12345 water maple street");
		newUser.setCity("pittsburgh");
		newUser.setState("PA");
		newUser.setZip("12345");
		newUser.setPhone("123-456-7890");
		newUser.setEmail(userName + "@email.com");

		return newUser;
	}

}
