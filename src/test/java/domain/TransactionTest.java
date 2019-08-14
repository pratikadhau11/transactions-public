package domain;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class TransactionTest {

    @Test
    public void shouldPerformTransaction() {
        Account tom = new Account("1", "tom");
        Account tomWithBalance = tom.deposit(new BigInteger("1000"));
        Account ann = new Account("2", "Ann");
        Account annWithBalance = ann.deposit(new BigInteger("2000"));
        BigInteger amount = new BigInteger("500");

        Transaction transaction = new Transaction(tomWithBalance, annWithBalance, amount);

        assertEquals(transaction.from.balance, tomWithBalance.withdraw(amount).balance);
        assertEquals(transaction.to.balance, annWithBalance.deposit(amount).balance);
        assertEquals(transaction.amount, amount);
    }

}
