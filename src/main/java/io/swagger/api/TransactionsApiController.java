package io.swagger.api;

import io.swagger.annotations.Api;
import io.swagger.model.*;
import io.swagger.model.dto.TransactionDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.dto.TransactionResponseDTO;
import io.swagger.service.AccountService;
import io.swagger.service.TransactionService;
import io.swagger.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-05-13T15:15:19.174Z[GMT]")
@RestController
@Api(tags = {"employee", "customer", "transaction"})
public class TransactionsApiController implements TransactionsApi {

    private static final Logger log = LoggerFactory.getLogger(TransactionsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;

    @Autowired
    public TransactionsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<List<TransactionResponseDTO>> transactionsGet(
            @Parameter(in = ParameterIn.QUERY, description = "fetch transaction from start date" , required=true,schema=@Schema()) @Valid @RequestParam(value = "startDate", required = true)
            @DateTimeFormat(pattern = "yyyy-MM-dd") String startDate,
            @Parameter(in = ParameterIn.QUERY, description = "fetch transaction till end date" ,required=true,schema=@Schema())
            @Valid @RequestParam(value = "endDate", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate,
            @Valid @RequestParam(value = "skip", required = false) Integer offset,
            @Valid @RequestParam(value = "limit", required = false) Integer limit) {


        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String username = userAuthentication.getName();
        User user = userService.getUserByUsername(username);

        if(!user.getRoles().contains(Role.ROLE_ADMIN)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not authorized to access this list");
        }
        LocalDate startdate;
        LocalDate enddate;
        try{
            startdate = LocalDate.parse(startDate);
            enddate = LocalDate.parse(endDate);
        }catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "date needs to be in yyyy-MM-dd");
        }

        if (offset == null && limit == null) {
            offset = 1;
            limit = 10;
        }

        List<Transaction> transactions = transactionService.getAllTransactions(offset, limit, startdate, enddate);

        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        for (Transaction transaction : transactions) {
            TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(transaction);
            transactionResponseDTOS.add(transactionResponseDTO);
        }
        return new ResponseEntity<List<TransactionResponseDTO>>(transactionResponseDTOS, HttpStatus.OK);

    }

    public TransactionResponseDTO convertTransactionEntityToTransactionResponseDTO(Transaction storeTransaction) {

        TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
        transactionResponseDTO.setTransactionId(storeTransaction.getTransactionId());
        transactionResponseDTO.setUserPerformingId(storeTransaction.getUserPerforming().getUserId());
        transactionResponseDTO.setFromAccount(storeTransaction.getFromAccount());
        transactionResponseDTO.setToAccount(storeTransaction.getToAccount());
        transactionResponseDTO.setAmount(storeTransaction.getAmount());
        transactionResponseDTO.setTransactionType(storeTransaction.getTransactionType().toString());
        transactionResponseDTO.setTimestamp(storeTransaction.getTimestamp());
        return transactionResponseDTO;
    }

    // creates transaction
    public ResponseEntity<TransactionResponseDTO> transactionsPost(
            @Parameter(in = ParameterIn.DEFAULT, description = "", schema=@Schema())
            @Valid @RequestBody TransactionDTO body) throws Exception {

        if (body.getFromAccount().equals(null) ||
                body.getToAccount().equals(null) ||
                body.getTransactionType().equals(null) ||
                body.getAmount().equals(null)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One of input parameters is null");
        }

        if(body.getFromAccount().equals(body.getToAccount()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "transfer accounts cannot be the same!");

        User user = loggedInUser();

       Transaction storeTransaction = transactionService.createTransaction(user, body);
       TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(storeTransaction);
       return new ResponseEntity<TransactionResponseDTO>(transactionResponseDTO, HttpStatus.OK);
    }



    private User loggedInUser(){
        Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String username = userAuthentication.getName();
        return userService.getUserByUsername(username);
    }


}
