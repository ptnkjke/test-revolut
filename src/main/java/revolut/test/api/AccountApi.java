package revolut.test.api;

import com.google.gson.Gson;
import revolut.test.api.model.AccountRepresent;
import revolut.test.service.AccountManager;
import revolut.test.service.dto.ChangeValueAccountCommand;
import revolut.test.service.dto.CreateAccountCommand;
import revolut.test.service.dto.TransferAccountCommand;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import java.util.List;

public class AccountApi {
    private AccountRepresent.Transformer transformer = new AccountRepresent.Transformer();
    private AccountRepresent.ListTransformer listTransformer = new AccountRepresent.ListTransformer();


    private AccountManager accountManager;
    private Gson gson;

    @Inject
    public AccountApi(AccountManager accountManager, Gson gson) {
        this.accountManager = accountManager;
        this.gson = gson;
    }

    public AccountRepresent getAccountById(Request request, Response response) {
        response.header("Content-Type", "application/json");
        return accountManager.get(request.params(":id"), transformer);
    }

    public AccountRepresent createNewAccount(Request request, Response response) {
        response.header("Content-Type", "application/json");
        CreateAccountCommand createAccountCommand = gson.fromJson(request.body(), CreateAccountCommand.class);
        return accountManager.create(createAccountCommand, transformer);
    }

    public AccountRepresent addValueToAccount(Request request, Response response) {
        response.header("Content-Type", "application/json");

        ChangeValueAccountCommand command = gson.fromJson(request.body(), ChangeValueAccountCommand.class);
        String accountId = request.params(":id");

        return accountManager.addValue(accountId, command, transformer);
    }

    public AccountRepresent minusValueToAccount(Request request, Response response) {
        response.header("Content-Type", "application/json");

        ChangeValueAccountCommand command = gson.fromJson(request.body(), ChangeValueAccountCommand.class);
        String accountId = request.params(":id");

        return accountManager.minusValue(accountId, command, transformer);
    }

    public Object transfer(Request request, Response response) {
        TransferAccountCommand command = gson.fromJson(request.body(), TransferAccountCommand.class);

        accountManager.transfer(command);

        return "";
    }

    public List<AccountRepresent> list(Request req, Response response) {
        response.header("Content-Type", "application/json");

        return accountManager.list(listTransformer);
    }
}
