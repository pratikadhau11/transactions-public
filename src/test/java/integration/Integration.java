package integration;

import configurations.ApplicationConfiguration;
import configurations.Main;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.jdbi.v3.core.Jdbi;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;

public abstract class Integration {
    JerseyClient client = new JerseyClientBuilder().build();

    @ClassRule
    public static final DropwizardAppRule<ApplicationConfiguration> RULE =
            new DropwizardAppRule<>(Main.class,
                    ResourceHelpers.resourceFilePath("test-application.yml"));

    abstract void testSetup();

    @Before
    public void setUp() throws Exception {
        RULE.getApplication().run("db", "migrate", ResourceHelpers.resourceFilePath("test-application.yml"));
        testSetup();
    }

    @After
    public void tearDown() throws Exception {
        JdbiFactory jdbiFactory = new JdbiFactory();
        Jdbi jdbi = jdbiFactory.build(RULE.getEnvironment(), RULE.getConfiguration().getDataSourceFactory(), "h2");
        Liquibase liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(jdbi.open().getConnection()));
        liquibase.dropAll();
    }
}
