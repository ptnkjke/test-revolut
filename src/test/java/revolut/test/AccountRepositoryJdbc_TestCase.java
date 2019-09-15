package revolut.test;

import io.restassured.RestAssured;
import org.junit.*;
import revolut.test.entity.Account;
import revolut.test.repository.AccountRepository;
import revolut.test.core.transaction.TransactionManual;
import revolut.test.service.AccountManager;
import revolut.test.service.dto.ChangeValueAccountCommand;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountRepositoryJdbc_TestCase {
    private Context context;
    private AccountRepository accountRepository;
    private TransactionManual transactionManual;
    private AccountManager accountManager;

    @BeforeClass
    public static void setup() {
        RestAssured.port = 8080;
    }

    @Before
    public void init() {
        context = new Context();
        context.start();

        accountRepository = context.get(AccountRepository.class);
        transactionManual = context.get(TransactionManual.class);
        accountManager = context.get(AccountManager.class);
    }

    @After
    public void after() {
        context.end();
    }

    @Test
    public void test_lock_find() throws InterruptedException {
        transactionManual.runInTransaction(() -> {
            accountRepository.create(new Account("1", new BigDecimal(1000)));

            return null;
        });


        List<String> threadNames = Collections.synchronizedList(new ArrayList<String>());

        // h2 has 1000ms timeout
        Thread t = new Thread(() -> {
            transactionManual.runInTransaction(() -> {
                Account account = accountRepository.findAccountWithLock("1");

                try {

                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                threadNames.add("thread-1");

                return account;
            });

        });

        t.start();

        Thread t2 = new Thread(() -> {
            transactionManual.runInTransaction(() -> {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Account account = accountRepository.findAccountWithLock("1");


                threadNames.add("thread-2");
                return account;
            });

        });
        t2.start();


        t.join();
        t2.join();


        Assert.assertEquals(2, threadNames.size());
        Assert.assertEquals("thread-1", threadNames.get(0));
        Assert.assertEquals("thread-2", threadNames.get(1));
    }

    @Test
    public void test_not_lock_find() throws InterruptedException {
        transactionManual.runInTransaction(() -> {
            accountRepository.create(new Account("1", new BigDecimal(1000)));

            return null;
        });


        List<String> threadNames = Collections.synchronizedList(new ArrayList<String>());

        // h2 has 1000ms timeout
        Thread t = new Thread(() -> {
            transactionManual.runInTransaction(() -> {
                Account account = accountRepository.findAccount("1");

                try {

                    Thread.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                threadNames.add("thread-1");

                return account;
            });

        });

        t.start();

        Thread t2 = new Thread(() -> {
            transactionManual.runInTransaction(() -> {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Account account = accountRepository.findAccount("1");


                threadNames.add("thread-2");
                return account;
            });

        });
        t2.start();


        t.join();
        t2.join();


        Assert.assertEquals(2, threadNames.size());
        Assert.assertEquals("thread-2", threadNames.get(0));
        Assert.assertEquals("thread-1", threadNames.get(1));
    }
}
