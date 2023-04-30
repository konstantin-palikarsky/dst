package dst.ass2.service.trip.impl;

import dst.ass2.service.api.trip.*;
import dst.ass2.service.api.trip.rest.ITripServiceResource;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/trips")
public class TripServiceResource implements ITripServiceResource {

    @Inject
    TripService tripService;

    @Override
    @GET
    @Path("/{id}")
    public Response getTrip(@PathParam("id") Long tripId) throws EntityNotFoundException {
        var tripDto = tripService.find(tripId);

        if (tripDto == null) {
            throw new EntityNotFoundException("found no trip");
        }

        return Response.ok().entity(tripDto).build();
    }

    @Override
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTrip(@FormParam("riderId") Long riderId,
                               @FormParam("pickupId") Long pickupId,
                               @FormParam("destinationId") Long destinationId)
            throws EntityNotFoundException {

            var tripDto = tripService.create(riderId, pickupId, destinationId);
            return Response.status(Response.Status.CREATED).entity(tripDto.getId()).build();
    }

    @Override
    @DELETE
    @Path("/{id}")
    public Response deleteTrip(@PathParam("id") Long tripId) throws EntityNotFoundException {
        tripService.delete(tripId);

        return Response.status(Response.Status.OK).build();
    }


    @Override
    @POST
    @Path("/{id}/stops")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStop(@PathParam("id") Long tripId,
                            @FormParam("locationId") Long locationId)
            throws EntityNotFoundException {

        var tripDto = new TripDTO();
        tripDto.setId(tripId);


        var added = tripService.addStop(tripDto, locationId);

        if (!added) {
            throw new IllegalArgumentException("");
        }

        return Response.status(Response.Status.OK).entity(tripDto.getFare()).build();
    }

    @Override
    @DELETE
    @Path("/trips/{id}/stops/{locationId}")
    public Response removeStop(@PathParam("id") Long tripId,
                               @PathParam("locationId") Long locationId)
            throws EntityNotFoundException {

        var tripDto = new TripDTO();
        tripDto.setId(tripId);

        var removed = tripService.removeStop(tripDto, locationId);

        if (!removed) {
            throw new IllegalArgumentException("");
        }

        return Response.status(Response.Status.OK).entity(tripDto.getFare()).build();
    }

    @Override
    @PATCH
    @Path("/trips/{id}/confirm")
    public Response confirm(@PathParam("id") Long tripId) throws EntityNotFoundException, InvalidTripException {
        tripService.confirm(tripId);

        return Response.status(Response.Status.OK).build();
    }


    @Override
    @POST
    @Path("/trips/{id}/match")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response match(@PathParam("id") Long tripId,
                          MatchDTO matchDTO)
            throws EntityNotFoundException, DriverNotAvailableException {
        tripService.match(tripId, matchDTO);

        return Response.status(Response.Status.OK).build();
    }

    @Override
    @PATCH
    @Path("/trips/{id}/cancel")
    public Response cancel(@PathParam("id") Long tripId) throws EntityNotFoundException {

        tripService.cancel(tripId);

        return Response.status(Response.Status.OK).build();
    }

    @Override
    @POST
    @Path("/trips/{id}/complete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response complete(@PathParam("id") Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        tripService.complete(tripId, tripInfoDTO);

        return Response.status(Response.Status.OK).build();
    }

}
