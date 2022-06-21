package io.swagger.api;

import io.swagger.model.Transaction;
import io.swagger.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionsApiControllerTest {

    Transaction transaction;
    User testuser;

    List<Transaction> transactions;

    @BeforeEach
    public void setup() {

    }

    @Test
    void transactionsGet() {
    }
}