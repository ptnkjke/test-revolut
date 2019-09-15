package revolut.test.configuration;

import com.google.gson.Gson;
import revolut.test.api.AccountApi;
import revolut.test.api.model.AccountRepresent;
import revolut.test.exception.AccountNotFoundException;
import spark.ResponseTransformer;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class HttpServerSpark implements HttpServer {
    private AccountApi accountApi;
    private DBInit dbInit;
    private Gson gson;

    @Inject
    public HttpServerSpark(AccountApi accountApi, DBInit dbInit, Gson gson) {
        this.accountApi = accountApi;
        this.dbInit = dbInit;
        this.gson = gson;
    }

    @Override
    public void start() {
        dbInit.init();

        port(8080);
        threadPool(100, 5, 100000);

        ResponseTransformer accountRT = new AccountRepresent.ResponseTransformer();
        ResponseTransformer listAccountRT = new AccountRepresent.ListResponseTransformer();

        path("/api", () -> {
            path("/account", () -> {
                post("/transfer", (req, res) -> accountApi.transfer(req, res));
                path("/:id", () -> {
                    get("", (req, res) -> accountApi.getAccountById(req, res), accountRT);
                    post("/add", (req, res) -> accountApi.addValueToAccount(req, res), accountRT);
                    post("/minus", (req, res) -> accountApi.minusValueToAccount(req, res), accountRT);
                });
                post("", (req, res) -> accountApi.createNewAccount(req, res), accountRT);
                get("", (req, res) -> accountApi.list(req, res), listAccountRT);
            });
        });

        exception(RuntimeException.class, (e, request, response) -> {
            response.status(500);

            Map<String, String> result = new HashMap<>();
            result.put("message", e.getMessage());

            response.body(gson.toJson(result));
        });

        exception(AccountNotFoundException.class, (e, request, response) -> {
            response.status(404);

            Map<String, String> result = new HashMap<>();
            result.put("message", e.getMessage());

            response.body(gson.toJson(result));
        });
    }

    @Override
    public void shutDown() {
        stop();
    }
}
