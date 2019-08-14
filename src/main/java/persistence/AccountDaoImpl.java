package persistence;

import domain.Account;
import org.jdbi.v3.core.Jdbi;
import service.AccountDao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDaoImpl implements AccountDao {

    Jdbi jdbi;

    public AccountDaoImpl(Jdbi jdbi) {
        this.jdbi = jdbi;
    }


    @Override
    public List<Account> getAllAccounts() {
        List<Account> accounts1 = jdbi.withHandle(handle -> {
            List<Account> accounts = new ArrayList<>();
            handle.createQuery("Select * from accounts")
                    .mapToMap()
                    .forEach(map -> {
                        Long balance = (Long) map.get("balance");
                        accounts.add(new Account((String) map.get("id"), (String) map.get("name"))
                                    .deposit(BigInteger.valueOf(balance.longValue())));
                    });
            return accounts;
        });
        return accounts1;
    }

    @Override
    public Optional<Account> getAllAccount(String id) {
        return jdbi.withHandle(handle ->  handle.createQuery("Select * from accounts where id = '" +id +"'")
                    .mapToMap()
                    .findFirst()
                    .map(map -> {
                        Long balance = (Long) map.get("balance");
                        return new Account((String) map.get("id"), (String) map.get("name"))
                                .deposit(new BigInteger(balance.toString()));
                    })

        );
    }

    @Override
    public Boolean save(Account account) {
        return jdbi.withHandle(
                handle -> {
                    int execute = handle.createUpdate(String.format("UPDATE accounts SET balance='%s' WHERE id=%s;", account.balance, account.id)).execute();
                    return execute;
                }
        ) == 1 ? Boolean.TRUE : Boolean.FALSE;
    }
}