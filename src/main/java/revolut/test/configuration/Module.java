package revolut.test.configuration;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import revolut.test.repository.AccountRepository;
import revolut.test.repository.AccountRepositoryJdbc;

import javax.sql.DataSource;

public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(HttpServer.class).to(HttpServerSpark.class);
        bind(AccountRepository.class).to(AccountRepositoryJdbc.class);
        bind(DataSource.class).toProvider(DataSourceProvider.class);
        bind(Gson.class).toProvider(GsonProvider.class);
    }
}
