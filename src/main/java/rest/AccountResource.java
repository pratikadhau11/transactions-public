package rest;

import domain.account.Account;

import service.AccountService;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Optional;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {

    private AccountService accountService;


    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @POST
    public Response createAccount(CreateAccountRequest createAccountRequest, @Context UriInfo uriInfo) {
        Account account = accountService.createAccount(createAccountRequest);
        return Response
                .created(UriBuilder
                        .fromUri(uriInfo.getRequestUri())
                        .path(account.id).build())
                .build();
    }

    @GET
    @Path("/{id}")
    public Optional<Account> getAccount(@PathParam("id") String id) {
        return accountService.getAccount(id);
    }


}
