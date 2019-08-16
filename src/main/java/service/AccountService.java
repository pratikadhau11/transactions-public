package service;

import domain.account.Account;
import rest.CreateAccountRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountService {
    AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public Account createAccount(CreateAccountRequest createAccountRequest) {
        Account account = new Account(UUID.randomUUID().toString(), createAccountRequest.getName());
        return accountDao.create(account);
    }

    public List<Account> getAccounts() {
        return accountDao.getAllAccounts();
    }

    public Optional<Account> getAccount(String id) {
        return accountDao.getAccount(id);
    }

    public Account update(Account account){
        return accountDao.update(account);
    }
}
