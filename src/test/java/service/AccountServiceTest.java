package service;

import domain.account.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rest.CreateAccountRequest;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {
    @Mock
    AccountDao accountDao;

    @Mock
    CreateAccountRequest createAccountRequest;

    @InjectMocks
    AccountService accountService;

    @Before
    public void setUp() {
        Mockito.reset(accountDao, createAccountRequest);
    }

    @Test
    public void createAccount() {
        Account tom = new Account("id", "tom");
        ArgumentCaptor<Account> captor = forClass(Account.class);
        when(accountDao.create(captor.capture())).thenReturn(tom);
        when(createAccountRequest.getName()).thenReturn("tom");

        Account account = accountService.createAccount(createAccountRequest);

        assertEquals(tom, account);
        assertEquals(captor.getValue().name, "tom");
        assertEquals(captor.getValue().balance, BigInteger.ZERO);
    }

    @Test
    public void shouldRetrieveAllAccounts() {
        Account tom = new Account("id", "tom");
        when(accountDao.getAllAccounts()).thenReturn(Collections.singletonList(tom));

        List<Account> accounts = accountService.getAccounts();

        assertEquals(1, accounts.size());
        assertEquals(tom.id, accounts.get(0).id);
        assertEquals(tom.name, accounts.get(0).name);
        assertEquals(tom.balance, accounts.get(0).balance);
    }

    @Test
    public void shouldRetrieveSpecificAccount() {
        Account tom = new Account("id", "tom");
        when(accountDao.getAccount("id")).thenReturn(Optional.of(tom));

        Optional<Account> account = accountService.getAccount("id");


        assertEquals(tom.id, account.get().id);
        assertEquals(tom.name, account.get().name);
        assertEquals(tom.balance, account.get().balance);
    }

    @Test
    public void shouldUpdateAccount() {
        Account tom = new Account("id", "tom");
        Account updatedTom = new Account("id", "tom");
        when(accountDao.update(tom)).thenReturn(updatedTom);

        Account actual = accountService.update(tom);

        assertEquals(updatedTom, actual);
    }
}
