package revolut.test.exception;

public class InsufficientAmountException extends RuntimeException {
    private String accountId;

    public InsufficientAmountException(String accountId) {
        this.accountId = accountId;
    }
}
