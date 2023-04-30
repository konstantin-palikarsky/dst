package dst.ass2.service.facade.impl;

import dst.ass2.service.api.auth.NoSuchUserException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class NoSuchUserExceptionMapper implements ExceptionMapper<NoSuchUserException> {
    @Override
    public Response toResponse(NoSuchUserException exception) {
        return Response.status(Response.Status.EXPECTATION_FAILED).build();
    }

}