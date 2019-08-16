package service;

import domain.account.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.Optional;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

    @Mock
    AccountService accountService;

    @InjectMocks
    TransactionService transactionService;

    private String accountId = "id";

    private Account account = new Account(accountId, "tom").deposit(new BigInteger("10000"));

    @Before
    public void setUp() throws Exception {
        reset(accountService);

        Optional<Account> maybeAccount = Optional.of(this.account);
        when(accountService.getAccount(accountId)).thenReturn(maybeAccount);
    }

    @Test
    public void shouldDepositMoneyToAccount() {
        DepositRequest transferRequest = new DepositRequest(1000l);

        Optional<TransactionResponse> transfer = transactionService.transfer(accountId, transferRequest);


    }
}
