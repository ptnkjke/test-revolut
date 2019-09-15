package revolut.test.configuration;

import com.google.inject.Provider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

class DataSourceProvider implements Provider<DataSource> {
    private HikariConfig createConfig() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:h2:mem:testdb;MVCC=true");
        config.setUsername("sa");
        config.setMaximumPoolSize(20);

        return config;
    }

    @Override
    public DataSource get() {
        return new HikariDataSource(createConfig());
    }
}
