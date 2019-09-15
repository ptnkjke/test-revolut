package revolut.test.core.transaction;

import org.apache.commons.dbutils.DbUtils;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SessionFactory {
    private DataSource dataSource;
    private static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    @Inject
    public SessionFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection openConnection() {
        Connection connection = connectionThreadLocal.get();
        if (connection != null) {
            return connection;
        }

        connection = createNew();
        connectionThreadLocal.set(connection);

        return connection;
    }

    public Connection getCurrentConnection() {
        return connectionThreadLocal.get();
    }

    public void closeCurrentConnections() {
        try {
            DbUtils.close(connectionThreadLocal.get());
        } catch (SQLException e) {
            throw new RuntimeException("exception", e);
        } finally {
            connectionThreadLocal.set(null);
        }
    }

    private Connection createNew() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("oops, something was wrong..");
        }
    }

}
