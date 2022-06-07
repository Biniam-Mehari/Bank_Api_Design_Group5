package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;

import javax.persistence.*;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Log
public class Account{

  @Id
  @SequenceGenerator(name = "account_seq", initialValue = 1)
  @GeneratedValue(generator = "account_seq", strategy = GenerationType.SEQUENCE)
  private Integer accountId;
  private String IBAN;

  @ManyToOne
  @JsonIgnoreProperties({"accounts"})
  private User user;

  private Double currentBalance = 0.00;
  private AccountType accountType;
  private Double absoluteLimit = 0.00;

  //private Integer userId;


  // to create the bank account
  public Account(String IBAN, User user,Double currentBalance, AccountType accountType) {
    this.IBAN = IBAN;
    this.user = user;
    this.currentBalance = currentBalance;
    this.accountType = accountType;
  }

  // generate the IBAN
  public String generateIBAN() {
    String countryCode = "NL";
    // create random number between 2 and 99
    int randomNumber = (int) (Math.random() * (99 - 2 + 1) + 2);
    String randomNumberString = String.valueOf(randomNumber);
    if (randomNumber < 10) {
      randomNumberString = "0" + randomNumberString;
    }
    String bankCode = "INHO";
    // create a 10 digit random number
    int randomNumber2 = (int) (Math.random() * (999999999 - 100000000 + 1) + 100000000);
    int lastDigit = (int) (Math.random() * (9 - 0 + 1) + 0);
    String randomNumberString2 = String.valueOf(randomNumber2);

    String iban = countryCode + randomNumberString + bankCode + randomNumberString2 + lastDigit;
    return iban;
  }

  //check valid iban
  public boolean validateIBAN(String IBAN){
    // validate IBAN for correct format
    if(IBAN.substring(0,2).equals("NL") && IBAN.substring(2,4).matches("[0-9]+") && IBAN.substring(4,8).equals("INHO") && IBAN.substring(8,18).matches("[0-9]+")){
      return true;
    }
    else{
      return false;
    }
  }



}