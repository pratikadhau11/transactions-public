package rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import domain.Account;

import java.math.BigInteger;

public class TransactionResponse {
    @JsonProperty
    String from;

    @JsonProperty
    String to;

    @JsonProperty
    BigInteger amount;

    @JsonProperty
    Account account;

    public TransactionResponse() {
    }

    public TransactionResponse(String from, String to, BigInteger amount, Account account) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.account = account;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public BigInteger getAmount() {
        return amount;
    }
}
