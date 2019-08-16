package rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.account.DepositException;
import domain.account.WithdrawException;
import domain.transaction.TransactionException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import service.DepositRequest;
import service.ToBankTransactionRequest;
import service.TransactionService;
import service.WithdrawalRequest;

import javax.ws.rs.BadRequestException;

import java.io.IOException;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionResourceTest {
    @Mock
    TransactionService transactionService;

    @InjectMocks
    TransactionResource transactionResource;


    @Before
    public void setUp() {
        Mockito.reset(transactionService);
    }

    @Test
    public void shouldDepositAmount() throws IOException {
        TransferRequest transferRequest = new ObjectMapper().readValue("{\"amount\": 10000}",
                TransferRequest.class);
        String fromAccount = "fromAccount";

        transactionResource.deposit(fromAccount, transferRequest);

        ArgumentCaptor<DepositRequest> captor = ArgumentCaptor.forClass(DepositRequest.class);
        verify(transactionService, times(1)).transfer(eq(fromAccount), captor.capture());
        DepositRequest value = captor.getValue();
        Assert.assertEquals(10000l, value.amount);
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowBadRequestIfDepositExceptionOccur() throws IOException {
        TransferRequest transferRequest = new ObjectMapper().readValue("{\"amount\": 10000}",
                TransferRequest.class);
        String fromAccount = "fromAccount";
        ArgumentCaptor<DepositRequest> captor = ArgumentCaptor.forClass(DepositRequest.class);
        when(transactionService.transfer(eq(fromAccount), captor.capture()))
                .thenThrow(new DepositException("some thing"));

        transactionResource.deposit(fromAccount, transferRequest);
    }

    @Test
    public void shouldWithdrawAmount() throws IOException {
        TransferRequest transferRequest = new ObjectMapper().readValue("{\"amount\": 10000}",
                TransferRequest.class);
        String fromAccount = "fromAccount";

        transactionResource.withdraw(fromAccount, transferRequest);

        ArgumentCaptor<WithdrawalRequest> captor = ArgumentCaptor.forClass(WithdrawalRequest.class);
        verify(transactionService, times(1)).transfer(eq(fromAccount), captor.capture());
        WithdrawalRequest value = captor.getValue();
        Assert.assertEquals(10000l, value.amount);
    }



    @Test(expected = BadRequestException.class)
    public void shouldThrowBadRequestIfWithdrawExceptionOccur() throws IOException {
        TransferRequest transferRequest = new ObjectMapper().readValue("{\"amount\": 10000}",
                TransferRequest.class);
        String fromAccount = "fromAccount";
        ArgumentCaptor<WithdrawalRequest> captor = ArgumentCaptor.forClass(WithdrawalRequest.class);
        when(transactionService.transfer(eq(fromAccount), captor.capture()))
                .thenThrow(new WithdrawException("some thing"));

        transactionResource.withdraw(fromAccount, transferRequest);
    }



    @Test
    public void shouldTransferAmount() throws IOException {
        BankTransferRequest bankTransferRequest = new ObjectMapper()
                .readValue("{\"amount\": 10000, \"to\": \"1\"}",
                    BankTransferRequest.class);
        String fromAccount = "fromAccount";

        transactionResource.transfer(fromAccount, bankTransferRequest);

        ArgumentCaptor<ToBankTransactionRequest> captor = ArgumentCaptor.forClass(ToBankTransactionRequest.class);
        verify(transactionService, times(1)).transfer(eq(fromAccount), captor.capture());
        ToBankTransactionRequest value = captor.getValue();
        Assert.assertEquals(10000l, value.amount);
        Assert.assertEquals("1", value.to);
    }



    @Test(expected = BadRequestException.class)
    public void shouldThrowBadRequestIfTransactionExceptionOccur() throws IOException {
        BankTransferRequest bankTransferRequest = new ObjectMapper()
                .readValue("{\"amount\": 10000, \"to\": \"1\"}",
                        BankTransferRequest.class);
        String fromAccount = "fromAccount";
        ArgumentCaptor<ToBankTransactionRequest> captor = ArgumentCaptor.forClass(ToBankTransactionRequest.class);
        when(transactionService.transfer(eq(fromAccount), captor.capture()))
                .thenThrow(new TransactionException("some thing"));

        transactionResource.transfer(fromAccount, bankTransferRequest);
    }
}
