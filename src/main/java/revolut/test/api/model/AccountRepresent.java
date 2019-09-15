package revolut.test.api.model;

import com.google.gson.Gson;
import lombok.Data;
import revolut.test.entity.Account;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class AccountRepresent {
    private String id;
    private String amount;

    protected AccountRepresent() {
    }

    public AccountRepresent(String id, String amount) {
        this.id = id;
        this.amount = amount;
    }

    public static class Transformer implements revolut.test.core.Transformer<Account, revolut.test.api.model.AccountRepresent> {

        @Override
        public AccountRepresent transform(Account object) {
            return new AccountRepresent(object.id(), object.amount().toString());
        }
    }

    public static class ListTransformer implements revolut.test.core.Transformer<List<Account>, List<revolut.test.api.model.AccountRepresent>> {
        private Transformer transformer = new Transformer();

        @Override
        public List<AccountRepresent> transform(List<Account> object) {
            return object.stream().map(o -> transformer.transform(o)).collect(Collectors.toList());
        }
    }

    public static class ResponseTransformer implements spark.ResponseTransformer {
        private static final Gson GSON = new Gson();

        @Override
        public String render(Object o) {
            AccountRepresent account = (AccountRepresent) o;

            return GSON.toJson(account);
        }
    }

    public static class ListResponseTransformer implements spark.ResponseTransformer {
        private static final Gson GSON = new Gson();

        @Override
        public String render(Object o) {
            List<AccountRepresent> list = (List<AccountRepresent>) o;

            return GSON.toJson(list);
        }
    }
}
