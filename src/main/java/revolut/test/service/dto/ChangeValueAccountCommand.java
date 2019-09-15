package revolut.test.service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChangeValueAccountCommand {
    private BigDecimal amount;

    public ChangeValueAccountCommand() {
    }

    public ChangeValueAccountCommand(BigDecimal amount) {
        this.amount = amount;
    }
}
