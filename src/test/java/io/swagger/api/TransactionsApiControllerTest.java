package io.swagger.api;

import io.cucumber.java8.Tr;
import io.swagger.model.*;
import io.swagger.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionsApiControllerTest {

    List<Transaction> transactionList;

    User testuser1;
    User testuser2;

    LocalDateTime localDate;
    LocalDateTime localDate2;
    LocalDateTime localDate3;
    LocalDateTime localDate4;

    Account savingAccountuser1;
    Account currentAccountuser1;

    Account savingAccountuser2;
    Account currentAccountuser2;
    Transaction test;

    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    public void setup() {

        testuser1 = new User();

        testuser1.setFullname("Marshall Mathers");
        testuser1.setUsername("eminem");
        testuser1.setPassword("secret");
        testuser1.setRoles(Arrays.asList(Role.ROLE_USER));

        testuser2 = new User();
        testuser2.setUserId(2);
        testuser2.setFullname("Thresa Lisbon");
        testuser2.setUsername("thr");
        testuser2.setRoles(Arrays.asList(Role.ROLE_ADMIN));
        testuser2.setPassword("thr345");

        savingAccountuser1 = new Account("NL43INHO4186520410", testuser1, 300.00, AccountType.saving);
        currentAccountuser1 = new Account("NL65INHO2095310012", testuser1, 800.00, AccountType.current);

        savingAccountuser2 = new Account("NL53INHO4666097791", testuser2, 500.00, AccountType.saving);
        currentAccountuser2 = new Account("NL05INHO8972164151", testuser2, 4000.00, AccountType.current);

        String str1 = "2022-05-04 10:25:57";
        String str2 = "2022-06-29 16:27:39";
        String str3 = "2022-04-15 04:55:23";
        String str4 = "2024-05-14 14:36:25";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        localDate = LocalDateTime.parse(str1, formatter);
        localDate2 = LocalDateTime.parse(str2, formatter);
        localDate3 = LocalDateTime.parse(str3, formatter);
        localDate4 = LocalDateTime.parse(str4, formatter);

        transactionList = List.of(
            new Transaction(testuser1, "NL65INHO2095310012", "NL05INHO8972164151", 200.00, TransactionType.transfer, localDate),
            new Transaction(testuser2, "NL01INHO0000000001", "NL05INHO8972164151", 400.00, TransactionType.withdraw, localDate2),
            new Transaction(testuser1, "NL65INHO2095310012", "NL65INHO2095310012", 120.00, TransactionType.withdraw, localDate2),
            new Transaction(testuser1, "NL65INHO2095310012", "NL05INHO8972164151", 300.00, TransactionType.transfer, localDate3),
            new Transaction(testuser2, "NL01INHO0000000001", "NL05INHO8972164151", 100.00, TransactionType.withdraw, localDate2),
            new Transaction(testuser2, "NL65INHO2095310012", "NL05INHO8972164151", 1200.00, TransactionType.withdraw, localDate4)
        );
        test =  new Transaction(testuser2, "NL65INHO2095310012", "NL05INHO8972164151", 1200.00, TransactionType.withdraw, localDate4);

    }

    @Test
    public void shouldGetAllTransactionsByTimeStamp() {

    }

    @Test
    void transactionsGet() {
      List<Transaction> allTransactions = transactionService.getAllTransactions(0, 5, localDate2, localDate4);
        assertNotNull(allTransactions);
    }

    @Test
    void transactionsPost() {
        Transaction transaction = transactionService.createTransaction(test);
        assertNotNull(transaction);
    }
}