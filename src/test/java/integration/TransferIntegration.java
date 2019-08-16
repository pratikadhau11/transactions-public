package integration;

import configurations.ApplicationConfiguration;
import configurations.Main;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.jdbi.v3.core.Jdbi;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

public class TransferIntegration {

    @ClassRule
    public static final DropwizardAppRule<ApplicationConfiguration> RULE =
            new DropwizardAppRule<>(Main.class,
                    ResourceHelpers.resourceFilePath("test-application.yml"));

    String fromId;
    String toId;


    @Before
    public void setUp() throws Exception {
        RULE.getApplication().run("db", "migrate", ResourceHelpers.resourceFilePath("test-application.yml"));
        JerseyClient client = new JerseyClientBuilder().build();
        Response tomResponse = client.target(
                String.format("http://localhost:%s/accounts", RULE.getLocalPort()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity("{\n" +
                        "\t\"name\" : \"Tom\"\n" +
                        "}", MediaType.APPLICATION_JSON));
        String tomLocation = tomResponse.getHeaderString("location");

        client.target(UriBuilder.fromUri(tomLocation).path("deposit"))
                .request("application/json")
                .post(Entity.entity("{\t\"amount\": 10000}", MediaType.APPLICATION_JSON_TYPE));

        fromId = tomLocation.replace(String.format("http://localhost:%s/accounts/", RULE.getLocalPort()), "");


        Response annResponse = client.target(
                String.format("http://localhost:%s/accounts", RULE.getLocalPort()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity("{\n" +
                        "\t\"name\" : \"Ann\"\n" +
                        "}", MediaType.APPLICATION_JSON));

        String annLocation = annResponse.getHeaderString("location");
        client.target(UriBuilder.fromUri(annLocation).path("deposit"))
                .request("application/json")
                .post(Entity.entity("{\t\"amount\": 10000}", MediaType.APPLICATION_JSON_TYPE));

        toId = annLocation.replace(String.format("http://localhost:%s/accounts/", RULE.getLocalPort()), "");
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
    public void shouldTransferAmount() {
        JerseyClient client = new JerseyClientBuilder().build();

        Response response = client.target(
                String.format("http://localhost:%s/accounts/%s/transfer", RULE.getLocalPort(), fromId))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity("{\n" +
                        "\t\"to\" : \""+toId+"\",\n" +
                        "\t\"amount\": 100\n" +
                        "}", MediaType.APPLICATION_JSON));

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("{" +
                "\"from\":\""+fromId+"\"," +
                "\"to\":\""+toId+"\"," +
                "\"amount\":100," +
                "\"account\":{" +
                    "\"id\":\""+fromId+"\"," +
                    "\"name\":\"Tom\"," +
                    "\"balance\":9900" +
                "}" +
            "}", response.readEntity(String.class));
    }

    @Test
    public void shouldNotTransferFromSameAccount() {
        JerseyClient client = new JerseyClientBuilder().build();

        Response response = client.target(
                String.format("http://localhost:%s/accounts/%s/transfer", RULE.getLocalPort(), fromId))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity("{\n" +
                        "\t\"to\" :\""+fromId+"\",\n" +
                        "\t\"amount\": 100\n" +
                        "}", MediaType.APPLICATION_JSON));

        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals("{" +
                "\"code\":400," +
                "\"message\":\"Account number "+fromId+" can not transfer to account number "+fromId+" \"}",
                response.readEntity(String.class));
    }

    @Test
    public void shouldNotTransferNegativeAmount() {
        JerseyClient client = new JerseyClientBuilder().build();

        Response response = client.target(
                String.format("http://localhost:%s/accounts/%s/transfer", RULE.getLocalPort(), fromId))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity("{\n" +
                        "\t\"to\" : \""+toId+"\",\n" +
                        "\t\"amount\": -100\n" +
                        "}", MediaType.APPLICATION_JSON));

        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals("{" +
                        "\"code\":400," +
                        "\"message\":\"Deposit is negative\"}",
                response.readEntity(String.class));
    }

    @Test
    public void shouldNotTransferAmountMoreThanBalance() {
        JerseyClient client = new JerseyClientBuilder().build();

        Response response = client.target(
                String.format("http://localhost:%s/accounts/%s/transfer", RULE.getLocalPort(), fromId))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity("{\n" +
                        "\t\"to\" : \""+toId+"\",\n" +
                        "\t\"amount\": 5000000000\n" +
                        "}", MediaType.APPLICATION_JSON));

        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals("{" +
                        "\"code\":400," +
                        "\"message\":\"Account number "+fromId+" has balance less than 5000000000\"}",
                response.readEntity(String.class));
    }
}
