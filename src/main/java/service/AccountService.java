package service;

import domain.Account;

import java.util.List;
import java.util.Optional;

public class AccountService {
    AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public List<Account> getAccounts() {
        return accountDao.getAllAccounts();
    }

    public Optional<Account> getAccount(String id) {
        return accountDao.getAllAccount(id);
    }

    public Boolean save(Account account){
        return accountDao.save(account);
    }
}
