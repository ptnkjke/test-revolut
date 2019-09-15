package revolut.test.service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountCommand {
    private String id;
    private BigDecimal amount;
}
