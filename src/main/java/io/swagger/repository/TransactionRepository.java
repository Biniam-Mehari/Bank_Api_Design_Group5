package io.swagger.repository;


import io.swagger.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {


    List<Transaction> findAllByTimestampBetween(LocalDateTime fromDate,LocalDateTime toDate);

    @Query(value = "SELECT * from Transaction WHERE from_Account =:IBAN Or to_Account =:IBAN AND timestamp between :startDate and :endDate order by TRANSACTION_ID offset :skip rows FETCH NEXT :limit rows only", nativeQuery = true)
            List<Transaction> filterTransactionsByIBAN(String IBAN, LocalDateTime startDate, LocalDateTime endDate,int skip,int limit);


    @Query(value = "SELECT * from Transaction WHERE amount > :amount AND from_Account =:IBAN Or to_Account =:IBAN", nativeQuery = true)
            List<Transaction> findAllTransactionsGreaterThanAmount(Double amount, String IBAN);

    @Query(value = "SELECT * FROM Transaction WHERE amount < :amount AND from_Account =:IBAN Or to_Account =:IBAN", nativeQuery = true)
            List<Transaction> findAllTransactionsLessThanAmount(Double amount, String IBAN);

    @Query(value = "SELECT * FROM Transaction WHERE amount =:amount AND from_Account =:IBAN Or to_Account =:IBAN", nativeQuery = true)
            List<Transaction> findAllTransactionsEqualToAmount(Double amount, String IBAN);

    @Query("select sum(transaction.amount) from Transaction transaction where transaction.fromAccount =:fromaccount And transaction.timestamp =:dateOfToday")
            Double transactionBalanceOfOneDay(Date dateOfToday);

    List<Transaction> findAllByFromAccount(String IBAN);
    List<Transaction> findAllByToAccount(String IBAN);

    // get all transactions with fromAccount and toAccount by timestamp and limit and offset
//    @Query(value = "SELECT * FROM transaction WHERE from_account = ?1 AND to_account = ?2 AND timestamp BETWEEN ?3 AND ?4 LIMIT ?5 OFFSET ?6", nativeQuery = true)
//    List<Transaction> findAllTransactions(String fromAccount, String toAccount, LocalDateTime dateFrom, LocalDateTime dateTo, Integer limit, Integer offset);
//
}
