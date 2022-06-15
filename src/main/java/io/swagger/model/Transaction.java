package io.swagger.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import org.threeten.bp.OffsetDateTime;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Log
public class Transaction   {
  @Id
  @SequenceGenerator(name = "transaction_seq", initialValue = 1)
  @GeneratedValue(generator = "transaction_seq", strategy = GenerationType.SEQUENCE)
  @Column
  private Integer transactionId;
  @ManyToOne
  private User userPerforming;
  private String fromAccount;
  private String toAccount;
  private Double amount;
  private TransactionType transactionType;
  private LocalDate timestamp;


  public Transaction(User userPerforming, String fromAccount, String toAccount, Double amount, TransactionType transactionType, LocalDate timestamp) {
    this.transactionId = transactionId;
    this.userPerforming = userPerforming;
    this.fromAccount = fromAccount;
    this.toAccount = toAccount;
    this.amount = amount;
    this.transactionType = transactionType;
    this.timestamp = timestamp;
  }
}
