package integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import static org.junit.Assert.assertEquals;

public class AccountIntegration extends Integration {
    String tomLocation;

    @Override
    void testSetup() {
        Response tomCreated = client.target(
                String.format("http://localhost:%s/accounts", RULE.getLocalPort()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity("{\n" +
                        "\t\"name\" : \"Tom\"\n" +
                        "}", MediaType.APPLICATION_JSON));

        assertEquals(201, tomCreated.getStatus());

        tomLocation = tomCreated.getHeaderString("location");
    }

    @Test
    public void shouldCreateAccount() {
        Response tomResponse = client.target(tomLocation)
                .request()
                .get();

        assertEquals(200, tomResponse.getStatus());
        JsonNode jsonNode = tomResponse.readEntity(JsonNode.class);
        assertEquals("Tom", jsonNode.get("name").asText());
        assertEquals(0l, jsonNode.get("balance").asLong());
    }

    @Test
    public void shouldDepositToAccount() {
        Response depositResponse = client.target(UriBuilder.fromUri(tomLocation).path("deposit"))
                .request("application/json")
                .post(Entity.entity("{\t\"amount\": 100}", MediaType.APPLICATION_JSON_TYPE));


        assertEquals(200, depositResponse.getStatus());
        JsonNode jsonNode = depositResponse.readEntity(JsonNode.class);
        assertEquals(100l, jsonNode.get("amount").asLong());
        assertEquals("Tom", jsonNode.get("account").get("name").asText());
        assertEquals(100l, jsonNode.get("account").get("balance").asLong());
    }

    @Test
    public void shouldWithdrawFromAccount() {
        Response depositResponse = client.target(UriBuilder.fromUri(tomLocation).path("deposit"))
                .request("application/json")
                .post(Entity.entity("{\t\"amount\": 10000}", MediaType.APPLICATION_JSON_TYPE));

        Response withdrawResponse = client.target(UriBuilder.fromUri(tomLocation).path("withdraw"))
                .request("application/json")
                .post(Entity.entity("{\t\"amount\": 100}", MediaType.APPLICATION_JSON_TYPE));


        assertEquals(200, withdrawResponse.getStatus());
        JsonNode jsonNode = withdrawResponse.readEntity(JsonNode.class);
        assertEquals(100l, jsonNode.get("amount").asLong());
        assertEquals("Tom", jsonNode.get("account").get("name").asText());
        assertEquals(9900l, jsonNode.get("account").get("balance").asLong());
    }
}
