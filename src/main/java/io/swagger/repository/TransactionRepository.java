package io.swagger.repository;


import io.swagger.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {


    @Query(value = "SELECT * FROM Transaction WHERE timestamp BETWEEN :fromDate AND :toDate ORDER BY TRANSACTION_ID OFFSET :skip ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
    List<Transaction> findAllByTimestampBetween(LocalDateTime fromDate,LocalDateTime toDate, int skip, int limit);

    @Query(value = "SELECT * from Transaction WHERE from_Account =:IBAN Or to_Account =:IBAN AND timestamp between :startDate and :endDate order by TRANSACTION_ID OFFSET :skip ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
    List<Transaction> filterTransactionsByIBAN(String IBAN, LocalDateTime startDate, LocalDateTime endDate,int skip,int limit);

    @Query(value = "SELECT * from Transaction WHERE amount > :amount AND from_Account =:IBAN Or to_Account =:IBAN ORDER BY TRANSACTION_ID OFFSET :skip ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
    List<Transaction> findAllTransactionsGreaterThanAmount(Double amount, String IBAN, int skip, int limit);

    @Query(value = "SELECT * FROM Transaction WHERE amount < :amount AND from_Account =:IBAN Or to_Account =:IBAN ORDER BY TRANSACTION_ID OFFSET :skip ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
    List<Transaction> findAllTransactionsLessThanAmount(Double amount, String IBAN, int skip, int limit);

    @Query(value = "SELECT * FROM Transaction WHERE amount =:amount AND from_Account =:IBAN Or to_Account =:IBAN ORDER BY TRANSACTION_ID OFFSET :skip ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
    List<Transaction> findAllTransactionsEqualToAmount(Double amount, String IBAN, int skip, int limit);


    @Query(value = "SELECT SUM(amount) FROM Transaction WHERE from_Account =:iban and timestamp between :startDay and :endDay", nativeQuery = true)
        Double getSumOfAllTransaction(String iban, LocalDateTime startDay, LocalDateTime endDay);

    @Query(value = "SELECT * FROM Transaction WHERE from_Account =:IBAN ORDER BY TRANSACTION_ID OFFSET :skip ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
    List<Transaction> findAllByFromAccount(String IBAN, int skip, int limit);

    @Query(value = "SELECT * FROM Transaction WHERE to_Account =:IBAN ORDER BY TRANSACTION_ID OFFSET :skip ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
    List<Transaction> findAllByToAccount(String IBAN, int skip, int limit);

    // get all transactions with fromAccount and toAccount by timestamp and limit and offset
//    @Query(value = "SELECT * FROM transaction WHERE from_account = ?1 AND to_account = ?2 AND timestamp BETWEEN ?3 AND ?4 LIMIT ?5 OFFSET ?6", nativeQuery = true)
//    List<Transaction> findAllTransactions(String fromAccount, String toAccount, LocalDateTime dateFrom, LocalDateTime dateTo, Integer limit, Integer offset);
//
}
