package io.swagger.api;

import io.swagger.annotations.Api;
import io.swagger.jwt.JwtTokenProvider;
import io.swagger.model.*;
import io.swagger.model.dto.TransactionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.dto.TransactionResponseDTO;
import io.swagger.service.AccountService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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
    private AccountService accountService;

    @Autowired
    public TransactionsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<List<Transaction>> transactionsGet(
            @Parameter(in = ParameterIn.QUERY, description = "fetch transaction from start date" , required=true,schema=@Schema()) @Valid @RequestParam(value = "startDate", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd") String startDate,
            @Parameter(in = ParameterIn.QUERY, description = "fetch transaction till end date" ,required=true,schema=@Schema())
            @Valid @RequestParam(value = "endDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate,
            @Valid @RequestParam(value = "page", required = false, defaultValue="0") Integer fromIndex,
            @Valid @RequestParam(value = "limit", required = false, defaultValue = "50") Integer limit) {


        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String username = userAuthentication.getName();
        User user = userService.getUserByUsername(username);

        if(!user.getRoles().contains(Role.ROLE_ADMIN)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not authorized to acces this list");
        }

        List<Transaction> transactions = transactionService.getAllTransactions(startDate, endDate);

        // ask for check if limit or skip is just words
            transactions = transactions.stream()
                    .skip(fromIndex)
                    .limit(limit)
                    .collect(Collectors.toList());

        return new ResponseEntity<>(transactions, HttpStatus.OK);

    }
    public ResponseEntity<TransactionResponseDTO> transactionsPost(
            @Parameter(in = ParameterIn.DEFAULT, description = "", schema=@Schema())
            @Valid @RequestBody TransactionDTO body) throws Exception {

        if (body.getFromAccount() == null ||
                body.getToAccount() == null ||
                body.getTransactionType() == null ||
                body.getAmount() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One of input parameters is null");
        }

        if(body.getFromAccount().equals(body.getToAccount()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "transfer accounts cannot be the same!");

        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String username = userAuthentication.getName();
        User user = userService.getUserByUsername(username);

        Account fromAccount = accountService.findByIBAN(body.getFromAccount());
        Account toAccount = accountService.findByIBAN(body.getToAccount());

        if (!user.getAccounts().contains(fromAccount)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "account not found");
        }

        if (toAccount.getAccountType().equals(AccountType.bank)) {
            body.setTransactionType(TransactionType.deposit.toString());
        }

        if (fromAccount.getAccountType().equals(AccountType.bank) && user.getRoles().equals(Role.ROLE_ADMIN)) {
            body.setTransactionType(TransactionType.withdraw.toString());
        }
        // check if user is admin or user looged
        if(fromAccount.getUser()!= user) {
            if (!user.getRoles().equals(Role.ROLE_ADMIN)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "this account does not belong to you");
            }
        }

        //check if they are the same and both are current account
        if(!fromAccount.getAccountType().equals(AccountType.current) || !toAccount.getAccountType().equals(AccountType.current)) {
            if(fromAccount.getAccountType().equals(AccountType.saving) && toAccount.getAccountType().equals(AccountType.saving)){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "you can send or receive from a saving account to a saving account");
            }
            if(fromAccount.getUser() != toAccount.getUser()){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "You can not send or receive from saving account and current account of different user");
            }
        }
        // senario: getting money from atm
        // senario: putting money to atm
        if (fromAccount.getAccountType().equals(AccountType.bank) && user.getRoles().equals(Role.ROLE_USER)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you cannot not authorized to transfer from the bank");
        }

        if(fromAccount.getCurrentBalance() < body.getAmount()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "insufficient balance! cannot make transaction");
        }

        if (body.getAmount() <= 0.00) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "amount to be transferred needs to be greater than zero");
        }



        Double deductBalanceAfterTransaction = fromAccount.getCurrentBalance() - body.getAmount();
        fromAccount.setCurrentBalance(deductBalanceAfterTransaction);
        accountService.createAccount(fromAccount);

        Double addBalanceAfterTransaction = toAccount.getCurrentBalance() + body.getAmount();
        toAccount.setCurrentBalance(addBalanceAfterTransaction);
        accountService.createAccount(toAccount);


       Transaction storeTransaction = transactionService.createTransaction(username, body);
       TransactionResponseDTO transactionResponseDTO = transactionService.getTransactionResponseDTO(storeTransaction, user, fromAccount);
       return new ResponseEntity<TransactionResponseDTO>(transactionResponseDTO, HttpStatus.OK);
    }

}
