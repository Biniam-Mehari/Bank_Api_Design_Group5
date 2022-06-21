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
import java.util.Date;
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

        List<Transaction> transactions = new ArrayList<>();
        transactions.addAll(transactionRepository.findAllByTimestampBetween(startdate, enddate));

        return filterTransactionsByPagination(skip, limit, transactions);
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

        List<Transaction> transactions = new ArrayList<>();

        List<Transaction> temp = transactionRepository.filterTransactionsByIBAN(iban, datefrom, dateto,skip,limit);
       // transactions.addAll(temp);

        return temp;
                //filterTransactionsByPagination(skip, limit, transactions);
    }


    public List<Transaction> findAllTransactionsLessThanAmount(Integer skip, Integer limit, String IBAN, Double amount) {
        List<Transaction> transactions = new ArrayList<>();

        transactions.addAll(transactionRepository.findAllTransactionsLessThanAmount(amount, IBAN));

        return filterTransactionsByPagination(skip, limit, transactions);
    }

    public List<Transaction> findAllTransactionsGreaterThanAmount(Integer skip, Integer limit, String IBAN, Double amount) {
        List<Transaction> transactions = new ArrayList<>();

        transactions.addAll(transactionRepository.findAllTransactionsGreaterThanAmount(amount, IBAN));

        return filterTransactionsByPagination(skip, limit, transactions);
    }

    public List<Transaction> findAllTransactionEqualToAmount(Integer skip, Integer limit, String IBAN, Double amount) {
        List<Transaction> transactions = new ArrayList<>();
        transactions.addAll(transactionRepository.findAllTransactionsEqualToAmount(amount, IBAN));

        return filterTransactionsByPagination(skip, limit, transactions);
    }

    public List<Transaction> findAllTransactionsByFromAccount(Integer skip, Integer limit, String IBAN) {
        List<Transaction> transactions = new ArrayList<>();

        transactions.addAll(transactionRepository.findAllByFromAccount(IBAN));

        return filterTransactionsByPagination(skip, limit, transactions);
    }

    public List<Transaction> findAllTransactionByToAccount(Integer skipValue, Integer limitValue, String IBAN) {
         List<Transaction> transactions = new ArrayList<>();

         transactions.addAll(transactionRepository.findAllByToAccount(IBAN));

         return filterTransactionsByPagination(skipValue, limitValue, transactions);
    }

    public List<Transaction> filterTransactionsByPagination(Integer skip, Integer limit, List<Transaction> transactions) {

        if (skip < 0 || limit <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "skip value cannot be less than zero or limit value cannot be less than or equal to zero");
        }

        if (skip > limit) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "skip value cannot be greater than the limit value");
        }

        int transactionSize = transactions.size();

        limit = limit + skip;

        if (limit > transactionSize) {
            limit = transactionSize;
        }

        if (skip > transactionSize) {
            skip = transactionSize;
        }

        return transactions.subList(skip, limit);
    }
}
