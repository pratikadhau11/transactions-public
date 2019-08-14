package domain;

import org.junit.Test;

import java.math.BigInteger;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccountTest {

    @Test
    public void shouldCreateAccount() {
        String id = UUID.randomUUID().toString();
        String name = "Tom Cruise";

        Account tomCruise = new Account(id, name);

        assertEquals(name, tomCruise.name);
        assertEquals(id, tomCruise.id);
        assertEquals(new BigInteger("0"), tomCruise.balance);
    }

    @Test
    public void accountWithSameIdShouldBeEqual() {
        String id = UUID.randomUUID().toString();
        String name = "Tom Cruise";
        Account tomCruise1 = new Account(id, name);
        Account tomCruise2 = new Account(id, name);

        assertTrue(tomCruise1.equals(tomCruise2));
    }

    @Test
    public void accountWithSameIdShouldNotBeEqual() {
        String id1 = UUID.randomUUID().toString();
        String name = "Tom Cruise";
        String id2 = UUID.randomUUID().toString();
        Account tomCruise1 = new Account(id1, name);
        Account tomCruise2 = new Account(id2, name);

        assertFalse(tomCruise1.equals(tomCruise2));
    }

    @Test
    public void shouldDepositInAccount() {
        String id = UUID.randomUUID().toString();
        String name = "Tom Cruise";
        Account account = new Account(id, name);
        Account accountWithBalance = account.deposit(new BigInteger("10"));

        Account response = accountWithBalance.deposit(new BigInteger("1"));

        assertEquals(new BigInteger("11"), response.balance);
    }

    @Test
    public void shouldDepositBigAmount() {
        String id = UUID.randomUUID().toString();
        String name = "Tom Cruise";
        Account account = new Account(id, name);

        Account response = account.deposit(new BigInteger("10000000000000"));

        assertEquals(new BigInteger("10000000000000"), response.balance);
    }

    @Test(expected = DepositException.class)
    public void shouldNotDepositNegativeAmount() {
        String id = UUID.randomUUID().toString();
        String name = "Tom Cruise";
        Account account = new Account(id, name);

        account.deposit(new BigInteger("-11"));
    }

    @Test
    public void shouldWithdrawInAccount() {
        String id = UUID.randomUUID().toString();
        String name = "Tom Cruise";
        Account account = new Account(id, name);
        Account accountWithBalance = account.deposit(new BigInteger("10"));

        Account response = accountWithBalance.withdraw(new BigInteger("1"));

        assertEquals(new BigInteger("9"), response.balance);
    }

    @Test(expected = WithdrawException.class)
    public void shouldNotDrawMoreThanBalance() {
        String id = UUID.randomUUID().toString();
        String name = "Tom Cruise";
        Account account = new Account(id, name);
        Account accountWithBalance = account.deposit(new BigInteger("10"));

        accountWithBalance.withdraw(new BigInteger("11"));
    }
}
