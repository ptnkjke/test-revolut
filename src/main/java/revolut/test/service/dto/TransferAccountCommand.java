package revolut.test.service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferAccountCommand {
    private String accountIdFrom;
    private String accountIdTo;
    private BigDecimal amount;
}
