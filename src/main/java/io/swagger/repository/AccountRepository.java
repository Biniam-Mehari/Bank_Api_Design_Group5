package io.swagger.repository;

import io.swagger.model.Account;
import io.swagger.model.AccountType;
import io.swagger.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Integer> {

    Account findByIBAN(String iban);

    // find all accounts belonging to a user
    List<Account> findAllByUser(User user);

    List<Account> findAllByUserAndAccountType(User user, AccountType accountType);

    //todo: better queirs
    @Query(value = "SELECT * from Account offset :skip rows FETCH NEXT :limit rows only", nativeQuery = true)
    List<Account> getAllAccountsinsideSkipAndLimit(Integer skip,Integer limit);

    // get sum of all accounts currentBalance where account contains user
    @Query("select sum(account.currentBalance) from Account account where account.user =:user")
    Double getSumOfAllAccounts(User user);

    //returns current account of a user
    Account findByUserAndAccountType(User user, AccountType accountType);
}