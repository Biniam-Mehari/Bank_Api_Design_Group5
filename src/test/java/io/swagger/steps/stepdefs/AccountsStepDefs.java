package io.swagger.steps.stepdefs;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java8.En;
import io.swagger.model.dto.AccountDTO;
import io.swagger.service.UserService;
import io.swagger.steps.BaseStepDefinations;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

public class AccountsStepDefs extends BaseStepDefinations implements En {

    private static final String VALID_TOKEN_USER = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbXJpc2giLCJhdXRoIjpbeyJhdXRob3JpdHkiOiJST0xFX1VTRVIifV0sImlhdCI6MTY1NTkwMjMwMiwiZXhwIjoxNjU1OTA1OTAyfQ.6_Y033QiO66dvqVHceCEqyOaPWutsm4hRZKSIJ06ocg";
    private static final String VALID_TOKEN_ADMIN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9LHsiYXV0aG9yaXR5IjoiUk9MRV9BRE1JTiJ9XSwiaWF0IjoxNjU1NjY3NTk0LCJleHAiOjE2NTYyNzIzOTR9.XI7nat8c9C1oxrLkFydif3C6qtdzIIg6OGoiRcjLr6E";
    private static final String INVALID_TOKEN = "invalidtoken";

    private final HttpHeaders httpHeaders = new HttpHeaders();
    private final TestRestTemplate restTemplate = new TestRestTemplate();

    private final ObjectMapper mapper = new ObjectMapper();

    private ResponseEntity<String> response;
    private HttpEntity<String> request;
    private Integer status;

    private String token = null;

    private AccountDTO accountDTO;

    public AccountsStepDefs() {
        Given("^I have an valid token for role \"([^\"]*)\" to access accounts$", (String role) -> {
            if (role.equals("admin")) {
                token = VALID_TOKEN_ADMIN;
            }
        });

        When("^I call get accounts by IBAN \"([^\"]*)\"$", (String iban) -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            request = new HttpEntity<>(null, httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/accounts/" + iban, HttpMethod.GET, new HttpEntity<>(null,httpHeaders), String.class);
            status = response.getStatusCodeValue();
        });

        Then("^I receive a status code of (\\d+)$", (Integer code) -> {
            Assertions.assertEquals(code, status);
        });

        // Senario 2
        Given("^I have an valid token for role \"([^\"]*)\" to access transactions$", (String role) -> {
             if (role.equals("user")) {
                 token = VALID_TOKEN_USER;
             }
             else if (role.equals("admin")) {
                 token = VALID_TOKEN_ADMIN;
             }
        });

        /*When("^I call get transactions by IBAN \"([^\"]*)\"$", (String iban) -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            request = new HttpEntity<>(null, httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/accounts/" + iban + "/transactions?startDate=2022-04-03T10:25:57&endDate=2022-05-27T16:27:39&skip=0&limit=4", HttpMethod.GET, new HttpEntity<>(null,httpHeaders), String.class);
            status = response.getStatusCodeValue();
        });*/

        When("^I call get transactions by IBAN \"([^\"]*)\" with startDate \"([^\"]*)\" and endDate \"([^\"]*)\" and skip (\\d+) and limit (\\d+)$", (String iban, String startDate, String endDate, Integer skip, Integer limit) -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            request = new HttpEntity<>(null, httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/accounts/" + iban + "/transactions?startDate=" + startDate + "&endDate=" + endDate + "&skip=" + skip + "&limit=" + limit, HttpMethod.GET,request, String.class);
            status = response.getStatusCodeValue();
        });
        Given("^I have an invalid token for role \"([^\"]*)\" to access transactions$", (String arg0) -> {
            token = INVALID_TOKEN;
        });
        Given("^I have an valid token for role \"([^\"]*)\" to access account transactions$", (String role) -> {

            if (role.equals("user")){
                token = VALID_TOKEN_USER;
            }
        });
        When("^I call get transactions by IBAN \"([^\"]*)\" by amount$", (String iban) -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            request = new HttpEntity<>(null, httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/accounts/" + iban + "/transactions/byamount?amount=900.00&operator==&skip=0&limit=5", HttpMethod.GET, new HttpEntity<>(null,httpHeaders), String.class);
            status = response.getStatusCodeValue();
        });
        When("^I call get transactions by IBAN \"([^\"]*)\" by operator \"([^\"]*)\"$", (String iban, String operator) -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization", "Bearer " + token);
            request = new HttpEntity<>(null, httpHeaders);

            response = restTemplate.exchange(getBaseUrl() + "bankAPI/accounts/" + iban + "/transactions/byamount?amount=900.00&operator==" + operator + "&skip=0&limit=5", HttpMethod.GET, new HttpEntity<>(null,httpHeaders), String.class);
            status = response.getStatusCodeValue();
        });
        Given("^I have an valid token for role \"([^\"]*)\" to create account$", (String user) -> {
            if(user.equals("admin")){
                token = VALID_TOKEN_ADMIN;
            }
            else if (user.equals("user"))
                token = VALID_TOKEN_USER;
        });
        And("^I gave valid (\\d+) and account type \"([^\"]*)\"$", (Integer userId, String accountType) -> {
            accountDTO = new AccountDTO();
            accountDTO.setUserId(userId);
            accountDTO.setAccountType(accountType);
        });

        When("^I call post account$", () -> {
            httpHeaders.clear();
            httpHeaders.add("Authorization",  "Bearer " + token);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            request = new HttpEntity<>(mapper.writeValueAsString(accountDTO), httpHeaders);
            response = restTemplate.exchange(getBaseUrl() + "bankAPI/accounts", HttpMethod.POST, request, String.class);
            status = response.getStatusCodeValue();
        });


    }
}
