package rest;

import domain.Account;
import domain.AccountNotFoundException;
import domain.DepositException;

import domain.TransactionException;
import domain.WithdrawException;
import service.AccountService;
import service.TransactionService;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.List;
import java.util.Optional;

@Path("/accounts")

@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private AccountService accountService;
    private TransactionService transactionService;

    public AccountResource(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GET
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @GET
    @Path("/{id}")
    public Optional<Account> getAccount(@PathParam("id") String id) {
        return accountService.getAccount(id);
    }

    @PUT
    @Path("/{id}/transfer")
    public Optional<TransactionResponse> transfer(@PathParam("id") String id, TransferRequest transferRequest) {
        try{
            return transactionService.transfer(id, transferRequest);
        }catch (WithdrawException | DepositException | TransactionException | AccountNotFoundException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
