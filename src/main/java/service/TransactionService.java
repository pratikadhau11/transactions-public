package service;

import domain.account.Account;
import domain.transaction.Transaction;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class TransactionService {
    private AccountService accountService;

    public TransactionService(AccountService accountService) {
        this.accountService = accountService;
    }

    public Optional<TransactionResponse> transfer(String fromId, TransactionRequest transferRequest) {
        String to = transferRequest instanceof ToAccountTransactionRequest ?
                ((ToAccountTransactionRequest) transferRequest).to :
                UUID.randomUUID().toString();

        String transactionType = getTransactionType(transferRequest);

        synchronized (fromId.intern()) {
            synchronized (to.intern()) {
                switch (transactionType) {
                    case "Account" :
                        return transferToOtherAccount(fromId, (ToAccountTransactionRequest) transferRequest);
                    case "Withdraw" :
                        return withdraw(fromId, transferRequest);
                    default:
                        return deposit(fromId, transferRequest);
                }

            }
        }
    }

    private String getTransactionType(TransactionRequest  transferRequest){
        return transferRequest instanceof ToAccountTransactionRequest ? "Account"
                : transferRequest instanceof DepositRequest ? "Deposit" : "Withdraw";
    }

    private Optional<TransactionResponse> transferToOtherAccount(String fromId, ToAccountTransactionRequest transferRequest) {
        return executeAccountToAccountTransaction(fromId, transferRequest).map(this::saveTransaction);
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
                                                                     ToAccountTransactionRequest transferRequest) {
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
