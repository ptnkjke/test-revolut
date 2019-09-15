package revolut.test.core.transaction;

import revolut.test.exception.DBException;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class TransactionManual {
    private SessionFactory sessionFactory;

    @Inject
    public TransactionManual(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T runInTransaction(Callable<T> callable) {
        T result = null;

        Connection connection = sessionFactory.openConnection();
        try {
            connection.setAutoCommit(false);

            result = callable.call();

            connection.commit();
        } catch (SQLException e) {
            throw new DBException("sql error:", e);
        } catch (RuntimeException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new DBException("sql error:", e);
            }
            throw e;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new DBException("sql error:", e);
            }
            throw new RuntimeException("error:", e);
        } finally {
            sessionFactory.closeCurrentConnections();
        }

        return result;
    }
}
