package io.swagger.model.dto;

import io.swagger.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.java.Log;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Log
public class TransactionResponseDTO {

    private Integer transactionId;
    private Integer userPerformingId;
    private String fromAccount;
    private String toAccount;
    private Double amount;
    private String transactionType;
    private LocalDateTime timestamp;

}
