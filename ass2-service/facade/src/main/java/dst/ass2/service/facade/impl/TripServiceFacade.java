package dst.ass2.service.facade.impl;

import dst.ass2.service.api.trip.*;
import dst.ass2.service.api.trip.rest.ITripServiceResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/trips")
public class TripServiceFacade implements ITripServiceResource {

    @Override
    @GET
    @Path("/{id}")
    public Response getTrip(@PathParam("id") Long tripId) throws EntityNotFoundException {
        throw new RuntimeException("Unimplemented");

    }

    @Override
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTrip(@FormParam("riderId") Long riderId,
                               @FormParam("pickupId") Long pickupId,
                               @FormParam("destinationId") Long destinationId)
            throws EntityNotFoundException {
        throw new RuntimeException("Unimplemented");

    }

    @Override
    @DELETE
    @Path("/{id}")
    public Response deleteTrip(@PathParam("id") Long tripId) throws EntityNotFoundException {
        throw new RuntimeException("Unimplemented");

    }


    @Override
    @POST
    @Path("/{id}/stops")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStop(@PathParam("id") Long tripId,
                            @FormParam("locationId") Long locationId)
            throws EntityNotFoundException {
        throw new RuntimeException("Unimplemented");


    }

    @Override
    @DELETE
    @Path("/{id}/stops/{locationId}")
    public Response removeStop(@PathParam("id") Long tripId,
                               @PathParam("locationId") Long locationId)
            throws EntityNotFoundException {

        throw new RuntimeException("Unimplemented");

    }

    @Override
    @PATCH
    @Path("/{id}/confirm")
    public Response confirm(@PathParam("id") Long tripId) throws EntityNotFoundException, InvalidTripException {
        throw new RuntimeException("Unimplemented");

    }


    @Override
    @POST
    @Path("/{id}/match")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response match(@PathParam("id") Long tripId,
                          MatchDTO matchDTO)
            throws EntityNotFoundException, DriverNotAvailableException {
        throw new RuntimeException("Unimplemented");

    }

    @Override
    @PATCH
    @Path("/{id}/cancel")
    public Response cancel(@PathParam("id") Long tripId) throws EntityNotFoundException {
        throw new RuntimeException("Unimplemented");

    }

    @Override
    @POST
    @Path("/{id}/complete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response complete(@PathParam("id") Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        throw new RuntimeException("Unimplemented");
    }
}
