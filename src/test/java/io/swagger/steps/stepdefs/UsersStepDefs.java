package io.swagger.steps.stepdefs;
import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.swagger.model.dto.LoginDTO;
import io.swagger.model.dto.LoginResponseDTO;
import org.junit.Assert;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class UsersStepDefs {

    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();

    ResponseEntity<String> responseEntity;

    String response;



    String baseURL = "http://localhost:8089/bankAPI/users";

    private ObjectMapper objectMapper = new ObjectMapper();

    String VALID_TOKEN_ADMIN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b21teSIsImF1dGgiOlt7ImF1dGhvcml0eSI6IlJPTEVfVVNFUiJ9LHsiYXV0aG9yaXR5IjoiUk9MRV9BRE1JTiJ9XSwiaWF0IjoxNjU1NDIzNzI1LCJleHAiOjE2NTU0MjczMjV9.tu6eIW484hWOlR5u2HYrZDkyWvWQLGpBakpWCBqfx0w";
    private int status;

    @When("I receive all users")
    public void iReceiveAllUsers() throws URISyntaxException {
        headers.clear();
        headers.add("Authorization",  "Bearer " + VALID_TOKEN_ADMIN);

        URI uri = new URI(baseURL);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        responseEntity = restTemplate.exchange(uri, HttpMethod.GET ,entity ,String.class);
        status = responseEntity.getStatusCodeValue();
    }

    @Then("I get http status {int}")
    public void iGetHttpStatus(int code) {
        Assert.assertEquals(status, code);
    }
}
