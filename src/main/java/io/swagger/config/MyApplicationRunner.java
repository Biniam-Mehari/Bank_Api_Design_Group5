package io.swagger.config;

import io.swagger.model.*;
import io.swagger.repository.AccountRepository;
import io.swagger.repository.TransactionRepository;
import io.swagger.repository.UserRepository;
import io.swagger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private MyWebSecurityConfig securityConfig;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception{
        // Create 1 Employee and 1 Customer
        // 1 = Employee, 0 = Customer
        User bank = userService.createUser("bank", "bank account", "secret", 1);
        User admin = userService.createUser("admin", "admin", "secret", 1);
        User user1 = userService.createUser("tommy", "tommy king", "secret", 1);
        User user2 = userService.createUser("abhishek", "abhishek king", "secret", 0);
        User user3 = userService.createUser("amrish", "amrish king", "secret", 0);
        User user4 = userService.createUser("biniam", "biniam king", "secret", 1);

        // create a list of user
        List<User> users = new ArrayList<>(Arrays.asList(bank,admin, user1,user2,user3,user4));
        userRepository.saveAll(users);
        // create 50 dummy users for testing
        userService.create50RandomUsers();

        List<Account> accounts = List.of(
                new Account("NL01INHO0000000001",bank, 100000000.00, AccountType.current),
                new Account("NL21INHO0123400081",user2, 50.00, AccountType.saving),
                new Account("NL21INHO0123400789",user2, 750.00, AccountType.current),
                new Account("NL51INHO0123400029",user3, 4000.00, AccountType.current),
                new Account("NL30INHO0123400157",user3, 50.00, AccountType.saving),
                new Account("NL21INHO0123400233",user4, 750.00, AccountType.current),
                new Account("NL51INHO0123400455",user1, 2000.00, AccountType.current)
        );
        accountRepository.saveAll(accounts);

        // Integer userPerformingId, String fromAccount, String toAccount, Double amount, String transactionType, java.time.LocalDateTime timestamp, Double balanceAfterTransfer
        String str1 = "2022-04-03 10:25:57";
        String str2 = "2022-05-27 16:27:39";
        String str3 = "2022-05-27 04:55:23";
        String str4 = "2024-05-27 14:36:25";
        String str5 = "2024-05-27 14:52:50";
        String str6 = "2022-05-26 23:04:03";
        String str7 = "2022-05-26 02:38:07";
        String str8 = "2023-05-27 22:32:17";
        String str9 = "2023-05-27 03:08:15";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime1 =  LocalDateTime.parse(str1, formatter);
        LocalDateTime dateTime2 = LocalDateTime.parse(str2, formatter);
        LocalDateTime dateTime3 = LocalDateTime.parse(str3, formatter);
        LocalDateTime dateTime4 =  LocalDateTime.parse(str4, formatter);
        LocalDateTime dateTime5 = LocalDateTime.parse(str5, formatter);
        LocalDateTime dateTime6 = LocalDateTime.parse(str6, formatter);
        LocalDateTime dateTime7 =  LocalDateTime.parse(str7, formatter);
        LocalDateTime dateTime8 = LocalDateTime.parse(str8, formatter);
        LocalDateTime dateTime9 = LocalDateTime.parse(str9, formatter);

        List<Transaction> transactions = List.of(
                new Transaction(user2, "NL21INHO0123400789", "NL21INHO0123400081", 1900.00, TransactionType.transfer, dateTime1),
                new Transaction(admin, "NL21INHO0123400789", "NL21INHO0123400081", 2100.00, TransactionType.deposit, dateTime2),
                new Transaction(admin, "NL01INHO0000000001", "NL21INHO0123400789", 800.00, TransactionType.withdraw, dateTime1),
                new Transaction(user1, "NL01INHO0000000001", "NL21INHO0123400789", 1200.00, TransactionType.transfer, dateTime2),
                new Transaction(user1, "NL01INHO0000000001", "NL51INHO0123400455", 700.00, TransactionType.deposit, dateTime1),
                new Transaction(user4, "NL01INHO0000000001", "NL51INHO0123400455", 500.00, TransactionType.withdraw, dateTime2),
                new Transaction(user4, "NL21INHO0123400233", "NL01INHO0000000001", 1900.00, TransactionType.transfer, dateTime7),
                new Transaction(user4, "NL21INHO0123400233", "NL01INHO0000000001", 1000.00, TransactionType.deposit, dateTime8),
                new Transaction(user3, "NL51INHO0123400029", "NL01INHO0000000001", 2000.00, TransactionType.withdraw, dateTime9),
                new Transaction(user3, "NL51INHO0123400029", "NL01INHO0000000001", 900.00, TransactionType.transfer, dateTime9),
                new Transaction(user3, "NL51INHO0123400029", "NL01INHO0000000001", 700.00, TransactionType.transfer, dateTime8),
                new Transaction(user3, "NL51INHO0123400029", "NL01INHO0000000001", 500.00, TransactionType.transfer, dateTime9),

                new Transaction(user2, "NL21INHO0123400789", "NL21INHO0123400081", 1900.00, TransactionType.transfer, dateTime1),
                new Transaction(admin, "NL21INHO0123400789", "NL21INHO0123400081", 2100.00, TransactionType.deposit, dateTime2),
                new Transaction(admin, "NL01INHO0000000001", "NL21INHO0123400789", 800.00, TransactionType.withdraw, dateTime1),
                new Transaction(user1, "NL01INHO0000000001", "NL21INHO0123400789", 1200.00, TransactionType.transfer, dateTime2),
                new Transaction(user1, "NL01INHO0000000001", "NL51INHO0123400455", 700.00, TransactionType.deposit, dateTime1)
        );
        transactionRepository.saveAll(transactions);
    }
}