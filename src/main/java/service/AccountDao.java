package service;

import domain.account.Account;

import java.util.List;
import java.util.Optional;

public interface AccountDao {

    List<Account> getAllAccounts();

    Optional<Account> getAccount(String id);

    Account update(Account account);

    Account create(Account account) throws AccountCreationException;
}

