package service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import domain.account.Account;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
