package io.swagger.service;

import io.swagger.model.*;
import io.swagger.model.dto.TransactionDTO;
import io.swagger.model.dto.TransactionResponseDTO;
import io.swagger.repository.AccountRepository;
import io.swagger.repository.TransactionRepository;
import io.swagger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;


    public List<Transaction> getAllTransactions(Integer skip, Integer limit, LocalDate startdate, LocalDate enddate) {
        List<Transaction> transactions = transactionRepository.findAllByTimestampBetween(startdate, enddate);

        return filterTransactionsByPagination(skip, limit, transactions);
    }

    public Transaction createTransaction(User user, TransactionDTO body) {

        Account fromAccount = new Account();
        Account toAccount = new Account();

        if (!fromAccount.validateIBAN(body.getFromAccount())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid IBAN format");
        }

        if (!toAccount.validateIBAN(body.getToAccount())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid IBAN format");
        }

        fromAccount = accountRepository.findByIBAN(body.getFromAccount());
        toAccount = accountRepository.findByIBAN(body.getToAccount());

        if (fromAccount == null || toAccount == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "account not found");
        }

        if(fromAccount.getUser()!= user) {
            if (!(body.getTransactionType().equals("deposit") && fromAccount.getAccountId().equals(1))){
                if (!user.getRoles().contains(Role.ROLE_ADMIN)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "this account does not belong to you");
                }
            }
        }

        if(!fromAccount.getAccountType().equals(AccountType.current) || !toAccount.getAccountType().equals(AccountType.current)) {
            if(fromAccount.getAccountType().equals(AccountType.saving) && toAccount.getAccountType().equals(AccountType.saving)){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "you cannot send or receive from a saving account to a saving account");
            }
            if(fromAccount.getUser() != toAccount.getUser()){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "You cannot send or receive from saving account and current account of different user");
            }
        }
        deductMoneyFromAccountAndUpdateBalance(fromAccount, body, user);

        //User userFromAccount = userRepository.getUserModelById(fromAccount.getUser().getUserId());
        User userFromAccount = userRepository.findById(fromAccount.getUser().getUserId()).orElse(null);
        userFromAccount.setRemainingDayLimit(userFromAccount.getRemainingDayLimit()- body.getAmount());
        userRepository.save(userFromAccount);

        addMoneyToAccountAndUpdateBalance(toAccount, body);

        Transaction transaction = convertDTOToTransactionEntity(body, user);
        return transactionRepository.save(transaction);
    }

    public void deductMoneyFromAccountAndUpdateBalance(Account fromAccount, TransactionDTO transactionDTO, User user) {
        if (transactionDTO.getAmount() > user.getRemainingDayLimit()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "cannot transfer funds! you have exceed your day limit");
        }

        if (transactionDTO.getAmount() > user.getTransactionLimit()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "cannot transfer funds! you have exceed your transaction limit");
        }

        if (transactionDTO.getAmount() <= 0.00) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "amount to be transfered needs to be greater than zero");
        }

        //deduct balance
        Double deductBalanceAfterTransaction = fromAccount.getCurrentBalance() - transactionDTO.getAmount();

        //check absolute limit
        if(deductBalanceAfterTransaction < fromAccount.getAbsoluteLimit()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "you have exceeded your absolute limit!");
        }
        fromAccount.setCurrentBalance(deductBalanceAfterTransaction);
        accountRepository.save(fromAccount);
    }

    public void addMoneyToAccountAndUpdateBalance(Account toAccount, TransactionDTO transactionDTO) {
        Double addBalanceAfterTransaction = toAccount.getCurrentBalance() + transactionDTO.getAmount();
        toAccount.setCurrentBalance(addBalanceAfterTransaction);
        accountRepository.save(toAccount);
    }

    public Transaction convertDTOToTransactionEntity(TransactionDTO body, User user) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.valueOf(body.getTransactionType().toLowerCase()));
        transaction.setFromAccount(body.getFromAccount());
        transaction.setToAccount(body.getToAccount());
        transaction.setAmount(body.getAmount());
        transaction.setTimestamp(LocalDate.now());
        transaction.setUserPerforming(user);
        return transaction;
    }

    public List<TransactionResponseDTO>  findAllTransactionsByIBANAccount(String iban, LocalDate datefrom, LocalDate dateto) {

        List<Transaction> transactions = new ArrayList<>();
        List<TransactionResponseDTO> transactionResponseDTOList = new ArrayList<>();

        transactions.addAll(transactionRepository.getTransactionByFromAccountAndTimestampBetween(iban, datefrom, dateto));
        transactions.addAll(transactionRepository.getTransactionByToAccountAndTimestampBetween(iban, datefrom, dateto));

        for (Transaction transaction: transactions) {
             TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(transaction);
             transactionResponseDTOList.add(transactionResponseDTO);
        }

        return transactionResponseDTOList;
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

    public List<TransactionResponseDTO> findAllTransactionsLessThanAmount(String IBAN, Double amount) {
        List<Transaction> transactions = new ArrayList<>();
        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        transactions.addAll(transactionRepository.findAllByAmountLessThanAndFromAccount(amount, IBAN));
        transactions.addAll(transactionRepository.findAllByAmountLessThanAndToAccount(amount, IBAN));

        for (Transaction transaction: transactions) {
            TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(transaction);
            transactionResponseDTOS.add(transactionResponseDTO);
        }
        return transactionResponseDTOS;
    }

    public List<TransactionResponseDTO> findAllTransactionsGreaterThanAmount(String IBAN, Double amount) {
        List<Transaction> transactions = new ArrayList<>();
        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        transactions.addAll(transactionRepository.findAllByAmountGreaterThanAndFromAccount(amount, IBAN));
        transactions.addAll(transactionRepository.findAllByAmountGreaterThanAndToAccount(amount, IBAN));

        for (Transaction transaction: transactions) {
            TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(transaction);
            transactionResponseDTOS.add(transactionResponseDTO);
        }
        return transactionResponseDTOS;
    }

    public List<TransactionResponseDTO> findAllTransactionEqualToAmount(String IBAN, Double amount) {
        List<Transaction> transactions = new ArrayList<>();
        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        transactions.addAll(transactionRepository.findAllByAmountEqualsAndFromAccount(amount, IBAN));
        transactions.addAll(transactionRepository.findAllByAmountEqualsAndToAccount(amount, IBAN));

        for (Transaction transaction: transactions) {
            TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(transaction);
            transactionResponseDTOS.add(transactionResponseDTO);
        }
        return transactionResponseDTOS;
    }

    public List<TransactionResponseDTO> findAllTransactionsByFromAccount(String IBAN) {
        List<Transaction> transactions = new ArrayList<>();
        List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

        transactions.addAll(transactionRepository.findAllByFromAccount(IBAN));

        for (Transaction transaction: transactions) {
            TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(transaction);
            transactionResponseDTOS.add(transactionResponseDTO);
        }
        return transactionResponseDTOS;
    }

    public List<TransactionResponseDTO> findAllTransactionByToAccount(String IBAN) {
         List<Transaction> transactions = new ArrayList<>();
         List<TransactionResponseDTO> transactionResponseDTOS = new ArrayList<>();

         transactions.addAll(transactionRepository.findAllByToAccount(IBAN));

         for (Transaction transaction: transactions) {
              TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(transaction);
              transactionResponseDTOS.add(transactionResponseDTO);
         }
         return transactionResponseDTOS;
    }

    public List<Transaction> filterTransactionsByPagination(Integer skip, Integer limit, List<Transaction> transactions) {
        if (skip < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "skip value cannot be less than zero");
        }

        if (limit <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "limit cannot be less than or equal to zero");
        }

        return transactions.subList(skip, limit);
    }
}
