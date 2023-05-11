package dst.ass2.service.api.trip.rest;

import dst.ass2.service.api.trip.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * This interface exposes the {@code ITripService} as a RESTful interface.
 */
public interface ITripServiceResource {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    Response createTrip(@FormParam("riderId") Long riderId,
                        @FormParam("pickupId") Long pickupId,
                        @FormParam("destinationId") Long destinationId)
            throws EntityNotFoundException;

    @PATCH
    @Path("/{id}/confirm")
    Response confirm(@PathParam("id") Long tripId)
            throws EntityNotFoundException, InvalidTripException;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getTrip(@PathParam("id") Long tripId) throws EntityNotFoundException;

    @DELETE
    @Path("/{id}")
    Response deleteTrip(@PathParam("id") Long tripId) throws EntityNotFoundException;

    @POST
    @Path("/{id}/stops")
    @Produces(MediaType.APPLICATION_JSON)
    Response addStop(@PathParam("id") Long tripId, @FormParam("locationId") Long locationId) throws EntityNotFoundException;


    @DELETE
    @Path("/{id}/stops/{locationId}")
    Response removeStop(@PathParam("id") Long tripId,
                        @PathParam("locationId") Long locationId)
            throws EntityNotFoundException;

    @POST
    @Path("/{id}/match")
    @Consumes(MediaType.APPLICATION_JSON)
    Response match(@PathParam("id") Long tripId,
                   MatchDTO matchDTO)
            throws EntityNotFoundException, DriverNotAvailableException;

    @POST
    @Path("/{id}/complete")
    @Consumes(MediaType.APPLICATION_JSON)
    Response complete(@PathParam("id") Long tripId, TripInfoDTO tripInfoDTO)
            throws EntityNotFoundException;
    @PATCH
    @Path("/{id}/cancel")
    Response cancel(@PathParam("id") Long tripId)
            throws EntityNotFoundException;

}
