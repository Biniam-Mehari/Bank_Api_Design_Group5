package io.swagger.service;

import io.swagger.model.*;
import io.swagger.model.dto.TransactionDTO;
import io.swagger.model.dto.TransactionResponseDTO;
import io.swagger.repository.AccountRepository;
import io.swagger.repository.TransactionRepository;
import io.swagger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;


    public List<Transaction> getAllTransactions(Integer skip, Integer limit, LocalDateTime startdate, LocalDateTime enddate) {

        return transactionRepository.findAllByTimestampBetween(startdate, enddate, skip, limit);

    }

    public Transaction createTransaction2(Transaction transaction) {
        return transactionRepository.save(transaction);
    }



    public Transaction createTransaction(User user, TransactionDTO body) {

        Account fromAccount = new Account();
        Account toAccount = new Account();


        if (!fromAccount.validateIBAN(body.getFromAccount())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid IBAN format");
        }

        if (!toAccount.validateIBAN(body.getToAccount())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid IBAN format");
        }

        fromAccount = accountRepository.findByIBAN(body.getFromAccount());
        toAccount = accountRepository.findByIBAN(body.getToAccount());

        if (fromAccount == null || toAccount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "account not found");
        }

        if(fromAccount.getUser()!= user) {
            if (!(body.getTransactionType().equals("deposit") && fromAccount.getAccountId().equals(1))){
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

        deductMoneyFromAccountAndUpdateBalance(fromAccount, body, user);

        //todo: customize the check and update
        //User userFromAccount = userRepository.getUserModelById(fromAccount.getUser().getUserId());
        User userFromAccount = userRepository.findById(fromAccount.getUser().getUserId()).orElse(null);
        //todo: remaninglimit
        userFromAccount.setRemainingDayLimit(userFromAccount.getRemainingDayLimit()- body.getAmount());
        userRepository.save(userFromAccount);

        addMoneyToAccountAndUpdateBalance(toAccount, body.getAmount());

        Transaction transaction = convertDTOToTransactionEntity(body, user);
        return transactionRepository.save(transaction);
    }

    public void deductMoneyFromAccountAndUpdateBalance(Account fromAccount, TransactionDTO transactionDTO, User user) {
       //todo: this check needs to be changed and work with query
        if (transactionDTO.getAmount() > user.getRemainingDayLimit()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "cannot transfer funds! you have exceed your day limit");
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
        accountRepository.save(fromAccount);
    }

    public void addMoneyToAccountAndUpdateBalance(Account toAccount, double amount) {
        Double addBalanceAfterTransaction = toAccount.getCurrentBalance() + amount;
        toAccount.setCurrentBalance(addBalanceAfterTransaction);
        accountRepository.save(toAccount);
    }

    public Transaction convertDTOToTransactionEntity(TransactionDTO body, User user) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.valueOf(body.getTransactionType().toLowerCase()));
        transaction.setFromAccount(body.getFromAccount());
        transaction.setToAccount(body.getToAccount());
        transaction.setAmount(body.getAmount());

        LocalDateTime todayDatetime = LocalDateTime.now();
        transaction.setTimestamp(todayDatetime.truncatedTo(ChronoUnit.SECONDS));
        transaction.setUserPerforming(user);
        return transaction;
    }

    public List<Transaction>  findAllTransactionsByIBANAccount(String iban, LocalDateTime datefrom, LocalDateTime dateto, Integer skip, Integer limit) {

        return transactionRepository.filterTransactionsByIBAN(iban, datefrom, dateto, skip, limit);
    }

    public List<Transaction> findAllTransactionsLessThanAmount(Integer skip, Integer limit, String IBAN, Double amount) {

        return transactionRepository.findAllTransactionsLessThanAmount(amount, IBAN, skip, limit);
    }

    public List<Transaction> findAllTransactionsGreaterThanAmount(Integer skip, Integer limit, String IBAN, Double amount) {

        return transactionRepository.findAllTransactionsGreaterThanAmount(amount, IBAN, skip, limit);
    }

    public List<Transaction> findAllTransactionEqualToAmount(Integer skip, Integer limit, String IBAN, Double amount) {

        return transactionRepository.findAllTransactionsEqualToAmount(amount, IBAN, skip, limit);
    }

    public List<Transaction> findAllTransactionsByFromAccount(Integer skip, Integer limit, String IBAN) {

        return transactionRepository.findAllByFromAccount(IBAN, skip, limit);
    }

    public List<Transaction> findAllTransactionByToAccount(Integer skip, Integer limit, String IBAN) {
         return transactionRepository.findAllByToAccount(IBAN, skip, limit);
    }

}
