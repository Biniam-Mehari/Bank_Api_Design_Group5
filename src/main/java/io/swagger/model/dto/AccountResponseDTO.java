package io.swagger.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.model.Account;
import io.swagger.model.AccountType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-05-13T15:15:19.174Z[GMT]")

@Getter
@Setter
public class AccountResponseDTO {
    @JsonProperty("IBAN")
    private String IBAN = null;

    @JsonProperty("accountType")
    private String accountType = null;

    private Double absoluteLimit = null;

    private Integer accountId = null;

    private Integer userId = null;

    private  Double currentBalance = null;
    private String status = null;



}
