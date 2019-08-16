package domain;

import domain.account.Account;
import domain.transaction.Transaction;
import domain.transaction.TransactionException;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class TransactionTest {

    @Test
    public void shouldPerformTransaction() {
        Account tom = new Account("1", "tom").deposit(new BigInteger("1000"));
        Account ann = new Account("2", "Ann").deposit(new BigInteger("2000"));
        BigInteger amount = new BigInteger("500");

        Transaction transaction = new Transaction(tom, ann, amount);

        assertEquals(transaction.from.balance, tom.withdraw(amount).balance);
        assertEquals(transaction.to.balance, ann.deposit(amount).balance);
        assertEquals(transaction.amount, amount);
    }

    @Test(expected = TransactionException.class)
    public void shouldNotPerformTransactionOnSameAccount() {
        Account tom = new Account("1", "tom").deposit(new BigInteger("1000"));
        BigInteger amount = new BigInteger("500");

        new Transaction(tom, tom, amount);
    }

}
