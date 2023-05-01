package dst.ass2.service.api.auth.rest;

import dst.ass2.service.api.auth.AuthenticationException;
import dst.ass2.service.api.auth.NoSuchUserException;

import javax.ws.rs.core.Response;

/**
 * The IAuthenticationResource exposes parts of the {@code IAuthenticationService} as a RESTful interface.
 */
public interface IAuthenticationResource {

    Response authenticate(String email, String password)
            throws NoSuchUserException, AuthenticationException;

}
