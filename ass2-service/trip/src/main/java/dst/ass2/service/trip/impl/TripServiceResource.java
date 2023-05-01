package dst.ass2.service.trip.impl;

import dst.ass2.service.api.trip.*;
import dst.ass2.service.api.trip.rest.ITripServiceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/trips")
public class TripServiceResource implements ITripServiceResource {

    @Inject
    TripService tripService;

    private static final Logger LOG = LoggerFactory.getLogger(TripServiceResource.class);

    @Override
    @GET
    @Path("/{id}")
    public Response getTrip(@PathParam("id") Long tripId) throws EntityNotFoundException {
        LOG.info("Getting trip {}", tripId);
        var tripDto = tripService.find(tripId);

        if (tripDto == null) {
            throw new EntityNotFoundException("Trip not found");
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
        LOG.info("Creating trip for rider {}, pickup {}, and destination {}",
                riderId, pickupId, destinationId);

        var tripDto = tripService.create(riderId, pickupId, destinationId);
        return Response.status(Response.Status.CREATED).entity(tripDto.getId()).build();
    }

    @Override
    @DELETE
    @Path("/{id}")
    public Response deleteTrip(@PathParam("id") Long tripId) throws EntityNotFoundException {
        LOG.info("Deleting trip {}", tripId);

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
        LOG.info("Adding stop {} to trip {}", locationId, tripId);

        var tripDto = new TripDTO();
        tripDto.setId(tripId);


        var added = tripService.addStop(tripDto, locationId);

        if (!added) {
            throw new IllegalStateException("Stop Already Present");
        }

        return Response.status(Response.Status.OK).entity(tripDto.getFare()).build();

    }

    @Override
    @DELETE
    @Path("/{id}/stops/{locationId}")
    public Response removeStop(@PathParam("id") Long tripId,
                               @PathParam("locationId") Long locationId)
            throws EntityNotFoundException {
        LOG.info("Removing stop {} from trip {}", locationId, tripId);

        var tripDto = new TripDTO();
        tripDto.setId(tripId);

        var removed = tripService.removeStop(tripDto, locationId);

        if (!removed) {
            throw new IllegalStateException("No stop present");
        }

        return Response.status(Response.Status.OK).build();
    }

    @Override
    @PATCH
    @Path("/{id}/confirm")
    public Response confirm(@PathParam("id") Long tripId) throws EntityNotFoundException, InvalidTripException {
        LOG.info("Confirming trip {}", tripId);

        tripService.confirm(tripId);

        return Response.status(Response.Status.OK).build();
    }


    @Override
    @POST
    @Path("/{id}/match")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response match(@PathParam("id") Long tripId,
                          MatchDTO matchDTO)
            throws EntityNotFoundException, DriverNotAvailableException {
        LOG.info("Matching trip {}", tripId);

        tripService.match(tripId, matchDTO);

        return Response.status(Response.Status.OK).build();
    }

    @Override
    @PATCH
    @Path("/{id}/cancel")
    public Response cancel(@PathParam("id") Long tripId) throws EntityNotFoundException {
        LOG.info("Cancelling trip {}", tripId);

        tripService.cancel(tripId);

        return Response.status(Response.Status.OK).build();
    }

    @Override
    @POST
    @Path("/{id}/complete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response complete(@PathParam("id") Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        LOG.info("Completing trip {}, on {}", tripId, tripInfoDTO.getCompleted());

        tripService.complete(tripId, tripInfoDTO);

        return Response.status(Response.Status.OK).build();
    }

}
