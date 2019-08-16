package rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.account.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import service.AccountService;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountResourceTest {
    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountResource accountResource;

    @Mock
    UriInfo uriInfo;

    @Before
    public void setUp() {
        Mockito.reset(uriInfo, accountService);
    }

    @Test
    public void shouldCreateAccount() throws IOException {

        CreateAccountRequest createAccountRequest = new ObjectMapper()
                .readValue("{\"name\": \"tom cruise\"}",
                        CreateAccountRequest.class);
        URI sample = URI.create("sample");
        when(uriInfo.getRequestUri()).thenReturn(sample);
        when(accountService.createAccount(createAccountRequest))
                .thenReturn(new Account("1", createAccountRequest.getName()));

        Response response = accountResource.createAccount(createAccountRequest, uriInfo);

        assertEquals(201, response.getStatus());
        assertEquals(UriBuilder.fromUri(sample).path("1").toString(), response.getHeaderString("location"));
    }

    @Test
    public void shouldGetAllAccounts() throws IOException {
        Account tom = new Account("1", "Tom");
        when(accountService.getAccounts())
                .thenReturn(Arrays.asList(tom));

        List<Account> accounts = accountResource.getAccounts();

        assertEquals(1, accounts.size());
        assertEquals(tom.id, accounts.get(0).id);
        verify(accountService).getAccounts();
    }

    @Test
    public void shouldGetOneAccount() throws IOException {
        Account tom = new Account("1", "Tom");
        String id = "id";
        when(accountService.getAccount(id)).thenReturn(Optional.of(tom));

        Optional<Account> account = accountResource.getAccount(id);
        assertEquals(tom.id, account.get().id);
        verify(accountService).getAccount(id);
    }
}
