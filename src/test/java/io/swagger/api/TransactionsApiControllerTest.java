package io.swagger.api;

import io.swagger.model.*;
import io.swagger.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionsApiControllerTest {

    @Autowired
    private TransactionService transactionService;

    User testuser1;
    User testuser2;


    Account savingAccount;
    Account currentAccount;

    Transaction transaction;

    List<Transaction> transactions;

    @Autowired
    private PasswordEncoder passwordEncoder;

    String str = "2022-03-12";
    String str2 = "2022-03-15";
    String str3 = "2022-04-17";
    String str4 = "2022-03-10";
    String str5 = "2022-06-09";

    LocalDate datetime1 = LocalDate.parse(str);
    LocalDate datetime2 = LocalDate.parse(str2);
    LocalDate datetime3 = LocalDate.parse(str3);
    LocalDate datetime4 = LocalDate.parse(str4);



    @BeforeEach
    public void setup() {

        transactions  = List.of(
                new Transaction(testuser1, "", "", 200.00, TransactionType.deposit,datetime1),
                new Transaction(testuser1, "", "", 100.00, TransactionType.transfer,datetime2),
                new Transaction(testuser1, "", "", 400.00, TransactionType.withdraw, datetime3),
                new Transaction(testuser2, "", "", 300.00, TransactionType.deposit, datetime4),
                new Transaction(testuser2, "", "", 200.00, TransactionType.deposit, datetime2),
                new Transaction(testuser1, "", "", 100.00, TransactionType.transfer, datetime1),
                new Transaction(testuser2, "", "", 300.00, TransactionType.deposit, datetime4),
                new Transaction(testuser2, "", "", 200.00, TransactionType.deposit, datetime2),
                new Transaction(testuser1, "", "", 100.00, TransactionType.transfer, datetime1)
        );
    }

}