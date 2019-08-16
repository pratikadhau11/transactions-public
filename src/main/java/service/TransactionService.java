package service;

import domain.account.Account;
import domain.transaction.Transaction;

import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Function;

public class TransactionService {
    private AccountService accountService;
    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }

    public Optional<TransactionResponse> transfer(String fromId, TransactionRequest transferRequest) {
        synchronized (fromId.intern()) {
            if(transferRequest instanceof ToBankTransactionRequest){
                return transferToOtherAccount(fromId, (ToBankTransactionRequest) transferRequest);
            } else if(transferRequest instanceof WithdrawalRequest){
                return withdraw(fromId, transferRequest);
            } else {
                return deposit(fromId, transferRequest);
            }
        }
    }

    private Optional<TransactionResponse> transferToOtherAccount(String fromId, ToBankTransactionRequest transferRequest) {
        synchronized (transferRequest.to.intern()) {
            return executeAccountToAccountTransaction(fromId, transferRequest).map(this::saveTransaction);
        }
    }

    private Optional<TransactionResponse> withdraw(String fromId, TransactionRequest transferRequest) {
        Function<Account, Account> withdraw = account -> account.withdraw(BigInteger.valueOf(transferRequest.amount));
        return transfer(fromId, transferRequest, withdraw);
    }

    private Optional<TransactionResponse> deposit(String fromId, TransactionRequest transferRequest) {
        Function<Account, Account> deposit = account -> account.deposit(BigInteger.valueOf(transferRequest.amount));
        return transfer(fromId, transferRequest, deposit);
    }

    private Optional<TransactionResponse> transfer(String fromId, TransactionRequest transferRequest, Function<Account, Account> transfer) {
        Optional<Account> maybeAccount = accountService.getAccount(fromId);
        return maybeAccount
                .map(transfer)
                .map(account -> accountService.update(account))
                .map(account -> new TransactionResponse(account.id, null, BigInteger.valueOf(transferRequest.amount), account));
    }

    private Optional<Transaction> executeAccountToAccountTransaction(String fromId,
                                                                     ToBankTransactionRequest transferRequest) {
        Optional<Account> maybeFrom = accountService.getAccount(fromId);
        Account to = accountService.getAccount(transferRequest.to)
                .orElseThrow(() ->
                        new AccountNotFoundException(String.format("Account number %s is not found",
                                transferRequest.to)));
        return maybeFrom.map(from -> new Transaction(from, to, BigInteger.valueOf(transferRequest.amount)));
    }

    private TransactionResponse saveTransaction(Transaction transaction) {
        accountService.update(transaction.from);
        accountService.update(transaction.to);
        return new TransactionResponse(transaction.from.id,
                transaction.to.id,
                transaction.amount,
                transaction.from);
    }
}
