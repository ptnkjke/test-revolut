package revolut.test.entity;

import revolut.test.exception.InsufficientAmountException;

import java.math.BigDecimal;

public class Account {
    private String id;
    private BigDecimal amount;


    private Account() {
    }

    public Account(String id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public String id() {
        return id;
    }

    public BigDecimal amount() {
        return amount;
    }

    public void minusAmount(BigDecimal delta) {
        this.amount = this.amount.subtract(delta);

        if (this.amount.intValue() < 0) {
            throw new InsufficientAmountException(this.id);
        }
    }

    public void addAmount(BigDecimal delta) {
        this.amount = this.amount.add(delta);

        if (this.amount.intValue() < 0) {
            throw new InsufficientAmountException(this.id);
        }
    }
}
