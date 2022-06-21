package io.swagger.steps.stepdefs;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java8.En;
import io.swagger.model.dto.TransactionDTO;
import io.swagger.steps.BaseStepDefinations;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

public class TransactionStepDefs extends BaseStepDefinations implements En {

    private static final String VALID_TOKEN_USER = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbXJpc2giLCJhdXRoIjpbeyJhdXRob3JpdHkiOiJST0xFX1VTRVIifV0sImlhdCI6MTY1NTgyNDgzOCwiZXhwIjoxNjU1ODI4NDM4fQ.z1IcTVlTymBRIIS-vyw0GFC9Jk5kaEWoNwjm4lkQWc0";
    private static final String VALID_TOKEN_ADMIN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9LHsiYXV0aG9yaXR5IjoiUk9MRV9BRE1JTiJ9XSwiaWF0IjoxNjU1NjY3NTk0LCJleHAiOjE2NTYyNzIzOTR9.XI7nat8c9C1oxrLkFydif3C6qtdzIIg6OGoiRcjLr6E";
    private static final String INVALID_TOKEN = "invalidtoken";

    private final HttpHeaders httpHeaders = new HttpHeaders();
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private final ObjectMapper mapper = new ObjectMapper();

    private ResponseEntity<String> response;
    private HttpEntity<String> request;
    private Integer status;

    private String token = null;

    private TransactionDTO dtos;

    public TransactionStepDefs() {
        Given("^I have an valid token for role \"([^\"]*)\" to access all transactions of users$", (String role) -> {
             token = VALID_TOKEN_ADMIN;
        });
        When("^I call the get all transactions endpoint$", () -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            request = new HttpEntity<>(null, httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/transactions?startDate=2022-04-03T10:25:57&endDate=2022-05-27T16:27:39&skip=4&limit=5", HttpMethod.GET, request, String.class);
            status = response.getStatusCodeValue();
        });

        Then("^I receive a status code of (\\d+) for listing all transactions$", (Integer code) -> {
            Assertions.assertEquals(code, status);
        });

        Given("^I have a valid token for role \"([^\"]*)\" to create transaction$", (String role) -> {
            if (role.equals("user"))
                token = VALID_TOKEN_USER;
        });
        And("^I have a valid transaction object with amount \"([^\"]*)\" and fromAccount \"([^\"]*)\" and toAccount \"([^\"]*)\" and TransactionType \"([^\"]*)\"$", (String amount, String fromaccount, String toaccount, String transactionType) -> {
            dtos = new TransactionDTO();
            dtos.setAmount(Double.parseDouble(amount));
            dtos.setFromAccount(fromaccount);
            dtos.setToAccount(toaccount);
            dtos.setTransactionType(transactionType);
        });
        When("^I call the post transaction endpoint$", () -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            request = new HttpEntity<>(mapper.writeValueAsString(dtos), httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/transactions", HttpMethod.POST, request, String.class);
            status = response.getStatusCodeValue();
        });

        Then("^I receive a status code of (\\d+) for creating a transaction$", (Integer code) -> {
            Assertions.assertEquals(code, status);
        });

        Given("^I have an invalid token for role \"([^\"]*)\" to access all transactions of users$", (String arg0) -> {
            token = INVALID_TOKEN;
        });

        Given("^I have an invalid token for role \"([^\"]*)\" to create transaction$", (String arg0) -> {
            token = INVALID_TOKEN;
        });


    }
}
