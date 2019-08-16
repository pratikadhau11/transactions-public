package rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BankTransferRequest  {

    @JsonProperty("to")
    private String to;

    @JsonProperty("amount")
    private long amount;

    public String getTo() {
        return to;
    }

    public long getAmount() {
        return amount;
    }
}
