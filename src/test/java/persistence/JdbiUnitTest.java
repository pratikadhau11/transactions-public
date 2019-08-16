package persistence;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Environment;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.junit.After;
import org.junit.Before;

public abstract class JdbiUnitTest {

    protected Jdbi dbi;

    private Handle handle;

    private Liquibase liquibase;

    @Before
    public void setUpDatabase() throws Exception {

        Environment environment = new Environment( "test-env", Jackson.newObjectMapper(), null, new MetricRegistry(), null );
        dbi = new JdbiFactory().build( environment, getDataSourceFactory(), "test" );
        handle = dbi.open();
        migrateDatabase();
    }

    @After
    public void tearDown() throws Exception {
        liquibase.dropAll();
    }

    private void migrateDatabase() throws LiquibaseException {
        liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(handle.getConnection()));
        liquibase.update("");
    }

    protected DataSourceFactory getDataSourceFactory()
    {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        dataSourceFactory.setDriverClass( "org.h2.Driver" );
        dataSourceFactory.setUrl( "jdbc:h2:./build/h2db" );
        dataSourceFactory.setUser( "sa" );
        dataSourceFactory.setPassword( "sa" );

        return dataSourceFactory;
    }
}
