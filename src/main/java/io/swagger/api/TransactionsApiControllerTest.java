package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Arrays;
import io.swagger.model.Transaction;
import io.swagger.model.TransactionType;
import io.swagger.model.User;
import io.swagger.model.dto.TransactionDTO;
import io.swagger.model.dto.TransactionResponseDTO;
import io.swagger.service.TransactionService;
import io.swagger.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.hasSize;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionsApiController.class)
class TransactionsApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    List<Transaction> transactions;
    List<TransactionResponseDTO> transactionResponseDTOs;


    String date1 = "2022-04-03";
    String date2 = "2022-05-27";

    LocalDate datetime1 = LocalDate.parse(date1);
    LocalDate datetime2 = LocalDate.parse(date2);
    User user;
    User user1;


    @BeforeEach
    public void setup() {

        user = userService.createUser("abhi", "abhishek narvekar", "sec", 1);
        user1 = userService.createUser("ary", "alice walker", "alii", 0);

        transactions = List.of(
                new Transaction(user, "NL01INHO0000000001", "NL21INHO0123400001", 2300.00, TransactionType.transfer, datetime1),
                new Transaction(user, "NL01INHO0000000001", "NL21INHO0123400001", 3200.00, TransactionType.withdraw, datetime2),
                new Transaction(user, "NL01INHO0000000001", "NL21INHO0123400001", 2700.00, TransactionType.deposit, datetime1),
                new Transaction(user, "NL01INHO0000000001", "NL21INHO0123400001", 2300.00, TransactionType.transfer, datetime2),
                new Transaction(user, "NL21INHO0123400001", "NL21INHO0123400001", 3200.00, TransactionType.withdraw, datetime1),
                new Transaction(user, "NL21INHO0123400001", "NL21INHO0123400001", 2700.00, TransactionType.deposit, datetime2)
        );

        for (Transaction transaction : transactions) {
            TransactionResponseDTO transactionResponseDTO = transactionService.convertTransactionEntityToTransactionResponseDTO(transaction);
            transactionResponseDTOs.add(transactionResponseDTO);
        }

    }

    @Test
    public void getAllTransactionsShouldReturnAllTransactions() throws Exception {

        when(transactionService.getAllTransactions(datetime1, datetime2)).thenReturn(transactionResponseDTOs);

        this.mockMvc.perform(get("/transactions"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$", hasSize(6)));
    }

    @Test
    public void createTransactionShouldReturnStatusCreatedAndOneObject() throws Exception {
        Transaction transaction = new Transaction();
        TransactionDTO transactionDTO = new TransactionDTO();

        when(transactionService.createTransaction(user.getUsername(), any(TransactionDTO.class))).thenReturn(transaction);
        this.mockMvc.perform(post("/transactions")
                        .content(objectMapper.writeValueAsString(transactionDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }
}