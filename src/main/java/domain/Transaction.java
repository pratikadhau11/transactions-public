package domain;


import java.math.BigInteger;
import java.util.UUID;

public class Transaction {
    public final String id;
    public final Account from;
    public final Account to;
    public final BigInteger amount;

    public Transaction(Account from, Account to, BigInteger amount) {
        if(from.equals(to)){
            throw new TransactionException(String.format("Account number %s can not transfer to account number %s ", from.id, to.id));
        }
        this.id = UUID.randomUUID().toString();
        this.from = from.withdraw(amount);
        this.to = to.deposit(amount);
        this.amount = amount;
    }
}
