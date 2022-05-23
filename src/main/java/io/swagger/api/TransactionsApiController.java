package io.swagger.api;

import io.swagger.annotations.Api;
import io.swagger.model.Transaction;
import io.swagger.model.dto.TransactionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.service.TransactionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-05-13T15:15:19.174Z[GMT]")
@RestController
@Api(tags = {"employee, customer, transaction"})
public class TransactionsApiController implements TransactionsApi {

    private static final Logger log = LoggerFactory.getLogger(TransactionsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private TransactionService transactionService;

    @org.springframework.beans.factory.annotation.Autowired
    public TransactionsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Iterable<Transaction>> transactionsGet(
            @NotNull @Parameter(in = ParameterIn.QUERY, description = "fetch transaction from start date" , required=true,schema=@Schema()) @Valid @RequestParam(value = "startDate", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime startDate,
            @NotNull @Parameter(in = ParameterIn.QUERY, description = "fetch transaction till end date" ,required=true,schema=@Schema())
            @Valid @RequestParam(value = "endDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime endDate) {
        // if start date == 0
        // if end date == 0 preset the date
        Iterable<Transaction> transactions = transactionService.getAllTransactions(startDate, endDate);
        return new ResponseEntity<>(transactions, HttpStatus.OK);

    }

    @GetMapping("/accounts/{IBAN}/transactions")
    public ResponseEntity<Iterable<Transaction>> getAllTransactionsFromAccount(@PathVariable(value = "IBAN") String iban) {
            Iterable<Transaction> transactions = transactionService.getAllTransactionsByIBAN(iban);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    public ResponseEntity<Transaction> transactionsPost(
            @Parameter(in = ParameterIn.DEFAULT, description = "", schema=@Schema())
            @Valid @RequestBody TransactionDTO body) {
            // Check weather the userId and account belongs to same user.
        try { // check if account exists + check if amount is greater than zero
            // send if the account enough balance
            Transaction transaction = new Transaction();
            transaction.setAmount(body.getAmount());
            transaction.setSenderIBANAccount(body.getFromAccount());

            LocalDateTime today = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String transactionDate = today.format(formatter);

            LocalDateTime dateTime = LocalDateTime.parse(transactionDate, formatter);
            transaction.setTimestamp(dateTime);
            transaction.setRecieverIBANAccount(body.getToAccount());
            transaction.setTransactionId(1);
            transaction.setTransactionType(body.getTransactionType());
            transaction.setUserPerformingId(2);

            Transaction storeTransaction =  transactionService.createTransaction(transaction);
            return new ResponseEntity<Transaction>(storeTransaction, HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<Transaction>(HttpStatus.BAD_GATEWAY);
        }

    }

}
