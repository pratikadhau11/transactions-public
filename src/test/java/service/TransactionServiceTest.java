package service;

import domain.account.Account;
import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    @Mock
    AccountService accountService;

    @InjectMocks
    TransactionService transactionService;

    private String accountId = "1";

    private Account account = new Account(accountId, "tom").deposit(new BigInteger("10000"));

    @Before
    public void setUp() throws Exception {
        reset(accountService);

        Optional<Account> maybeAccount = Optional.of(this.account);
        when(accountService.getAccount(accountId)).thenReturn(maybeAccount);
    }

    @Test
    public void shouldDepositMoneyToAccount() throws InterruptedException {
        DepositRequest transferRequest = new DepositRequest(1000l);
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        Account depositedAccount = account.deposit(new BigInteger("1000"));
        when(accountService.update(captor.capture())).thenReturn(depositedAccount);

        Optional<TransactionResponse> transfer = transactionService.transfer(accountId, transferRequest);

        Account actualDepositedAccount = captor.getValue();
        assertEquals(depositedAccount.balance, actualDepositedAccount.balance);
        TransactionResponse transactionResponse = transfer.get();
        assertEquals(accountId, transactionResponse.from);
        assertEquals(transferRequest.amount, transactionResponse.amount.longValue());
        assertNull(transactionResponse.to);
        assertEquals(depositedAccount, transactionResponse.account);
    }

    @Test
    public void shouldWithdrawMoneyToAccount() throws InterruptedException {
        WithdrawalRequest transferRequest = new WithdrawalRequest(1000l);
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        Account withdrawnAccount = account.withdraw(new BigInteger("1000"));
        when(accountService.update(captor.capture())).thenReturn(withdrawnAccount);

        Optional<TransactionResponse> transfer = transactionService.transfer(accountId, transferRequest);

        Account actualDepositedAccount = captor.getValue();
        assertEquals(withdrawnAccount.balance, actualDepositedAccount.balance);
        TransactionResponse transactionResponse = transfer.get();
        assertEquals(accountId, transactionResponse.from);
        assertEquals(transferRequest.amount, transactionResponse.amount.longValue());
        assertNull(transactionResponse.to);
        assertEquals(withdrawnAccount, transactionResponse.account);
    }

    @Test
    public void shouldTransferMoneyToOtherAccount() throws InterruptedException {
        Account annAccount = new Account("2", "Ann").deposit(new BigInteger("9000"));
        Optional<Account> mayBeAnnAccount = Optional.of(annAccount);
        when(accountService.getAccount("2")).thenReturn(mayBeAnnAccount);
        ToAccountTransactionRequest transferRequest = new ToAccountTransactionRequest(1000l, "2");
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        Account withdrawnAccount = account.withdraw(new BigInteger("1000"));
        Account depositedAccount = annAccount.deposit(new BigInteger("1000"));
        when(accountService.update(withdrawnAccount)).thenReturn(withdrawnAccount);
        when(accountService.update(depositedAccount)).thenReturn(withdrawnAccount);

        Optional<TransactionResponse> transfer = transactionService.transfer(accountId, transferRequest);

        TransactionResponse transactionResponse = transfer.get();
        assertEquals(depositedAccount.id, transactionResponse.to);
        assertEquals(withdrawnAccount.id, transactionResponse.from);
        assertEquals(withdrawnAccount, transactionResponse.account);
        assertEquals(new BigInteger("1000"), transactionResponse.amount);
    }

}
