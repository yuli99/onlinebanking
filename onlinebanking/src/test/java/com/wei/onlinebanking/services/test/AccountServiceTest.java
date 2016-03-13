package com.wei.onlinebanking.services.test;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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

import com.wei.onlinebanking.dao.AccountDao;
import com.wei.onlinebanking.domain.BankAccount;
import com.wei.onlinebanking.domain.BankUser;
import com.wei.onlinebanking.services.impl.AccountServiceImpl;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest extends TestCase {

	@Mock
	private FacesContext facesContext;
	@Mock
	private AccountDao accountDao;
	private AccountServiceImpl accountServiceImpl;

	private BankUser bankUser;
	private BCryptPasswordEncoder passwordEncoder;

	@Before
	public void init() {
		bankUser = constructBankUser();
		passwordEncoder = new BCryptPasswordEncoder();

		accountServiceImpl = new AccountServiceImpl() {
			@Override
			protected FacesContext getFacesContext() {
				return facesContext;
			}
		};

		accountServiceImpl.setAccountDao(accountDao);
	}

	@Test
	public void testCreateAccountSuccess() {
		BankAccount newAccount = constructNewAccount();

		when(accountDao.save(newAccount)).thenAnswer(new Answer<BankAccount>() {
			@Override
			public BankAccount answer(InvocationOnMock invocation)
					throws Throwable {
				BankAccount account = (BankAccount) invocation.getArguments()[0];
				account.setId(1L);
				return account;
			}
		});

		assertNull(newAccount.getId());
		assertEquals("1234", newAccount.getAccountPin());

		Boolean isCreateAccountSuccess = accountServiceImpl.createAccount(
				newAccount, bankUser);
		verify(accountDao, times(1)).save(newAccount);
		assertTrue(isCreateAccountSuccess);
		assertNotNull(newAccount.getId());
		assertEquals(bankUser, newAccount.getBankUser());
		assertEquals(BigDecimal.ZERO, newAccount.getAccountBalance());
		assertNotNull(newAccount.getDateOfCreated());
		assertTrue(passwordEncoder.matches("1234", newAccount.getAccountPin()));
	}

	@Test
	public void testCreateAccountFailtoSave() {
		BankAccount newAccount = constructNewAccount();

		when(accountDao.save(newAccount)).thenThrow(new PersistenceException());

		assertEquals("1234", newAccount.getAccountPin());

		Boolean isCreateAccountSuccess = accountServiceImpl.createAccount(
				newAccount, bankUser);
		verify(accountDao, times(1)).save(newAccount);
		assertFalse(isCreateAccountSuccess);
		assertNull(newAccount.getId());
		assertEquals(bankUser, newAccount.getBankUser());
		assertEquals(BigDecimal.ZERO, newAccount.getAccountBalance());
		assertNotNull(newAccount.getDateOfCreated());
		assertTrue(passwordEncoder.matches("1234", newAccount.getAccountPin()));
	}

	@Test
	public void testUpdateAccountSuccess() {
		BankAccount savedAccount = constructSavedAccount(2L,
				Timestamp.valueOf("2016-01-02 10:11:10.0"));

		when(accountDao.update(savedAccount)).thenReturn(savedAccount);

		Boolean isUpdateAccountSuccess = accountServiceImpl
				.updateAccount(savedAccount);
		verify(accountDao, times(1)).update(savedAccount);
		assertTrue(isUpdateAccountSuccess);
	}

	@Test
	public void testUpdateAccountFailtoUpdate() {
		BankAccount savedAccount = constructSavedAccount(2L,
				Timestamp.valueOf("2016-01-02 10:11:10.0"));

		when(accountDao.update(savedAccount)).thenThrow(
				new PersistenceException());

		Boolean isUpdateAccountSuccess = accountServiceImpl
				.updateAccount(savedAccount);
		verify(accountDao, times(1)).update(savedAccount);
		assertFalse(isUpdateAccountSuccess);
	}

	@Test
	public void testLoadAccountById() {
		BankAccount savedAccount = constructSavedAccount(3L,
				Timestamp.valueOf("2016-01-03 10:11:10.0"));

		when(accountDao.findById(savedAccount.getId()))
				.thenReturn(savedAccount);

		BankAccount account = accountServiceImpl.loadAccountById(savedAccount
				.getId());
		verify(accountDao, times(1)).findById(savedAccount.getId());
		assertEquals(savedAccount, account);
	}

	@Test
	public void testLoadAccountsByUser() {
		List<BankAccount> accountList = constructAccountList();

		when(accountDao.loadAccountsByUser(bankUser)).thenReturn(accountList);

		List<BankAccount> accounts = accountServiceImpl
				.loadAccountsByUser(bankUser);
		verify(accountDao, times(1)).loadAccountsByUser(bankUser);
		assertEquals(accountList, accounts);
	}

	@Test
	public void testCleanDataModel() {
		BankAccount savedAccount = constructSavedAccount(3L,
				Timestamp.valueOf("2016-01-03 10:11:10.0"));

		accountServiceImpl.cleanDataModel(savedAccount);
		assertNull(savedAccount.getId());
		assertNull(savedAccount.getAccountType());
		assertNull(savedAccount.getAccountPin());
	}

	// construct new account based on UI submitted form with null account Id
	private BankAccount constructNewAccount() {
		BankAccount account = new BankAccount();
		account.setAccountType("Saving");
		account.setAccountPin("1234");
		return account;
	}

	// construct saved account with given Id and dateOfCreated
	private BankAccount constructSavedAccount(Long id, Timestamp dateOfCreated) {
		BankAccount account = new BankAccount();
		account.setId(id);
		account.setAccountType("Saving");

		account.setAccountPin(passwordEncoder.encode("1234"));
		account.setAccountBalance(BigDecimal.ZERO);
		account.setDateOfCreated(dateOfCreated);
		account.setBankUser(bankUser);

		return account;
	}

	// construct list of account for user ann
	private List<BankAccount> constructAccountList() {
		List<BankAccount> accountList = new ArrayList<>();

		BankAccount account1 = constructSavedAccount(1L,
				Timestamp.valueOf("2016-01-01 10:11:10.0"));
		accountList.add(account1);

		BankAccount account2 = constructSavedAccount(2L,
				Timestamp.valueOf("2016-01-02 10:11:10.0"));
		accountList.add(account2);

		BankAccount account3 = constructSavedAccount(3L,
				Timestamp.valueOf("2016-01-03 10:11:10.0"));
		accountList.add(account3);

		return accountList;
	}

	// construct bank user
	private BankUser constructBankUser() {
		BankUser bankUser = new BankUser();
		bankUser.setId(1L);
		bankUser.setUserName("ann");
		bankUser.setPassword("123");
		bankUser.setFirstName("Ann");
		bankUser.setLastName("Green");
		bankUser.setMiddleInitial("A");
		bankUser.setGender("F");

		Date dateOfBirth = new GregorianCalendar(1980, Calendar.FEBRUARY, 10)
				.getTime();
		bankUser.setDateOfBirth(dateOfBirth);

		bankUser.setStreet("12345 water maple street");
		bankUser.setCity("pittsburgh");
		bankUser.setState("PA");
		bankUser.setZip("12345");
		bankUser.setPhone("123-456-7890");
		bankUser.setEmail("ann@email.com");

		return bankUser;
	}

}
