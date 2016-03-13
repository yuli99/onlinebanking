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
import com.wei.onlinebanking.dao.TransactionDao;
import com.wei.onlinebanking.domain.BankAccount;
import com.wei.onlinebanking.domain.BankUser;
import com.wei.onlinebanking.domain.TransactionRec;
import com.wei.onlinebanking.dto.TranRecByAccount;
import com.wei.onlinebanking.dto.TranRecSearch;
import com.wei.onlinebanking.dto.TransactionDetail;
import com.wei.onlinebanking.services.impl.TransactionServiceImpl;

import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest extends TestCase {

	@Mock
	private FacesContext facesContext;
	@Mock
	private AccountDao accountDao;
	@Mock
	private TransactionDao transactionDao;
	private TransactionServiceImpl transactionServiceImpl;

	private BCryptPasswordEncoder passwordEncoder;

	@Before
	public void init() {
		passwordEncoder = new BCryptPasswordEncoder();

		transactionServiceImpl = new TransactionServiceImpl() {
			@Override
			protected FacesContext getFacesContext() {
				return facesContext;
			}
		};

		transactionServiceImpl.setAccountDao(accountDao);
		transactionServiceImpl.setTransactionDao(transactionDao);
	}

	@Test
	public void testCreateWithdrawRecSuccess() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0")); // balance: 500
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setAmount(new BigDecimal(100)); // less than account balance
		tranDetail.setSecurityPin("1234"); // correct pin

		when(transactionDao.save(any(TransactionRec.class))).thenAnswer(
				new Answer<TransactionRec>() {
					@Override
					public TransactionRec answer(InvocationOnMock invocation)
							throws Throwable {
						TransactionRec record = (TransactionRec) invocation
								.getArguments()[0];
						record.setId(1L);
						return record;
					}
				});
		when(accountDao.update(any(BankAccount.class))).thenAnswer(
				new Answer<BankAccount>() {
					@Override
					public BankAccount answer(InvocationOnMock invocation)
							throws Throwable {
						BankAccount account = (BankAccount) invocation
								.getArguments()[0];
						return account;
					}
				});

		Boolean isCreateWithdrawRecSuccess = transactionServiceImpl
				.createWithdrawRec(tranDetail);
		verify(transactionDao, times(1)).save(any(TransactionRec.class));
		verify(accountDao, times(1)).update(any(BankAccount.class));
		assertTrue(isCreateWithdrawRecSuccess);

		assertEquals(new BigDecimal(400), accountFrom.getAccountBalance());
		assertNotNull(tranDetail.getId());
		assertEquals("Withdraw", tranDetail.getType());
		assertNotNull(tranDetail.getTranTime());
		assertNull(tranDetail.getSecurityPin());
	}

	@Test
	public void testCreateWithdrawRecNullAccount() {
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(null); // null account
		tranDetail.setAmount(new BigDecimal(100)); // less than account balance
		tranDetail.setSecurityPin("1234"); // correct pin

		Boolean isCreateWithdrawRecSuccess = transactionServiceImpl
				.createWithdrawRec(tranDetail);
		assertFalse(isCreateWithdrawRecSuccess);
	}

	@Test
	public void testCreateWithdrawRecWrongPin() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setAmount(new BigDecimal(100)); // less than account balance
		tranDetail.setSecurityPin("4321"); // wrong pin

		Boolean isCreateWithdrawRecSuccess = transactionServiceImpl
				.createWithdrawRec(tranDetail);
		assertFalse(isCreateWithdrawRecSuccess);
	}

	@Test
	public void testCreateWithdrawRecExceedBalance() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setAmount(new BigDecimal(1000)); // exceed account balance
		tranDetail.setSecurityPin("1234"); // correct pin

		Boolean isCreateWithdrawRecSuccess = transactionServiceImpl
				.createWithdrawRec(tranDetail);
		assertFalse(isCreateWithdrawRecSuccess);
	}

	@Test
	public void testCreateWithdrawRecFailToSaveRec() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setAmount(new BigDecimal(100)); // less than account balance
		tranDetail.setSecurityPin("1234"); // correct pin

		when(transactionDao.save(any(TransactionRec.class))).thenThrow(
				new PersistenceException());

		Boolean isCreateWithdrawRecSuccess = transactionServiceImpl
				.createWithdrawRec(tranDetail);
		verify(transactionDao, times(1)).save(any(TransactionRec.class));
		assertFalse(isCreateWithdrawRecSuccess);
	}

	@Test
	public void testCreateWithdrawRecFailToUpdateAccount() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setAmount(new BigDecimal(100)); // less than account balance
		tranDetail.setSecurityPin("1234"); // correct pin

		when(transactionDao.save(any(TransactionRec.class))).thenAnswer(
				new Answer<TransactionRec>() {
					@Override
					public TransactionRec answer(InvocationOnMock invocation)
							throws Throwable {
						TransactionRec record = (TransactionRec) invocation
								.getArguments()[0];
						record.setId(1L);
						return record;
					}
				});
		when(accountDao.update(any(BankAccount.class))).thenThrow(
				new PersistenceException());

		Boolean isCreateWithdrawRecSuccess = transactionServiceImpl
				.createWithdrawRec(tranDetail);
		verify(transactionDao, times(1)).save(any(TransactionRec.class));
		verify(accountDao, times(1)).update(any(BankAccount.class));
		assertFalse(isCreateWithdrawRecSuccess);
	}

	@Test
	public void testCreateDepositRecSuccess() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0")); // balance: 500
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setAmount(new BigDecimal(100));
		tranDetail.setSecurityPin("1234"); // correct pin

		when(transactionDao.save(any(TransactionRec.class))).thenAnswer(
				new Answer<TransactionRec>() {
					@Override
					public TransactionRec answer(InvocationOnMock invocation)
							throws Throwable {
						TransactionRec record = (TransactionRec) invocation
								.getArguments()[0];
						record.setId(1L);
						return record;
					}
				});
		when(accountDao.update(any(BankAccount.class))).thenAnswer(
				new Answer<BankAccount>() {
					@Override
					public BankAccount answer(InvocationOnMock invocation)
							throws Throwable {
						BankAccount account = (BankAccount) invocation
								.getArguments()[0];
						return account;
					}
				});

		Boolean isCreateDepositRecSuccess = transactionServiceImpl
				.createDepositRec(tranDetail);
		verify(transactionDao, times(1)).save(any(TransactionRec.class));
		verify(accountDao, times(1)).update(any(BankAccount.class));
		assertTrue(isCreateDepositRecSuccess);

		assertEquals(new BigDecimal(600), accountFrom.getAccountBalance());
		assertNotNull(tranDetail.getId());
		assertEquals("Deposit", tranDetail.getType());
		assertNotNull(tranDetail.getTranTime());
		assertNull(tranDetail.getSecurityPin());
	}

	@Test
	public void testCreateDepositRecNullAccount() {
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(null); // null account
		tranDetail.setAmount(new BigDecimal(100));
		tranDetail.setSecurityPin("1234"); // correct pin

		Boolean isCreateDepositRecSuccess = transactionServiceImpl
				.createDepositRec(tranDetail);
		assertFalse(isCreateDepositRecSuccess);
	}

	@Test
	public void testCreateDepositRecWrongPin() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setAmount(new BigDecimal(100));
		tranDetail.setSecurityPin("4321"); // wrong pin

		Boolean isCreateDepositRecSuccess = transactionServiceImpl
				.createDepositRec(tranDetail);
		assertFalse(isCreateDepositRecSuccess);
	}

	@Test
	public void testCreateDepositRecFailToSaveRec() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setAmount(new BigDecimal(100));
		tranDetail.setSecurityPin("1234"); // correct pin

		when(transactionDao.save(any(TransactionRec.class))).thenThrow(
				new PersistenceException());

		Boolean isCreateDepositRecSuccess = transactionServiceImpl
				.createDepositRec(tranDetail);
		verify(transactionDao, times(1)).save(any(TransactionRec.class));
		assertFalse(isCreateDepositRecSuccess);
	}

	@Test
	public void testCreateDepositRecFailToUpdateAccount() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setAmount(new BigDecimal(100));
		tranDetail.setSecurityPin("1234"); // correct pin

		when(transactionDao.save(any(TransactionRec.class))).thenAnswer(
				new Answer<TransactionRec>() {
					@Override
					public TransactionRec answer(InvocationOnMock invocation)
							throws Throwable {
						TransactionRec record = (TransactionRec) invocation
								.getArguments()[0];
						record.setId(1L);
						return record;
					}
				});
		when(accountDao.update(any(BankAccount.class))).thenThrow(
				new PersistenceException());

		Boolean isCreateDepositRecSuccess = transactionServiceImpl
				.createDepositRec(tranDetail);
		verify(transactionDao, times(1)).save(any(TransactionRec.class));
		verify(accountDao, times(1)).update(any(BankAccount.class));
		assertFalse(isCreateDepositRecSuccess);
	}

	@Test
	public void testCreateTransferRecSuccess() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0")); // balance: 500
		BankAccount accountTo = constructBankAccount(2L,
				Timestamp.valueOf("2016-01-02 10:19:10.0")); // balance: 500
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setIdTo("2"); // valid account id
		tranDetail.setAmount(new BigDecimal(100)); // less than account balance
		tranDetail.setSecurityPin("1234"); // correct pin

		when(accountDao.checkExistence(2L)).thenReturn(true);
		when(accountDao.findById(2L)).thenReturn(accountTo);
		when(transactionDao.save(any(TransactionRec.class))).thenAnswer(
				new Answer<TransactionRec>() {
					@Override
					public TransactionRec answer(InvocationOnMock invocation)
							throws Throwable {
						TransactionRec record = (TransactionRec) invocation
								.getArguments()[0];
						record.setId(1L);
						return record;
					}
				});
		when(accountDao.update(any(BankAccount.class))).thenAnswer(
				new Answer<BankAccount>() {
					@Override
					public BankAccount answer(InvocationOnMock invocation)
							throws Throwable {
						BankAccount account = (BankAccount) invocation
								.getArguments()[0];
						return account;
					}
				});

		Boolean isCreateTransferRecSuccess = transactionServiceImpl
				.createTransferRec(tranDetail);
		verify(accountDao, times(1)).checkExistence(2L);
		verify(accountDao, times(1)).findById(2L);
		verify(transactionDao, times(1)).save(any(TransactionRec.class));
		verify(accountDao, times(2)).update(any(BankAccount.class));
		assertTrue(isCreateTransferRecSuccess);

		assertEquals(new BigDecimal(400), accountFrom.getAccountBalance());
		assertEquals(new BigDecimal(600), accountTo.getAccountBalance());
		assertNotNull(tranDetail.getId());
		assertEquals("Transfer", tranDetail.getType());
		assertNotNull(tranDetail.getTranTime());
		assertNull(tranDetail.getSecurityPin());
	}

	@Test
	public void testCreateTransferRecNullAccount() {
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(null); // null account
		tranDetail.setIdTo("2"); // valid account id
		tranDetail.setAmount(new BigDecimal(100)); // less than account balance
		tranDetail.setSecurityPin("1234"); // correct pin

		Boolean isCreateTransferRecSuccess = transactionServiceImpl
				.createTransferRec(tranDetail);
		assertFalse(isCreateTransferRecSuccess);
	}

	@Test
	public void testCreateTransferRecInvalidIdTo() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setIdTo("9"); // invalid account id
		tranDetail.setAmount(new BigDecimal(100)); // less than account balance
		tranDetail.setSecurityPin("1234"); // correct pin

		when(accountDao.checkExistence(9L)).thenReturn(false);

		Boolean isCreateTransferRecSuccess = transactionServiceImpl
				.createTransferRec(tranDetail);
		verify(accountDao, times(1)).checkExistence(9L);
		assertFalse(isCreateTransferRecSuccess);
	}

	@Test
	public void testCreateTransferRecSameId() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setIdTo("1"); // the same id as accountFrom
		tranDetail.setAmount(new BigDecimal(100)); // less than account balance
		tranDetail.setSecurityPin("1234"); // correct pin

		when(accountDao.checkExistence(1L)).thenReturn(true);

		Boolean isCreateTransferRecSuccess = transactionServiceImpl
				.createTransferRec(tranDetail);
		verify(accountDao, times(1)).checkExistence(1L);
		assertFalse(isCreateTransferRecSuccess);
	}

	@Test
	public void testCreateTransferRecWrongPin() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setIdTo("2"); // valid account id
		tranDetail.setAmount(new BigDecimal(100)); // less than account balance
		tranDetail.setSecurityPin("4321"); // wrong pin

		when(accountDao.checkExistence(2L)).thenReturn(true);

		Boolean isCreateTransferRecSuccess = transactionServiceImpl
				.createTransferRec(tranDetail);
		verify(accountDao, times(1)).checkExistence(2L);
		assertFalse(isCreateTransferRecSuccess);
	}

	@Test
	public void testCreateTransferRecExceedBalance() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setIdTo("2"); // valid account id
		tranDetail.setAmount(new BigDecimal(1000)); // exceed account balance
		tranDetail.setSecurityPin("1234"); // correct pin

		when(accountDao.checkExistence(2L)).thenReturn(true);

		Boolean isCreateTransferRecSuccess = transactionServiceImpl
				.createTransferRec(tranDetail);
		verify(accountDao, times(1)).checkExistence(2L);
		assertFalse(isCreateTransferRecSuccess);
	}

	@Test
	public void testCreateTransferRecFailToSaveRec() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		BankAccount accountTo = constructBankAccount(2L,
				Timestamp.valueOf("2016-01-02 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setIdTo("2"); // valid account id
		tranDetail.setAmount(new BigDecimal(100)); // less than account balance
		tranDetail.setSecurityPin("1234"); // correct pin

		when(accountDao.checkExistence(2L)).thenReturn(true);
		when(accountDao.findById(2L)).thenReturn(accountTo);
		when(transactionDao.save(any(TransactionRec.class))).thenThrow(
				new PersistenceException());

		Boolean isCreateTransferRecSuccess = transactionServiceImpl
				.createTransferRec(tranDetail);
		verify(accountDao, times(1)).checkExistence(2L);
		verify(accountDao, times(1)).findById(2L);
		verify(transactionDao, times(1)).save(any(TransactionRec.class));
		assertFalse(isCreateTransferRecSuccess);
	}

	@Test
	public void testCreateTransferRecFailToUpdateAccount() {
		BankAccount accountFrom = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-01 10:19:10.0"));
		BankAccount accountTo = constructBankAccount(2L,
				Timestamp.valueOf("2016-01-02 10:19:10.0"));
		TransactionDetail tranDetail = new TransactionDetail();
		tranDetail.setAccountFrom(accountFrom); // valid account
		tranDetail.setIdTo("2"); // valid account id
		tranDetail.setAmount(new BigDecimal(100)); // less than account balance
		tranDetail.setSecurityPin("1234"); // correct pin

		when(accountDao.checkExistence(2L)).thenReturn(true);
		when(accountDao.findById(2L)).thenReturn(accountTo);
		when(transactionDao.save(any(TransactionRec.class))).thenAnswer(
				new Answer<TransactionRec>() {
					@Override
					public TransactionRec answer(InvocationOnMock invocation)
							throws Throwable {
						TransactionRec record = (TransactionRec) invocation
								.getArguments()[0];
						record.setId(1L);
						return record;
					}
				});
		when(accountDao.update(any(BankAccount.class))).thenThrow(
				new PersistenceException());

		Boolean isCreateTransferRecSuccess = transactionServiceImpl
				.createTransferRec(tranDetail);
		verify(accountDao, times(1)).checkExistence(2L);
		verify(accountDao, times(1)).findById(2L);
		verify(transactionDao, times(1)).save(any(TransactionRec.class));
		verify(accountDao, times(1)).update(any(BankAccount.class));
		assertFalse(isCreateTransferRecSuccess);
	}

	@Test
	public void testLoadRecordById() {
		TransactionRec record = constructTransactionRec(1L,
				Timestamp.valueOf("2016-01-07 10:19:10.0"));

		when(transactionDao.findById(1L)).thenReturn(record);

		TransactionRec result = transactionServiceImpl.loadRecordById(1L);
		verify(transactionDao, times(1)).findById(1L);
		assertEquals(record, result);
	}

	@Test
	public void testLoadRecrodsByAccountAndTime() {
		BankAccount account = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-02 10:11:10.0"));
		Date start = new GregorianCalendar(2016, Calendar.JANUARY, 20)
				.getTime();
		Date end = new GregorianCalendar(2016, Calendar.FEBRUARY, 20).getTime();
		TranRecSearch tranRecSearch = new TranRecSearch();
		tranRecSearch.setAccount(account);
		tranRecSearch.setStartDate(start);
		tranRecSearch.setEndDate(end);

		List<TransactionRec> recordList = constructTranRecList();

		when(transactionDao.loadRecordsByAccount(account)).thenReturn(
				recordList);

		List<TranRecByAccount> records = transactionServiceImpl
				.loadRecordsByAccountAndTime(tranRecSearch);
		verify(transactionDao, times(1)).loadRecordsByAccount(account);
		assertEquals(3, records.size());
	}

	@Test
	public void testCleanDataModel() {
		TransactionDetail tranDetail = new TransactionDetail();

		transactionServiceImpl.cleanDataModel(tranDetail);
		assertNull(tranDetail.getAccountFrom());
		assertNull(tranDetail.getIdTo());
		assertNull(tranDetail.getAmount());
		assertNull(tranDetail.getSecurityPin());
	}

	// construct transaction records of a given account
	private List<TransactionRec> constructTranRecList() {
		List<TransactionRec> recordList = new ArrayList<>();

		TransactionRec rec1 = constructTransactionRec(1L,
				Timestamp.valueOf("2016-01-07 10:19:10.0"));
		recordList.add(rec1);

		TransactionRec rec2 = constructTransactionRec(3L,
				Timestamp.valueOf("2016-01-20 10:15:10.0"));
		recordList.add(rec2);

		TransactionRec rec3 = constructTransactionRec(5L,
				Timestamp.valueOf("2016-02-02 10:10:10.0"));
		recordList.add(rec3);

		TransactionRec rec4 = constructTransactionRec(7L,
				Timestamp.valueOf("2016-02-20 10:11:10.0"));
		recordList.add(rec4);

		TransactionRec rec5 = constructTransactionRec(7L,
				Timestamp.valueOf("2016-03-02 10:24:10.0"));
		recordList.add(rec5);

		return recordList;
	}

	// construct transaction Record with given id and transaction time
	private TransactionRec constructTransactionRec(Long id, Timestamp tranTime) {
		BankAccount account = constructBankAccount(1L,
				Timestamp.valueOf("2016-01-02 10:11:10.0"));
		TransactionRec tranRec = new TransactionRec();
		tranRec.setId(id);
		tranRec.setAccountFrom(account);
		tranRec.setAccountTo(null);
		tranRec.setTransactionAmount(new BigDecimal(500));
		tranRec.setTransactionType("Deposite");
		tranRec.setTransactionTime(tranTime);

		return tranRec;
	}

	// construct bank account with given account Id and dateOfCreated
	private BankAccount constructBankAccount(Long id, Timestamp dateOfCreated) {
		BankAccount account = new BankAccount();
		account.setId(id);
		account.setAccountType("Saving");

		account.setAccountPin(passwordEncoder.encode("1234"));
		account.setAccountBalance(new BigDecimal(500));
		account.setDateOfCreated(dateOfCreated);

		BankUser user = constructBankUser();
		account.setBankUser(user);

		return account;
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
