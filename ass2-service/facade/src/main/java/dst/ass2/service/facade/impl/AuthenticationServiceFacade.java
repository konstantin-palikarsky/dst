package dst.ass2.service.facade.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.rest.IAuthenticationResource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AuthenticationServiceFacade implements IAuthenticationResource {

    @Override
    @POST
    @Path("/authenticate")
    public Response authenticate(String email, String password) throws NoSuchUserException, AuthenticationException {
        throw new RuntimeException("Unimplemented");
    }
}
