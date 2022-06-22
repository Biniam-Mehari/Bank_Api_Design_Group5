package io.swagger.api;

import io.swagger.annotations.Api;
import io.swagger.model.*;
import io.swagger.model.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.service.AccountService;
import io.swagger.service.TransactionService;
import io.swagger.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-05-13T15:15:19.174Z[GMT]")
@RestController
@Api(tags = {"employee", "customer", "transaction"})

public class AccountsApiController implements AccountsApi {

    private static final Logger log = LoggerFactory.getLogger(AccountsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;


    @Autowired
    public AccountsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> closeAccount(@Size(min = 18, max = 18) @Parameter(in = ParameterIn.PATH, description = "IBAN of a user", required = true, schema = @Schema()) @PathVariable("IBAN") String IBAN) {


        if(!validateIBAN(IBAN)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid IBAN");
        }
        //receiving the account form database
       Account account = accountService.findByIBAN(IBAN);


        if (account.getAccountId()==1){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You can not close the Bank's account");
        }
            if(account.getAccountType().equals(AccountType.current)){
                List<Account> savingAccounts = accountService.findAllByUserAndAccountType(account.getUser(),AccountType.saving);
                if(savingAccounts.isEmpty()){
                    accountService.closeAccount(account);
                }else {
                    throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "You can not close your currect account if you have a saving account.");
                }
            }
            else {
                accountService.closeAccount(account);
            }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<AccountResponseDTO> accountsIBANGet(@Size(min = 18, max = 18) @Parameter(in = ParameterIn.PATH, description = "IBAN of a user", required = true, schema = @Schema()) @PathVariable("IBAN") String IBAN) {
        // getes the data of a user from the token
        User user = loggedInUser();

        //receiving the account form database

        if(!validateIBAN(IBAN)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid IBAN");
        }
       Account account = accountService.findByIBAN(IBAN);

        //check if the user is owner of the account or admin(employee)
        if(user.getRoles().contains(Role.ROLE_ADMIN) || user.getAccounts().contains(account)){
            return new ResponseEntity<AccountResponseDTO>(changeAccoutToAccountResponseDTO(account), HttpStatus.OK);
        }
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You dont have authorization to get this account information");

    }


    public ResponseEntity<List<TransactionResponseDTO>> accountsIBANTransactionsGet(
            @Parameter(in = ParameterIn.PATH, description = "Numeric ID of the user to get", required = true, schema = @Schema()) @PathVariable("IBAN") String IBAN,
            @NotNull @Parameter(in = ParameterIn.QUERY, description = "fetch transaction from start date", required = true, schema = @Schema()) @Valid @RequestParam(value = "startDate", required = true) String startDate,
            @NotNull @Parameter(in = ParameterIn.QUERY, description = "fetch transaction till end date", required = true, schema = @Schema()) @Valid @RequestParam(value = "endDate", required = true) String endDate,
            @NotNull @Parameter(in = ParameterIn.QUERY, description = "skip Value", required = true, schema = @Schema())
            @Valid @RequestParam(value = "skip", required = true) Integer skipValue,
            @NotNull @Parameter(in = ParameterIn.QUERY, description = "limit Value", required = true, schema = @Schema())
            @Valid @RequestParam(value = "limit", required = true) Integer limitValue) {

        // getes the data of a user from the token
        User user = loggedInUser();

        if (startDate.equals(null) || endDate.equals(null)) {
            if (startDate.equals(null) && endDate.equals(null)) {
                LocalDate startdate = LocalDate.now();
                LocalDate enddate = LocalDate.now();
            }
            else {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "date range must be specified");
            }
        }
        //receiving the account form database

        if(!validateIBAN(IBAN)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid IBAN");
        }
       Account account = accountService.findByIBAN(IBAN);

        if(user != account.getUser()) {
            if (!user.getRoles().contains(Role.ROLE_ADMIN)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not authorized to see this transaction");
            }
        }
        LocalDateTime startdate;
        LocalDateTime enddate;
        try {
            startdate = LocalDateTime.parse(startDate);
            enddate = LocalDateTime.parse(endDate);
        }
        catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid date format, needs to be in yyyy-MM-ddTHH:mm:ss");
        }

