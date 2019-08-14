package service;

import domain.Account;

import java.util.List;
import java.util.Optional;

public interface AccountDao {

    List<Account> getAllAccounts();

    Optional<Account> getAllAccount(String id);

    Boolean save(Account account);
}

