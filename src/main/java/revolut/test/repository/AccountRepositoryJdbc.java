package revolut.test.repository;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import revolut.test.entity.Account;
import revolut.test.exception.AccountNotFoundException;
import revolut.test.exception.DBException;
import revolut.test.core.transaction.SessionFactory;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AccountRepositoryJdbc implements AccountRepository {
    private SessionFactory sessionFactory;

    @Inject
    public AccountRepositoryJdbc(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Account> list() {
        Connection connection = sessionFactory.getCurrentConnection();

        QueryRunner queryRunner = new QueryRunner();

        try {
            return queryRunner.execute(
                    connection,
                    "select * from account",
                    resultSet -> {
                        if (resultSet.next()) {
                            return new Account(
                                    resultSet.getString("id"),
                                    resultSet.getBigDecimal("amount")
                            );
                        }

                        return null;
                    });
        } catch (SQLException e) {
            throw new DBException("sql exception", e);
        }
    }

    @Override
    public Account getAccount(String id) {
        Account account = findAccount(id);

        if (account == null) {
            throw new AccountNotFoundException(id);
        }

        return account;
    }

    @Override
    public Account findAccount(String id) {
        Connection connection = sessionFactory.getCurrentConnection();

        QueryRunner queryRunner = new QueryRunner();

        try {
            List<Account> result = queryRunner.execute(
                    connection,
                    "select * from account where id = ?",
                    resultSet -> {
                        if (resultSet.next()) {
                            return new Account(
                                    resultSet.getString("id"),
                                    resultSet.getBigDecimal("amount")
                            );
                        }

                        return null;
                    },
                    id);

            if (result.isEmpty()) {
                return null;
            }

            return result.get(0);
        } catch (SQLException e) {
            throw new DBException("sql exception", e);
        }
    }

    @Override
    public Account getAccountWithLock(String id) {
        Account account = findAccountWithLock(id);

        if (account == null) {
            throw new AccountNotFoundException(id);
        }

        return account;
    }

    @Override
    public Account findAccountWithLock(String id) {
        Connection connection = sessionFactory.getCurrentConnection();

        QueryRunner queryRunner = new QueryRunner();

        try {
            List<Account> result = queryRunner.execute(
                    connection,
                    "select * from account where id = ? for update",
                    resultSet -> {
                        if (resultSet.next()) {
                            return new Account(
                                    resultSet.getString("id"),
                                    resultSet.getBigDecimal("amount")
                            );
                        }

                        return null;
                    },
                    id);

            if (result.isEmpty()) {
                return null;
            }

            return result.get(0);
        } catch (SQLException e) {
            throw new DBException("sql exception", e);
        }
    }

    public void create(Account account) {
        Connection connection = sessionFactory.getCurrentConnection();

        QueryRunner queryRunner = new QueryRunner();
        try {
            queryRunner.insert(connection, "INSERT into account (id, amount) values(?,?)", (ResultSetHandler<Account>) resultSet -> null, account.id(), account.amount());
        } catch (SQLException e) {
            throw new DBException("sql exception", e);
        }
    }

    @Override
    public void update(Account account) {
        Connection connection = sessionFactory.getCurrentConnection();

        QueryRunner queryRunner = new QueryRunner();
        try {
            queryRunner.insert(connection, "UPDATE account set amount = ? where id = ?", (ResultSetHandler<Account>) resultSet -> null, account.amount(), account.id());
        } catch (SQLException e) {
            throw new DBException("sql exception when update", e);
        }
    }

    @Override
    public void clear() {
        Connection connection = sessionFactory.getCurrentConnection();

        QueryRunner queryRunner = new QueryRunner();
        try {
            queryRunner.update(connection, "Delete from account");
        } catch (SQLException e) {
            throw new DBException("sql exception when clear", e);
        }
    }
}
