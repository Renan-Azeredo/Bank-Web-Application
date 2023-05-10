package org.academiadecodigo.javabank.services;

import org.academiadecodigo.javabank.exceptions.AccountNotFoundException;
import org.academiadecodigo.javabank.exceptions.JavaBankException;
import org.academiadecodigo.javabank.persistence.dao.AccountDao;
import org.academiadecodigo.javabank.persistence.model.account.Account;
import org.academiadecodigo.javabank.persistence.model.account.CheckingAccount;
import org.academiadecodigo.javabank.persistence.model.account.SavingsAccount;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {

    private AccountDao accountDao;
    private AccountServiceImpl accountService;

    @Before
    public void setup() {

        accountDao = mock(AccountDao.class);

        accountService = new AccountServiceImpl();
        accountService.setAccountDao(accountDao);

    }

    @Test
    public void testGet() throws JavaBankException {

        // setup
        int fakeId = 9999;
        Account fakeAccount = mock(Account.class);
        when(accountDao.findById(fakeId)).thenReturn(fakeAccount);

        // exercise
        Account recipient = accountService.get(fakeId);

        // verify
        assertEquals(fakeAccount, recipient);

    }


    @Test
    public void testDeposit() throws JavaBankException {

        // setup
        int fakeId = 1;
        double amount = 100.5;
        Account fakeAccount = mock(Account.class);
        when(accountDao.findById(fakeId)).thenReturn(fakeAccount);

        // exercise
        accountService.deposit(fakeId, amount);

        // verify
        verify(fakeAccount, times(1)).credit(amount);
        verify(accountDao, times(1)).saveOrUpdate(fakeAccount);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testDepositInvalidAccount() throws JavaBankException {

        // setup
        when(accountDao.findById(anyInt())).thenReturn(null);

        // exercise
        accountService.deposit(1, 100);

    }

    @Test
    public void testWithdraw() throws JavaBankException {

        // setup
        int fakeId = 1;
        double amount = 100.5;
        Account fakeAccount = mock(Account.class);
        when(accountDao.findById(fakeId)).thenReturn(fakeAccount);

        // exercise
        accountService.withdraw(fakeId, amount);

        // verify
        verify(fakeAccount, times(1)).debit(amount);
        verify(accountDao, times(1)).saveOrUpdate(fakeAccount);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testWithdrawInvalidAccount() throws JavaBankException {

        // setup
        when(accountDao.findById(anyInt())).thenReturn(null);

        // exercise
        accountService.withdraw(1, 100);

    }

    @Test
    public void testTransfer() throws JavaBankException {

        // setup
        int fakeSrcId = 9998;
        int fakeDstId = 9999;
        double amount = 100.5;
        Account fakeSrcAccount = mock(Account.class);
        Account fakeDstAccount = mock(Account.class);
        when(accountDao.findById(fakeSrcId)).thenReturn(fakeSrcAccount);
        when(accountDao.findById(fakeDstId)).thenReturn(fakeDstAccount);
        when(fakeSrcAccount.canDebit(anyDouble())).thenReturn(true);
        when(fakeDstAccount.canCredit(anyDouble())).thenReturn(true);

        // exercise
        accountService.transfer(fakeSrcId, fakeDstId, amount);

        // validate
        verify(accountDao, times(1)).saveOrUpdate(fakeSrcAccount);
        verify(accountDao, times(1)).saveOrUpdate(fakeDstAccount);
        verify(fakeSrcAccount, times(1)).canDebit(amount);
        verify(fakeDstAccount, times(1)).canCredit(amount);
        verify(fakeSrcAccount, times(1)).debit(amount);
        verify(fakeDstAccount, times(1)).credit(amount);

    }

    @Test
    public void testTransferDebitNotPossible() throws JavaBankException {

        // setup
        int fakeSrcId = 9998;
        int fakeDstId = 9999;
        double amount = 100.5;
        Account fakeSrcAccount = mock(Account.class);
        Account fakeDstAccount = mock(Account.class);
        when(accountDao.findById(fakeSrcId)).thenReturn(fakeSrcAccount);
        when(accountDao.findById(fakeDstId)).thenReturn(fakeDstAccount);
        when(fakeSrcAccount.canDebit(anyDouble())).thenReturn(false);
        when(fakeDstAccount.canCredit(anyDouble())).thenReturn(true);

        // exercise
        accountService.transfer(fakeSrcId, fakeDstId, amount);

        // validate
        verify(accountDao, times(1)).saveOrUpdate(fakeSrcAccount);
        verify(accountDao, times(1)).saveOrUpdate(fakeDstAccount);
        verify(fakeSrcAccount, times(1)).canDebit(amount);

    }

    @Test
    public void testTransferCreditNotPossible() throws JavaBankException {

        // setup
        int fakeSrcId = 9998;
        int fakeDstId = 9999;
        double amount = 100.5;
        Account fakeSrcAccount = mock(Account.class);
        Account fakeDstAccount = mock(Account.class);
        when(accountDao.findById(fakeSrcId)).thenReturn(fakeSrcAccount);
        when(accountDao.findById(fakeDstId)).thenReturn(fakeDstAccount);
        when(fakeSrcAccount.canDebit(anyDouble())).thenReturn(true);
        when(fakeDstAccount.canCredit(anyDouble())).thenReturn(false);

        // exercise
        accountService.transfer(fakeSrcId, fakeDstId, amount);

        // validate
        verify(accountDao, times(1)).saveOrUpdate(fakeSrcAccount);
        verify(accountDao, times(1)).saveOrUpdate(fakeDstAccount);
        verify(fakeDstAccount, times(1)).canCredit(amount);

    }

    @Test(expected = AccountNotFoundException.class)
    public void testTransferInvalidSrcAccount() throws JavaBankException {

        // setup
        int fakeSrcId = 9998;
        int fakeDstId = 9999;
        double amount = 100.5;
        Account fakeDstAccount = mock(Account.class);
        when(accountDao.findById(fakeSrcId)).thenReturn(null);
        when(accountDao.findById(fakeDstId)).thenReturn(fakeDstAccount);

        // exercise
        accountService.transfer(fakeSrcId, fakeDstId, amount);

    }

    @Test(expected = AccountNotFoundException.class)
    public void testTransferInvalidDstAccount() throws JavaBankException {

        // setup
        int fakeSrcId = 9998;
        int fakeDstId = 9999;
        double amount = 100.5;
        Account fakeSrcAccount = mock(Account.class);
        when(accountDao.findById(fakeSrcId)).thenReturn(fakeSrcAccount);
        when(accountDao.findById(fakeDstId)).thenReturn(null);

        // exercise
        accountService.transfer(fakeSrcId, fakeDstId, amount);

    }

}