        List<Transaction> transactions = transactionService.
                findAllTransactionsByIBANAccount(IBAN, startdate, enddate, skipValue, limitValue);

        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        for (Transaction tr : transactions) {
            TransactionResponseDTO dtos = convertTransactionEntityToTransactionResponseDTO(tr);
            transactionResponseDTOS.add(dtos);
        }
        return new ResponseEntity<List<TransactionResponseDTO>>(transactionResponseDTOS, HttpStatus.OK);
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

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponseDTO> createAccount(@Parameter(in = ParameterIn.DEFAULT, description = "New account details", schema = @Schema()) @Valid @RequestBody AccountDTO body) {

        //initializing object of AccountResponseDto
        AccountResponseDTO accountResponseDTO;


            //check if user exist
            User userToCreatAccount = userService.getUserModelById(body.getUserId());
            if(userToCreatAccount==null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user does not exist");
            }
            accountResponseDTO = checkAndCreateAccount(userToCreatAccount , body);



        return new ResponseEntity<AccountResponseDTO>(accountResponseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponseDTO>> getAccounts( @Parameter(in = ParameterIn.QUERY, description = "skip the needed amount of accounts", required = false, schema = @Schema()) @Valid @RequestParam(value = "skip", required = false) Integer skip,
                                                                 @Parameter(in = ParameterIn.QUERY, description = "fetch the needed amount of accounts", required = false, schema = @Schema()) @Valid @RequestParam(value = "limit", required = false) Integer limit) {


        //default value for skip and limit
        if(skip==0 || skip==null){
            //it alwasy skip the first account which is bank account
            skip=1;
        }
        if(limit==null || limit==0){
            //it alwasy skip the first account which is bank account
            limit=10;
        }
        List<Account> accounts = accountService.getAllAccountsInsideSkipAndLimit(skip,limit);
        List<AccountResponseDTO> accountResponseDTOS = new ArrayList<>();
        for (Account account: accounts){
          AccountResponseDTO accountResponseDTO = changeAccoutToAccountResponseDTO(account);
            accountResponseDTOS.add(accountResponseDTO);
        }

        return new ResponseEntity<List<AccountResponseDTO>>(accountResponseDTOS,HttpStatus.OK);
    }


    private  boolean checkifCurrentAccountExist(User user) {
        //check if the user have already a current account
        Account currentAccount = accountService.findByUserAndAccountType(user, AccountType.current);
        if (currentAccount != null) {
            return true;
        }
        return false;
    }

    private User loggedInUser(){
        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String username = userAuthentication.getName();
        return userService.getUserByUsername(username);
    }

    private AccountResponseDTO checkAndCreateAccount(User user ,AccountDTO body){
       // initialize object account
        Account account = new Account();
        String newIban;
        do {
             newIban = account.generateIBAN();
        }while (accountService.findByIBAN(newIban) != null);

        account.setIBAN(newIban);
        account.setUser(userService.getUserModelById(body.getUserId()));

        if(!body.getAccountType().toLowerCase().equals(AccountType.current.toString()) && !body.getAccountType().toLowerCase().equals(AccountType.saving.toString())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "accounts can be type: current or saving");
        }
        account.setAccountType(AccountType.valueOf(body.getAccountType().toLowerCase()));
        if(body.getAccountType().toLowerCase().equals(AccountType.current.toString())){
            // a user can have 1 current account a maltiple saving account
            if(checkifCurrentAccountExist(user)){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have current account");
            }
            account = accountService.saveAccount(account);
        }
        else{
            List<Account> accounts = accountService.findAllByUserAndAccountType(user,AccountType.current);
            if (!accounts.isEmpty()){
                account = accountService.saveAccount(account);
            }
            else
                throw new ResponseStatusException(HttpStatus.CONFLICT, "To make saving account first you need to make current account");
        }
        Account accountRegistered = accountService.findByIBAN(account.getIBAN());

        return changeAccoutToAccountResponseDTO(accountRegistered);
    }

    private AccountResponseDTO changeAccoutToAccountResponseDTO(Account accountRegistered){

        AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
        accountResponseDTO.setIBAN(accountRegistered.getIBAN());
        accountResponseDTO.setAccountType(accountRegistered.getAccountType().toString());
        accountResponseDTO.setAccountId(accountRegistered.getAccountId());
        accountResponseDTO.setAbsoluteLimit(accountRegistered.getAbsoluteLimit());
        accountResponseDTO.setUserId(accountRegistered.getUser().getUserId());
        accountResponseDTO.setCurrentBalance(accountRegistered.getCurrentBalance());
        accountResponseDTO.setStatus(accountRegistered.getStatus());
        return accountResponseDTO;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateAbsoluteLimitPost(@Size(min=18,max=18) @Parameter(in = ParameterIn.PATH, description = "IBAN of a user", required=true, schema=@Schema()) @PathVariable("IBAN") String IBAN, @Valid @RequestBody AbsoluteLimitDTO body) {


        if(!validateIBAN(IBAN)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid IBAN");
        }
        Account account = accountService.findByIBAN(IBAN);
        account.setAbsoluteLimit(body.getAbsoluteLimit());

        accountService.saveAccount(account);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<List<TransactionResponseDTO>> accountsIBANTransactionsByAmountGet(
            @Parameter(in = ParameterIn.PATH, description = "Numeric ID of the user to get", required=true, schema=@Schema())
            @PathVariable("IBAN") String IBAN, @NotNull @Parameter(in = ParameterIn.QUERY, description = "fetch transaction by amount" ,required=true,schema=@Schema())
            @Valid @RequestParam(value = "amount", required = true) Double amount,
            @NotNull @Parameter(in = ParameterIn.QUERY, description = "enter operator [<, =, >]" ,required=true,schema=@Schema())
            @Valid @RequestParam(value = "operator", required = true) String operator,
            @Valid @RequestParam(value = "skip", required = true) Integer skipValue,
            @Valid @RequestParam(value = "limit", required = true) Integer limitValue) {

        User user = loggedInUser();


        if(!validateIBAN(IBAN)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid IBAN");
        }
        Account account = accountService.findByIBAN(IBAN);

        if (account == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");

        if(user != account.getUser()) {
            if (!user.getRoles().contains(Role.ROLE_ADMIN)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not authorized to see this transaction");
            }
        }
        List<Transaction> transactions = new ArrayList<>();

        if (operator.equals("<")) {
            transactions.addAll(transactionService.findAllTransactionsLessThanAmount(skipValue, limitValue, account.getIBAN(), amount));
        }
        else if (operator.equals(">")) {
            transactions.addAll(transactionService.findAllTransactionsGreaterThanAmount(skipValue, limitValue, account.getIBAN(), amount));
        }
        else if (operator.equals("=")) {
            transactions.addAll(transactionService.findAllTransactionEqualToAmount(skipValue, limitValue, account.getIBAN(), amount));
        }

        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        for (Transaction transaction: transactions) {
            TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(transaction);
            transactionResponseDTOS.add(transactionResponseDTO);
        }
        return new ResponseEntity<List<TransactionResponseDTO>>(transactionResponseDTOS, HttpStatus.OK);
    }

    public ResponseEntity<List<TransactionResponseDTO>> getTransactionByToOrFromAccount(
            @Parameter(in = ParameterIn.PATH, description = "Numeric ID of the user to get", required = true, schema = @Schema())
            @PathVariable("IBAN") String IBAN,
            @NotNull @Parameter(in = ParameterIn.QUERY, description = "enter 'to' or 'from'", required = true, schema = @Schema())
            @Valid @RequestParam(value = "operator", required = true) String accountValue,
            @Valid @RequestParam(value = "skip", required = true) Integer skipValue,
            @Valid @RequestParam(value = "limit", required = true) Integer limitValue) {

      User user = loggedInUser();


        if(!validateIBAN(IBAN)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid IBAN");
        }
        Account  userAccount = accountService.findByIBAN(IBAN);
        userAccount.setIBAN(IBAN);

        if (userAccount == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");

        if(user != userAccount.getUser()) {
            if (!user.getRoles().contains(Role.ROLE_ADMIN)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not authorized to see this transaction");
            }
        }

        List<Transaction> transactions = new ArrayList<>();

        if (accountValue.equals("from")) {
            transactions.addAll(transactionService.findAllTransactionsByFromAccount(userAccount.getIBAN(), skipValue,limitValue));
        }
        else if (accountValue.equals("to")) {
            transactions.addAll(transactionService.findAllTransactionByToAccount(userAccount.getIBAN(), skipValue, limitValue));
        }
        else if (accountValue.equals("all")) {
            transactions.addAll(transactionService.findAllTransactionsByFromAccount(userAccount.getIBAN(), skipValue, limitValue));
            transactions.addAll(transactionService.findAllTransactionByToAccount(userAccount.getIBAN(), skipValue, limitValue));
        }

        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        for (Transaction transaction: transactions) {
            TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(transaction);
            transactionResponseDTOS.add(transactionResponseDTO);
        }

        return new ResponseEntity<List<TransactionResponseDTO>>(transactionResponseDTOS, HttpStatus.OK);
    }

    //check valid iban
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

// todo: check the swaggerui contain some end point that we didnt add