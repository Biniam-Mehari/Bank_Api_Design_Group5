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
       return transactionRepository.findAllByTimestampBetween(startdate, enddate, skip, limit);
    }

    public Transaction createTransaction(Transaction transaction) {

        return transactionRepository.save(transaction);
    }


    public List<Transaction>  findAllTransactionsByIBANAccount(String iban, LocalDateTime datefrom, LocalDateTime dateto, Integer skip, Integer limit) {

        return transactionRepository.filterTransactionsByIBAN(iban, datefrom, dateto,skip,limit);
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

    public List<Transaction> findAllTransactionsByFromAccount(String IBAN, Integer skip, Integer limit) {

        return transactionRepository.findAllByFromAccount(IBAN, skip, limit);
    }

    public List<Transaction> findAllTransactionByToAccount(String IBAN, Integer skip, Integer limit) {

         return transactionRepository.findAllByToAccount(IBAN, skip, limit);

    }
}
