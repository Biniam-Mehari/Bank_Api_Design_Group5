package io.swagger.api;

import io.swagger.annotations.Api;
import io.swagger.jwt.JwtTokenProvider;
import io.swagger.model.Transaction;
import io.swagger.model.User;
import io.swagger.model.dto.TransactionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.service.TransactionService;
import io.swagger.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-05-13T15:15:19.174Z[GMT]")
@RestController
@Api(tags = {"employee", "customer", "transaction"})
public class TransactionsApiController implements TransactionsApi {

    private static final Logger log = LoggerFactory.getLogger(TransactionsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    public TransactionsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Iterable<Transaction>> transactionsGet(
            @Parameter(in = ParameterIn.QUERY, description = "fetch transaction from start date" , required=true,schema=@Schema()) @Valid @RequestParam(value = "startDate", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") String startDate,
            @Parameter(in = ParameterIn.QUERY, description = "fetch transaction till end date" ,required=true,schema=@Schema())
            @Valid @RequestParam(value = "endDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") String endDate,
            @Valid @RequestParam(value = "page", required = false, defaultValue="0") Integer fromIndex,
            @Valid @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit) {

        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String username = userAuthentication.getName();

        List<Transaction> transactions = transactionService.
                getAllTransactions(username, startDate, endDate, fromIndex, limit);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }


    public ResponseEntity<Transaction> transactionsPost(
            @Parameter(in = ParameterIn.DEFAULT, description = "", schema=@Schema())
            @Valid @RequestBody TransactionDTO body) throws Exception {

        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String username = userAuthentication.getName();

        if (body.getFromAccount() == null ||
                body.getToAccount() == null ||
                body.getTransactionType() == null ||
                body.getAmount() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One of input parameters is null");
        }
        Transaction storeTransaction = transactionService.createTransaction(username, body);
        return new ResponseEntity<Transaction>(storeTransaction, HttpStatus.OK);
    }

}
