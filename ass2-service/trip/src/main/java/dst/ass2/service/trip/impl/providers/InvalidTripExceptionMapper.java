package dst.ass2.service.trip.impl.providers;

import dst.ass2.service.api.trip.InvalidTripException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class InvalidTripExceptionMapper implements ExceptionMapper<InvalidTripException> {
    @Override
    public Response toResponse(InvalidTripException exception) {
        return Response.status(Response.Status.EXPECTATION_FAILED).build();
    }

}