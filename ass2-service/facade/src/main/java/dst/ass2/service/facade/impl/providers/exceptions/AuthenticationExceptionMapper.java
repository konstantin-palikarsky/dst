package dst.ass2.service.facade.impl.providers.exceptions;

import dst.ass2.service.api.auth.AuthenticationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

    @Override
    public Response toResponse(AuthenticationException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .build();
    }

}
