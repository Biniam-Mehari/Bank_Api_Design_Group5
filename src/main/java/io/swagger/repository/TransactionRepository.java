package io.swagger.repository;

import io.swagger.model.Account;
import io.swagger.model.Transaction;
import io.swagger.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    // get all transactions with toAccount between two dates
    List<Transaction> getTransactionByToAccountAndTimestampBetween(String toAccount, LocalDateTime dateFrom, LocalDateTime dateTo);

    // get all transactions with fromAccount between two dates
    List<Transaction> getTransactionByFromAccountAndTimestampBetween(String fromAccount, LocalDateTime dateFrom, LocalDateTime dateTo);

    //List<Transaction> getTransactionByFromAccountAndToAccountAndTimestampBetween(String fromAccount, String toAccount, LocalDate dateFrom);
    //return all transactions between from date and to date
    List<Transaction> findAllByTimestampBetween(LocalDateTime fromDate,LocalDateTime toDate);

    // return all transactions by Amount range greater then given amount
    List<Transaction> findAllByAmountGreaterThanAndFromAccount(Double amount, String IBAN);
    List<Transaction> findAllByAmountGreaterThanAndToAccount(Double amount, String iban);

    // return all transactions by Amount range less than given amount
    List<Transaction> findAllByAmountLessThanAndFromAccount(Double amount, String IBAN);
    List<Transaction> findAllByAmountLessThanAndToAccount(Double amount, String IBAN);

    // return all transactions by Amount range == the given amount
    List<Transaction> findAllByAmountEqualsAndFromAccount(Double amount, String IBAN);
    List<Transaction> findAllByAmountEqualsAndToAccount(Double amount, String IBAN);

    List<Transaction> findAllByFromAccount(String IBAN);
    List<Transaction> findAllByToAccount(String IBAN);


}
