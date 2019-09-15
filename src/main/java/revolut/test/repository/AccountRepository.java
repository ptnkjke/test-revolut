package revolut.test.repository;

import revolut.test.entity.Account;

import java.util.List;

public interface AccountRepository {
    List<Account> list();

    Account getAccount(String id);

    Account findAccount(String id);

    Account getAccountWithLock(String id);

    Account findAccountWithLock(String id);

    void create(Account account);

    void update(Account account);

    void clear();
}
