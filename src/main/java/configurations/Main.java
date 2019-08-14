package configurations;

import hellow.HelloWorldResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import persistence.AccountDaoImpl;
import persistence.TransactionDaoImpl;
import rest.AccountResource;
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

        final HelloWorldResource resource = new HelloWorldResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );


        AccountDao accountDao = new AccountDaoImpl(jdbi);
        AccountService accountService = new AccountService(accountDao);


        TransactionService transactionService = new TransactionService(accountService);
        final AccountResource accountResource = new AccountResource(accountService, transactionService);
        environment.jersey().register(resource);
        environment.jersey().register(accountResource);
    }

}
