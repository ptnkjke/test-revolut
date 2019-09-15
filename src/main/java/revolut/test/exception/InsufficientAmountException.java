package revolut.test.exception;

public class InsufficientAmountException extends RuntimeException {
    private String accountId;

    public InsufficientAmountException(String accountId) {
        super(String.format("account [%s] has insufficient amount for this operation", accountId));
        this.accountId = accountId;
    }
}
