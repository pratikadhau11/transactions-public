package rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransferRequest {
    @JsonProperty
    private long amount;

    public long getAmount() {
        return amount;
    }
}
