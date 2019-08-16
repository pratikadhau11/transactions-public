package configurations;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import persistence.AccountDaoImpl;
import rest.AccountResource;
import rest.TransactionResource;
import service.AccountDao;
import service.AccountService;
import service.TransactionService;

public class Main extends Application<ApplicationConfiguration> {
    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<ApplicationConfiguration> bootstrap) {
        // nothing to do yet
        bootstrap.addBundle(new MigrationsBundle<ApplicationConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ApplicationConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(ApplicationConfiguration configuration,
                    Environment environment) {

        JdbiFactory jdbiFactory = new JdbiFactory();
        Jdbi jdbi = jdbiFactory.build(environment, configuration.getDataSourceFactory(), "h2");

        AccountDao accountDao = new AccountDaoImpl(jdbi);
        AccountService accountService = new AccountService(accountDao);
        final AccountResource accountResource = new AccountResource(accountService);
        environment.jersey().register(accountResource);

        TransactionService transactionService = new TransactionService(accountService);
        final TransactionResource transactionResource = new TransactionResource(transactionService);
        environment.jersey().register(transactionResource);
    }

}
