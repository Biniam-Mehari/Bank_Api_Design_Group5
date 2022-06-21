package io.swagger.repository;


import io.swagger.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDateTime;
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

    @Query(value = "SELECT * FROM Transaction WHERE from_Account =:IBAN ORDER BY TRANSACTION_ID OFFSET :skip ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
            List<Transaction> findAllByFromAccount(String IBAN, int skip, int limit);

    @Query(value = "SELECT * FROM Transaction WHERE to_Account =:IBAN ORDER BY TRANSACTION_ID OFFSET :skip ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
            List<Transaction> findAllByToAccount(String IBAN, int skip, int limit);

}
