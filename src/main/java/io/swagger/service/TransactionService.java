package io.swagger.service;

import io.swagger.model.*;
import io.swagger.model.dto.TransactionDTO;
import io.swagger.model.dto.TransactionResponseDTO;
import io.swagger.repository.TransactionRepository;
import io.swagger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;


    public List<TransactionResponseDTO> getAllTransactions(LocalDate startdate, LocalDate enddate) {
        List<Transaction> transactions = transactionRepository.findAllByTimestampBetween(startdate, enddate);
        List<TransactionResponseDTO> transactionResponseDTOList = new ArrayList<>();
        for (Transaction transaction: transactions) {
            TransactionResponseDTO transactionResponseDTO = convertTransactionEntityToTransactionResponseDTO(transaction);
            transactionResponseDTOList.add(transactionResponseDTO);
        }
        return transactionResponseDTOList;
    }

    public Transaction createTransaction(User user, TransactionDTO body) throws Exception {

        Transaction transaction = convertDTOToTransactionEntity(body, user);
        return transactionRepository.save(transaction);
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
}
