package io.swagger.api;

import io.swagger.annotations.Api;
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

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    public ResponseEntity<List<TransactionResponseDTO>> transactionsGet(
            @Parameter(in = ParameterIn.QUERY, description = "fetch transaction from start date" , required=true,schema=@Schema()) @Valid @RequestParam(value = "startDate", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String startDate,
            @Parameter(in = ParameterIn.QUERY, description = "fetch transaction till end date" ,required=true,schema=@Schema())
            @Valid @RequestParam(value = "endDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate,
            @Valid @RequestParam(value = "skip", required = false) Integer skipValue,
            @Valid @RequestParam(value = "limit", required = false) Integer limit) {

        User user = loggedInUser();
        if(!user.getRoles().contains(Role.ROLE_ADMIN))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not authorized to access this list");

        LocalDateTime startdate;
        LocalDateTime enddate;

        try {
            LocalDate fromdate = LocalDate.parse(startDate);
            LocalDate todate = LocalDate.parse(endDate);
            startdate = LocalDateTime.of(fromdate, LocalTime.MIN);
            enddate = LocalDateTime.of(todate, LocalTime.MAX);
        }
        catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "date needs to be in format yyyy-MM-dd");
        }

        if (skipValue == null && limit == null) {
            skipValue = 0;
            limit = 10;
        }

       List<Transaction> transactions = transactionService.getAllTransactions(skipValue, limit, startdate, enddate);
       List<TransactionResponseDTO> transactionResponseDTOList = new ArrayList<>();

        for (Transaction transaction: transactions) {
            TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(transaction);
            transactionResponseDTOList.add(transactionResponseDTO);
        }
        return new ResponseEntity<List<TransactionResponseDTO>>(transactionResponseDTOList, HttpStatus.OK);
    }

    // creates transaction
    public ResponseEntity<TransactionResponseDTO> transactionsPost(
            @Parameter(in = ParameterIn.DEFAULT, description = "", schema=@Schema())
            @Valid @RequestBody TransactionDTO body) throws Exception {

        if (body.getFromAccount().equals(null) ||
                body.getToAccount().equals(null) ||
                body.getTransactionType().equals(null) ||
                body.getAmount().equals(null)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One of input parameters is null");
        }

        if(body.getFromAccount().equals(body.getToAccount()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "transfer accounts cannot be the same!");

        //gets information of user loogedin
        User user = loggedInUser();

        validateFromAccountAndToAccount(body, user);

        Transaction createTransaction = convertTransactionDtoToTransactionEntity(user, body);

        Transaction storeTransaction = transactionService.createTransaction(createTransaction);

       TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(storeTransaction);
       return new ResponseEntity<TransactionResponseDTO>(transactionResponseDTO, HttpStatus.OK);
    }

    public void validateFromAccountAndToAccount(TransactionDTO dto, User user) {

        if (!validateIBAN(dto.getFromAccount()))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid IBAN format");

        if (!validateIBAN(dto.getToAccount()))
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid IBAN format");

        Account fromAccount = accountService.findByIBAN(dto.getFromAccount());
        Account toAccount = accountService.findByIBAN(dto.getToAccount());

        if (fromAccount == null || toAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "account not found");

        if (fromAccount.getUser() != user) {
            if (!(dto.getTransactionType().equals("deposit") && fromAccount.getAccountId().equals(1))){
                if (!user.getRoles().contains(Role.ROLE_ADMIN)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "this account does not belong to you");
                }
            }
        }

        if(!fromAccount.getAccountType().equals(AccountType.current) || !toAccount.getAccountType().equals(AccountType.current)) {
            if(fromAccount.getAccountType().equals(AccountType.saving) && toAccount.getAccountType().equals(AccountType.saving)){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "you cannot send or receive from a saving account to a saving account");
            }
            if(fromAccount.getUser() != toAccount.getUser()){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "You cannot send or receive from saving account and current account of different user");
            }
        }

        deductMoneyFromAccountAndUpdateBalance(fromAccount, dto, user);

        addMoneyToAccountAndUpdateBalance(toAccount, dto.getAmount());
    }

    public void deductMoneyFromAccountAndUpdateBalance(Account fromAccount, TransactionDTO transactionDTO, User user) {
        LocalDate date = LocalDate.now();
        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);
        Double dayLimitUsed = transactionService.getAmountTranferdPerDay(fromAccount.getIBAN(),startOfDay,endOfDay);
        if(dayLimitUsed != null){
            double totalAmountTranferWithTheCurrent = transactionDTO.getAmount() + dayLimitUsed;
            if (totalAmountTranferWithTheCurrent > user.getDayLimit()) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "cannot transfer funds! you have exceed your day limit");
            }
        }


        if (transactionDTO.getAmount() > user.getTransactionLimit()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "cannot transfer funds! you have exceed your transaction limit");
        }

        if (transactionDTO.getAmount() <= 0.00) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "amount to be transfered needs to be greater than zero");
        }

        //deduct balance
        Double deductBalanceAfterTransaction = fromAccount.getCurrentBalance() - transactionDTO.getAmount();

        //check absolute limit
        if(deductBalanceAfterTransaction < fromAccount.getAbsoluteLimit()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "you have exceeded your absolute limit!");
        }
        fromAccount.setCurrentBalance(deductBalanceAfterTransaction);
        accountService.saveAccount(fromAccount);
    }

    public void addMoneyToAccountAndUpdateBalance(Account toAccount, double amount) {
        Double addBalanceAfterTransaction = toAccount.getCurrentBalance() + amount;
        toAccount.setCurrentBalance(addBalanceAfterTransaction);
        accountService.saveAccount(toAccount);
    }

    private User loggedInUser(){
        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String username = userAuthentication.getName();
        return userService.getUserByUsername(username);
    }

    public Transaction convertTransactionDtoToTransactionEntity(User user, TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.valueOf(transactionDTO.getTransactionType().toLowerCase()));
        transaction.setFromAccount(transactionDTO.getFromAccount());
        transaction.setToAccount(transactionDTO.getToAccount());
        transaction.setAmount(transactionDTO.getAmount());

        LocalDateTime todayDatetime = LocalDateTime.now();
        transaction.setTimestamp(todayDatetime.truncatedTo(ChronoUnit.SECONDS));
        transaction.setUserPerforming(user);
        return transaction;
    }

    public TransactionResponseDTO convertTransactionEntityToTransactionResponseDTO(Transaction storeTransaction) {

        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        transactionResponseDTO.setTransactionId(storeTransaction.getTransactionId());
        transactionResponseDTO.setUserPerformingId(storeTransaction.getUserPerforming().getUserId());
        transactionResponseDTO.setFromAccount(storeTransaction.getFromAccount());
        transactionResponseDTO.setToAccount(storeTransaction.getToAccount());
        transactionResponseDTO.setAmount(storeTransaction.getAmount());
        transactionResponseDTO.setTransactionType(storeTransaction.getTransactionType().toString());
        transactionResponseDTO.setTimestamp(storeTransaction.getTimestamp());
        return transactionResponseDTO;
    }

    public boolean validateIBAN(String IBAN){
        // validate IBAN for correct format
        if(IBAN.substring(0,2).equals("NL") && IBAN.substring(2,4).matches("[0-9]+") && IBAN.substring(4,8).equals("INHO") && IBAN.substring(8,18).matches("[0-9]+")){
            return true;
        }
        else{
            return false;
        }
    }





}
