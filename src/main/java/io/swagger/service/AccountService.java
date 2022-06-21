package io.swagger.service;

import io.swagger.model.Account;
import io.swagger.model.AccountType;
import io.swagger.model.User;
import io.swagger.repository.AccountRepository;
import io.swagger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account saveAccount(Account account){
        return accountRepository.save(account);
    }


    public void closeAccount(Account account) {
        account.setStatus("closed");
        accountRepository.save(account);
    }

    public Account findByIBAN(String iban) {
        //check if we have account returned or null before send the data ??
        return accountRepository.findByIBAN(iban);
    }

    public List<Account> getAllAccountsOfUser(User user) {
        return accountRepository.findAllByUser(user);
    }

    //todo: better queirs
    public List<Account> findAllByAccountIdAfterAndAccountIdBefore(Integer skip,Integer limit){
        //return (List<Account>) accountRepository.findAllByAccountIdAfterAndAccountIdBefore(skip,limit);
        return  accountRepository.getAllAccountsinsideSkipAndLimit(skip,limit);
    }

    public List<Account> findAllByUserAndAccountType(User user, AccountType accountType) {
        return accountRepository.findAllByUserAndAccountType(user,accountType);
    }

    public Account findByUserAndAccountType(User user, AccountType accountType) {
        return  accountRepository.findByUserAndAccountType(user,accountType);
    }
}