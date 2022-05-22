/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.34).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.model.Account;
import java.math.BigDecimal;
import io.swagger.model.dto.LoginResponseDTO;
import io.swagger.model.dto.TotalAmountResponseDTO;
import io.swagger.model.dto.LoginDTO;
import io.swagger.model.User;
import io.swagger.model.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-05-13T15:15:19.174Z[GMT]")
@Validated
public interface UsersApi {

    @Operation(summary = "Gets all users", description = "Gets all users in the system according the limit and skip and account ", security = {
        @SecurityRequirement(name = "bearerAuth")    }, tags={ "employee" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Returns all users", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))),
        
        @ApiResponse(responseCode = "400", description = "Bad request"),
        
        @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
        
        @ApiResponse(responseCode = "404", description = "No users found"),
        
        @ApiResponse(responseCode = "500", description = "Internal server error") })
    @RequestMapping(value = "/users",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<User>> usersGet(@NotNull @Parameter(in = ParameterIn.QUERY, description = "skips the list of users" ,required=true,schema=@Schema()) @Valid @RequestParam(value = "skip", required = true) Integer skip, @NotNull @Parameter(in = ParameterIn.QUERY, description = "fetch the needed amount of users" ,required=true,schema=@Schema()) @Valid @RequestParam(value = "limit", required = true) Integer limit, @NotNull @Parameter(in = ParameterIn.QUERY, description = "fetch the users with or with out account" ,required=true,schema=@Schema()) @Valid @RequestParam(value = "withOutAccount", required = true) BigDecimal withOutAccount);


    @Operation(summary = "Login a user", description = "By passing in the appropriate options, you can search for user data in the system ", tags={ "customer", "employee" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "user logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))),

        @ApiResponse(responseCode = "400", description = "Invalid email or password format") })
    @RequestMapping(value = "/users/login",
        produces = { "application/json" }, 
        consumes = { "application/json" }, 
        method = RequestMethod.POST)
    LoginResponseDTO usersLoginPost(@Parameter(in = ParameterIn.DEFAULT, description = "New account details", required=true, schema=@Schema()) @Valid @RequestBody LoginDTO body);


    @Operation(summary = "Create user", description = "Adds a user to the system", security = {
        @SecurityRequirement(name = "bearerAuth")    }, tags={ "employee", "customer" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "201", description = "User created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        
        @ApiResponse(responseCode = "400", description = "invalid input, object invalid"),
        
        @ApiResponse(responseCode = "404", description = "No users found"),
        
        @ApiResponse(responseCode = "500", description = "Internal server error") })
    @RequestMapping(value = "/users",
        produces = { "application/json" }, 
        consumes = { "application/json" }, 
        method = RequestMethod.POST)
    ResponseEntity<User> usersPost(@Parameter(in = ParameterIn.DEFAULT, description = "User to add", schema=@Schema()) @Valid @RequestBody UserDTO body);


    @Operation(summary = "fetch accounts of a userId", description = "Returns an account for a user ", security = {
        @SecurityRequirement(name = "bearerAuth")    }, tags={ "customer", "employee" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Returns a user", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Account.class)))),
        
        @ApiResponse(responseCode = "400", description = "Invalid id format"),
        
        @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
        
        @ApiResponse(responseCode = "404", description = "Account based on this user id is not found"),
        
        @ApiResponse(responseCode = "500", description = "Internal server error") })
    @RequestMapping(value = "/users/{userId}/accounts",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<Account>> usersUserIdAccountsGet(@Parameter(in = ParameterIn.PATH, description = "Numeric ID of the user to get", required=true, schema=@Schema()) @PathVariable("userId") Integer userId);


    @Operation(summary = "Gets data of the user", description = "By passing in the appropriate options, you can search for user data in the system ", security = {
        @SecurityRequirement(name = "bearerAuth")    }, tags={ "customer", "employee" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Returns a user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        
        @ApiResponse(responseCode = "400", description = "Invaild id format"),
        
        @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
        
        @ApiResponse(responseCode = "404", description = "User with this id is not found"),
        
        @ApiResponse(responseCode = "500", description = "Internal server error") })
    @RequestMapping(value = "/users/{userId}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<User> usersUserIdGet(@Parameter(in = ParameterIn.PATH, description = "Numeric ID of the user to get", required=true, schema=@Schema()) @PathVariable("userId") Integer userId);


    @Operation(summary = "Gets total balance of the user", description = "Returns the total balance of all accounts for a user ", security = {
        @SecurityRequirement(name = "bearerAuth")    }, tags={ "customer", "employee" })
    @ApiResponses(value = { 
        @ApiResponse(responseCode = "200", description = "Returns a user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TotalAmountResponseDTO.class))),
        
        @ApiResponse(responseCode = "400", description = "Invalid id format"),
        
        @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
        
        @ApiResponse(responseCode = "404", description = "Total amount of user with this id is not found"),
        
        @ApiResponse(responseCode = "500", description = "Internal server error") })
    @RequestMapping(value = "/users/{userId}/totalBalance",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<TotalAmountResponseDTO> usersUserIdTotalBalanceGet(@Parameter(in = ParameterIn.PATH, description = "Numeric ID of the user to get", required=true, schema=@Schema()) @PathVariable("userId") Integer userId);

}

