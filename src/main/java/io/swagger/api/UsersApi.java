package io.swagger.api;

import io.swagger.model.Account;
import java.math.BigDecimal;

import io.swagger.model.dto.*;
import io.swagger.model.User;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-05-13T15:15:19.174Z[GMT]")
@Validated
@CrossOrigin(origins = "*", allowedHeaders = "*")
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
    ResponseEntity<List<UserResponseDTO>> usersGet(@NotNull @Parameter(in = ParameterIn.QUERY, description = "skips the list of users" ,required=false,schema=@Schema()) @Valid @RequestParam(value = "skip", required = false) Integer skip, @NotNull @Parameter(in = ParameterIn.QUERY, description = "fetch the needed amount of users" ,required=false,schema=@Schema()) @Valid @RequestParam(value = "limit", required = false) Integer limit, @NotNull @Parameter(in = ParameterIn.QUERY, description = "fetch the users with or with out account" ,required=false,schema=@Schema()) @Valid @RequestParam(value = "withOutAccount", required = false) Integer withOutAccount);


    @Operation(summary = "Login a user", description = "By passing in the appropriate options, you can search for user data in the system ", tags={ "customer", "employee" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "user logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Invalid email or password format") })
    @RequestMapping(value = "/users/login",
            produces = { "application/json" },
            consumes = { "application/json" },
            method = RequestMethod.POST)
    LoginResponseDTO usersLoginPost(@Parameter(in = ParameterIn.DEFAULT, description = "New account details", required=true, schema=@Schema()) @Valid @RequestBody LoginDTO body);


    @Operation(summary = "Create user", description = "Adds a user to the system", tags={ "employee", "customer" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),

            @ApiResponse(responseCode = "400", description = "invalid input, object invalid"),

            @ApiResponse(responseCode = "404", description = "No users found"),

            @ApiResponse(responseCode = "500", description = "Internal server error") })
    @RequestMapping(value = "/users/register",
            produces = { "application/json" },
            consumes = { "application/json" },
            method = RequestMethod.POST)
    ResponseEntity<UserResponseDTO> usersPost(@Parameter(in = ParameterIn.DEFAULT, description = "User to add", schema=@Schema()) @Valid @RequestBody UserDTO body);


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
    ResponseEntity<List<AccountResponseDTO>> usersUserIdAccountsGet(@Parameter(in = ParameterIn.PATH, description = "Numeric ID of the user to get", required=true, schema=@Schema()) @PathVariable("userId") Integer userId);


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
    ResponseEntity<UserResponseDTO> usersUserIdGet(@Parameter(in = ParameterIn.PATH, description = "Numeric ID of the user to get", required=true, schema=@Schema()) @PathVariable("userId") Integer userId);


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
    ResponseEntity<UserTotalBalanceResponseDTO> usersUserIdTotalBalanceGet(@Parameter(in = ParameterIn.PATH, description = "Numeric ID of the user to get", required=true, schema=@Schema()) @PathVariable("userId") Integer userId);

    @Operation(summary = "Gets total balance of the user", description = "Returns the total balance of all accounts for a user ", security = {
            @SecurityRequirement(name = "bearerAuth")    }, tags={ "customer", "employee" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a user", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Invalid id format"),

            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid")})
    @RequestMapping(value = "/users/loggedInUser",
            produces = { "application/json" },
            method = RequestMethod.GET)
    ResponseEntity<UserResponseDTO> loggedInUserGet();


    @Operation(summary = "update user details", description = "updates the day and transaction limit for a user", security = {
            @SecurityRequirement(name = "bearerAuth")    }, tags={"customer", "employee"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Day and Transaction limit updated"),

            @ApiResponse(responseCode = "400", description = "invalid input, object invalid"),

            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),

            @ApiResponse(responseCode = "404", description = "user not found") })
    @RequestMapping(value = "/users/{userId}",
            consumes = { "application/json" },
            method = RequestMethod.PUT)
    ResponseEntity<Void> updateUserDetails(@Parameter(in = ParameterIn.PATH, description = "userId of updated user", required=true, schema=@Schema()) @PathVariable("userId") Integer userId, @Valid @RequestBody UserUpdateDTO body);
}