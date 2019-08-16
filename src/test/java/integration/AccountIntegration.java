package integration;

import com.fasterxml.jackson.databind.JsonNode;
import configurations.ApplicationConfiguration;
import configurations.Main;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.jdbi.v3.core.Jdbi;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import static org.junit.Assert.assertEquals;

public class AccountIntegration {
    @ClassRule
    public static final DropwizardAppRule<ApplicationConfiguration> RULE =
            new DropwizardAppRule<>(Main.class,
                    ResourceHelpers.resourceFilePath("test-application.yml"));
    JerseyClient client = new JerseyClientBuilder().build();

    String tomLocation;
    @Before
    public void setUp() throws Exception {
        RULE.getApplication().run("db", "migrate", ResourceHelpers.resourceFilePath("test-application.yml"));

        Response tomCreated = client.target(
                String.format("http://localhost:%s/accounts", RULE.getLocalPort()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity("{\n" +
                        "\t\"name\" : \"Tom\"\n" +
                        "}", MediaType.APPLICATION_JSON));

        assertEquals(201, tomCreated.getStatus());

        tomLocation = tomCreated.getHeaderString("location");
    }

    @After
    public void tearDown() throws Exception {
        JdbiFactory jdbiFactory = new JdbiFactory();
        Jdbi jdbi = jdbiFactory.build(RULE.getEnvironment(), RULE.getConfiguration().getDataSourceFactory(), "h2");
        jdbi.withHandle(handle -> {
            handle.execute("DROP TABLE ACCOUNTS;");
            handle.execute("DROP TABLE DATABASECHANGELOG;");
            return 1;
        });
    }

    @Test
    public void shouldCreateAccount() {
        Response tomResponse = client.target(tomLocation)
                .request()
                .get();

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
