package rest;


import domain.account.DepositException;
import domain.account.WithdrawException;
import domain.transaction.TransactionException;
import service.DepositRequest;
import service.ToBankTransactionRequest;
import service.TransactionResponse;
import service.TransactionService;
import service.WithdrawalRequest;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.Optional;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {
    private TransactionService transactionService;

    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @POST
    @Path("/{id}/transfer")
    public Optional<TransactionResponse> transfer(@PathParam("id") String id, BankTransferRequest bankTransferRequest) {
        try{
            return transactionService.transfer(id,
                    new ToBankTransactionRequest(bankTransferRequest.getAmount(), bankTransferRequest.getTo()));
        }catch (TransactionException | DepositException | WithdrawException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @POST
    @Path("/{id}/deposit")
    public Optional<TransactionResponse> deposit(@PathParam("id") String id, TransferRequest bankTransferRequest) {
        try{
            return transactionService.transfer(id,
                    new DepositRequest(bankTransferRequest.getAmount()));
        }catch (DepositException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @POST
    @Path("/{id}/withdraw")
    public Optional<TransactionResponse> withdraw(@PathParam("id") String id, TransferRequest bankTransferRequest) {
        try{
            return transactionService.transfer(id,
                    new WithdrawalRequest(bankTransferRequest.getAmount()));
        }catch (WithdrawException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
