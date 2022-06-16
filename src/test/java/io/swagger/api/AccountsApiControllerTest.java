package io.swagger.api;

import io.swagger.model.Account;
import io.swagger.model.AccountType;
import io.swagger.model.Role;
import io.swagger.model.User;
import io.swagger.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountsApiControllerTest {

    @Autowired
    private AccountService accountService;

    User testuser1;
    Account currentAccount;
    Account savingAccount;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {

        testuser1 = new User();
        testuser1.setUserId(1);
        testuser1.setFullname("Wayne Rigsby");
        testuser1.setUsername("rigs");
        testuser1.setPassword(passwordEncoder.encode("rigs123"));
        testuser1.setRoles(Arrays.asList(Role.ROLE_USER));

        testuser1.setPassword(passwordEncoder.encode("pat123"));
        savingAccount = new Account("NL43INHO4186520410", testuser1, 300.00, AccountType.saving);
        currentAccount = new Account("NL65INHO2095310012", testuser1, 800.00, AccountType.current);

    }

    @Test
    public void canCreateSavingAccount() {
        Account account = accountService.saveAccount(savingAccount);
        assertEquals(account.getIBAN(), savingAccount.getIBAN());
    }

    @Test
    public void canCreateCurrentAccount() {
        Account account = accountService.saveAccount(currentAccount);
        assertEquals(account.getIBAN(), currentAccount.getIBAN());
    }

    @Test
    public void canFindAUserAndAccountType() {
        Account account = accountService.findByUserAndAccountType(testuser1, AccountType.current);
        assertEquals(account.getAccountType(), AccountType.current);
        assertEquals(account.getUser().getUserId(), testuser1.getUserId());
    }

    @Test
    public void shouldFindAllAccountsOfUser() {
        List<Account> accounts = accountService.getAllAccountsOfUser(testuser1);
        assertNotNull(accounts);
    }
}