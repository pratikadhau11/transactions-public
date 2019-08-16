package persistence;

import domain.account.Account;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import service.AccountCreationException;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class AccountDaoImplTest extends JdbiUnitTest{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldCreateUser() {
        AccountDaoImpl accountDao = new AccountDaoImpl(this.dbi);
        String id = UUID.randomUUID().toString();
        Account tom = new Account(id, "TOM");

        accountDao.create(tom);

        Optional<Account> account = accountDao.getAccount(id);
        assertEquals(tom.id, account.get().id);
    }

    @Test
    public void shouldNotAllowTOCreateUserWihSameId() {
        AccountDaoImpl accountDao = new AccountDaoImpl(this.dbi);
        String id = UUID.randomUUID().toString();
        Account tom = new Account(id, "TOM");
        Account tom2 = new Account(id, "TOM2");
        accountDao.create(tom);
        expectedException.expect(AccountCreationException.class);

        accountDao.create(tom2);
    }

    @Test
    public void shouldGetAllAccounts() {
        AccountDaoImpl accountDao = new AccountDaoImpl(this.dbi);
        accountDao.create(new Account("1", "Tom"));
        accountDao.create(new Account("2", "Ann"));
        accountDao.create(new Account("3", "Jake"));

        List<Account> allAccounts = accountDao.getAllAccounts();

        assertEquals(3, allAccounts.size());
    }

    @Test
    public void shouldGetSpecificAccounts() {
        AccountDaoImpl accountDao = new AccountDaoImpl(this.dbi);
        String tomId = UUID.randomUUID().toString();
        String annId = UUID.randomUUID().toString();
        String jakeId = UUID.randomUUID().toString();
        accountDao.create(new Account(tomId, "Tom"));
        accountDao.create(new Account(annId, "Ann"));
        accountDao.create(new Account(jakeId, "Jake"));

        Optional<Account> tom = accountDao.getAccount(tomId);

        assertEquals(tomId, tom.get().id);
        assertEquals("Tom", tom.get().name);

        Optional<Account> ann = accountDao.getAccount(annId);

        assertEquals(annId, ann.get().id);
        assertEquals("Ann", ann.get().name);
    }

    @Test
    public void shouldUpdateAccount() {
        AccountDaoImpl accountDao = new AccountDaoImpl(this.dbi);
        String tomId = UUID.randomUUID().toString();
        Account tom = new Account(tomId, "Tom");
        accountDao.create(tom);
        BigInteger amount = new BigInteger("10000");
        Account deposit = tom.deposit(amount);


        accountDao.update(deposit);

        Optional<Account> account = accountDao.getAccount(tomId);
        assertEquals(amount, account.get().balance);

        BigInteger withdrawAmount = new BigInteger("200");
        Account withdraw = account.get().withdraw(withdrawAmount);

        Account account1 = accountDao.update(withdraw);
        assertEquals(amount.subtract(withdrawAmount), account1.balance);
    }
}
