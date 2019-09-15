package revolut.test.exception;

public class AccountNotFoundException extends RuntimeException {
    private String id;

    public AccountNotFoundException(String id) {
        super(String.format("Account with id=[%s] is not found", id));
        this.id = id;
    }

    public String id() {
        return id;
    }
}
