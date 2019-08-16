package rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateAccountRequest {
    private String name;

    @JsonProperty
    public String getName() {
        return name;
    }
}
