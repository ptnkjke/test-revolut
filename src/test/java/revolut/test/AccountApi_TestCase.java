package revolut.test;

import io.restassured.RestAssured;
import org.junit.*;
import revolut.test.core.transaction.TransactionManual;
import revolut.test.entity.Account;
import revolut.test.repository.AccountRepository;
import revolut.test.service.AccountManager;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.equalTo;

public class AccountApi_TestCase {
    private static Context context;
    private AccountRepository accountRepository;
    private TransactionManual transactionManual;
    private AccountManager accountManager;

    @BeforeClass
    public static void setup() {
        RestAssured.port = 8080;
        context = new Context();
        context.start();
    }

    @Before
    public void init() {
        accountRepository = context.get(AccountRepository.class);
        transactionManual = context.get(TransactionManual.class);
        accountManager = context.get(AccountManager.class);

        transactionManual.runInTransaction(() -> {
            accountRepository.clear();
            return null;
        });
    }

    @AfterClass
    public static void after() {
        context.end();
    }

    @Test
    public void createNew() {
        given()
                .body("{\n" +
                        "  \"id\": \"1\",\n" +
                        "  \"amount\": \"1000000\"\n" +
                        "}")
                .when()
                .post("/api/account")
                .then()
                .body("id", equalTo("1"))
                .body("amount", equalTo("1000000"))
                .statusCode(200);


        Account account = transactionManual.runInTransaction(() -> accountRepository.findAccountWithLock("1"));
        Assert.assertNotNull(account);
        Assert.assertEquals("1000000", account.amount().toString());
    }

    @Test
    public void createNewExist() {
        // prepare
        transactionManual.runInTransaction(() -> {
            accountRepository.create(new Account("2", new BigDecimal(100)));

            return null;
        });


        given()
                .body("{\n" +
                        "  \"id\": \"2\",\n" +
                        "  \"amount\": \"1000000\"\n" +
                        "}")
                .when()
                .post("/api/account")
                .then()
                .statusCode(500);
    }

    @Test
    public void getExist() {
        // prepare
        transactionManual.runInTransaction(() -> {
            accountRepository.create(new Account("3", new BigDecimal(100)));

            return null;
        });

        when().get("http://localhost:8080/api/account/3")
                .then()
                .statusCode(200);
    }

    @Test
    public void getList() {
        // prepare
        transactionManual.runInTransaction(() -> {
            accountRepository.create(new Account("3", new BigDecimal(100)));

            return null;
        });

        when().get("http://localhost:8080/api/account")
                .then()
                .statusCode(200)
                .body("[0].id", equalTo("3"));
    }

    @Test
    public void getNotExist() {
        when().get("http://localhost:8080/api/account/not-known")
                .then()
                .statusCode(404);
    }


    @Test
    public void addValue() {
        // prepare
        transactionManual.runInTransaction(() -> {
            accountRepository.create(new Account("1", new BigDecimal(100)));

            return null;
        });

        given()
                .body("{\n" +
                        "  \"amount\": \"500\"\n" +
                        "}")
                .when()
                .post("/api/account/1/add")
                .then()
                .statusCode(200);

        Account a = transactionManual.runInTransaction(() -> accountRepository.getAccount("1"));

        Assert.assertEquals(600, a.amount().intValue());
    }

    @Test
    public void minusValue() {
        // prepare
        transactionManual.runInTransaction(() -> {
            accountRepository.create(new Account("1", new BigDecimal(100)));

            return null;
        });

        given()
                .body("{\n" +
                        "  \"amount\": \"100\"\n" +
                        "}")
                .when()
                .post("/api/account/1/minus")
                .then()
                .statusCode(200);

        Account a = transactionManual.runInTransaction(() -> accountRepository.getAccount("1"));

        Assert.assertEquals(0, a.amount().intValue());
    }

    @Test
    public void transfer1() {
        // prepare
        transactionManual.runInTransaction(() -> {
            accountRepository.create(new Account("4", new BigDecimal(1000)));
            accountRepository.create(new Account("5", new BigDecimal(1000)));
            return null;
        });

        given()
                .body("{\n" +
                        "  \"accountIdFrom\": \"4\",\n" +
                        "  \"accountIdTo\": \"5\",\n" +
                        "  \"amount\": \"500\"\n" +
                        "}")
                .when()
                .post("/api/account/transfer")
                .then()
                .statusCode(200);

        Account a4 = transactionManual.runInTransaction(() -> accountRepository.getAccount("4"));
        Account a5 = transactionManual.runInTransaction(() -> accountRepository.getAccount("5"));

        Assert.assertEquals(1500, a5.amount().intValue());
        Assert.assertEquals(500, a4.amount().intValue());
    }

    @Test
    public void transfer2() {
        // prepare
        transactionManual.runInTransaction(() -> {
            accountRepository.create(new Account("6", new BigDecimal(1000)));
            accountRepository.create(new Account("7", new BigDecimal(1000)));
            return null;
        });

        given()
                .body("{\n" +
                        "  \"accountIdFrom\": \"6\",\n" +
                        "  \"accountIdTo\": \"7\",\n" +
                        "  \"amount\": \"1001\"\n" +
                        "}")
                .when()
                .post("/api/account/transfer")
                .then()
                .statusCode(500);

        Account a6 = transactionManual.runInTransaction(() -> accountRepository.getAccount("6"));
        Account a7 = transactionManual.runInTransaction(() -> accountRepository.getAccount("7"));

        Assert.assertEquals(1000, a6.amount().intValue());
        Assert.assertEquals(1000, a7.amount().intValue());
    }
}
