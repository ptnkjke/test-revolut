package revolut.test.service;

import revolut.test.core.Transformer;
import revolut.test.entity.Account;
import revolut.test.repository.AccountRepository;
import revolut.test.service.dto.ChangeValueAccountCommand;
import revolut.test.service.dto.CreateAccountCommand;
import revolut.test.service.dto.TransferAccountCommand;
import revolut.test.core.transaction.TransactionManual;

import javax.inject.Inject;
import java.util.List;

public class AccountManager {
    private AccountRepository accountRepository;
    private TransactionManual transactionManual;

    @Inject
    public AccountManager(AccountRepository accountRepository, TransactionManual transactionManual) {
        this.accountRepository = accountRepository;
        this.transactionManual = transactionManual;
    }

    public <T> T get(String id, Transformer<Account, T> transformer) {
        Account account = transactionManual
                .runInTransaction(() -> accountRepository.getAccount(id));

        return transformer.transform(account);
    }

    public <T> List<T> list(Transformer<List<Account>, List<T>> transformer) {
        List<Account> result = transactionManual
                .runInTransaction(() -> accountRepository.list());

        return transformer.transform(result);
    }

    public <T> T create(CreateAccountCommand createAccountCommand, Transformer<Account, T> transformer) {
        Account account = transactionManual
                .runInTransaction(() -> {
                            Account acc = new Account(createAccountCommand.getId(), createAccountCommand.getAmount());

                            accountRepository.create(
                                    acc
                            );

                            return acc;
                        }
                );

        return transformer.transform(account);
    }

    public <T> T addValue(String accountId, ChangeValueAccountCommand command, Transformer<Account, T> transformer) {
        Account account = transactionManual
                .runInTransaction(() -> {
                            Account acc = accountRepository.getAccountWithLock(accountId);

                            acc.addAmount(command.getAmount());

                            accountRepository.update(acc);
                            return acc;
                        }
                );

        return transformer.transform(account);
    }

    public <T> T minusValue(String accountId, ChangeValueAccountCommand command, Transformer<Account, T> transformer) {
        Account account = transactionManual
                .runInTransaction(() -> {
                            Account acc = accountRepository.getAccountWithLock(accountId);

                            acc.minusAmount(command.getAmount());

                            accountRepository.update(acc);
                            return acc;
                        }
                );

        return transformer.transform(account);
    }

    public void transfer(TransferAccountCommand transferAccountCommand) {
        transactionManual
                .runInTransaction(() -> {
                            Account accountFrom = accountRepository.getAccountWithLock(transferAccountCommand.getAccountIdFrom());
                            Account accountTo = accountRepository.getAccountWithLock(transferAccountCommand.getAccountIdTo());

                            accountFrom.minusAmount(transferAccountCommand.getAmount());
                            accountTo.addAmount(transferAccountCommand.getAmount());


                            accountRepository.update(accountFrom);
                            accountRepository.update(accountTo);

                            return null;
                        }
                );
    }
}
