package domain.account;

import java.math.BigInteger;
import java.util.Objects;

import static java.lang.String.format;
import static java.math.BigInteger.ZERO;

public class Account {

    public final String id;
    public final String name;
    public BigInteger balance;

    public Account(String id, String name) {
        this.id = id;
        this.name = name;
        this.balance = ZERO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private Account(String id, String name, BigInteger balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public Account deposit(BigInteger bigInteger) {
        if(bigInteger.compareTo(ZERO) == -1){
            throw new DepositException(format("Deposit is negative",  id, bigInteger));
        }
        return new Account(this.id, this.name, this.balance.add(bigInteger));
    }

    public Account withdraw(BigInteger bigInteger) {
        if(balance.compareTo(bigInteger) == -1){
            throw new WithdrawException(format("Account number %s has balance less than %s",  id, bigInteger));
        }
        return new Account(this.id, this.name, this.balance.subtract(bigInteger));
    }
}
