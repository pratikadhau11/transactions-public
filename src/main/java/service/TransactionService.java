package service;

import domain.Account;
import domain.AccountNotFoundException;
import domain.Transaction;
import rest.TransactionResponse;
import rest.TransferRequest;

import java.math.BigInteger;
import java.util.Optional;

public class TransactionService {
    private AccountService accountService;

    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }

    public Optional<TransactionResponse> transfer(String fromId, TransferRequest transferRequest) {
        Optional<Account> maybeFrom = accountService.getAccount(fromId);
        Account to = accountService.getAccount(transferRequest.getTo())
                .orElseThrow(() -> new AccountNotFoundException(String.format("Account number %s is not found", transferRequest.getTo())));

        Optional<Transaction> maybeTransaction = maybeFrom.map(from -> new Transaction(from, to, BigInteger.valueOf(transferRequest.getAmount())));

        return maybeTransaction.map(transaction -> {
            accountService.save(transaction.from);
            accountService.save(transaction.to);
            return new TransactionResponse(transaction.from.id, transaction.to.id, transaction.amount, transaction.from);
        });

    }
}
