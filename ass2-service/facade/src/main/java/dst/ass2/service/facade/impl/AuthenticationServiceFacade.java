package dst.ass2.service.facade.impl;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;
import dst.ass2.service.api.auth.rest.IAuthenticationResource;
import dst.ass2.service.auth.client.IAuthenticationClient;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
public class AuthenticationServiceFacade implements IAuthenticationResource {

    @Inject
    private IAuthenticationClient authClient;

    @Override
    @POST
    @Path("/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(@FormParam("email") String email,
                                 @FormParam("password") String password)
            throws NoSuchUserException, AuthenticationException {
        var auth = authClient.authenticate(email, password);

        return Response.ok().entity(auth).build();
    }
}
