package dst.ass2.service.facade.impl;

import dst.ass2.service.api.trip.*;
import dst.ass2.service.api.trip.rest.ITripServiceResource;
import dst.ass2.service.facade.impl.providers.filter.RequireAuth;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/trips")
public class TripServiceFacade implements ITripServiceResource {

    private ITripServiceResource realService;

    @PostConstruct
    private void setup() {
        var configuration = new ClientConfig()
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);


        Client client = ClientBuilder.newClient(configuration);
        //Hardcoded for now
        WebTarget webTarget = client.target("http://localhost:8091/");

        realService = WebResourceFactory.newResource(ITripServiceResource.class, webTarget);
    }

    @Override
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireAuth
    public Response getTrip(@PathParam("id") Long tripId)
            throws EntityNotFoundException {

        return realService.getTrip(tripId);
    }

    @Override
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RequireAuth
    public Response createTrip(@FormParam("riderId") Long riderId,
                               @FormParam("pickupId") Long pickupId,
                               @FormParam("destinationId") Long destinationId)
            throws EntityNotFoundException {

        return realService.createTrip(riderId, pickupId, destinationId);
    }

    @Override
    @DELETE
    @Path("/{id}")
    @RequireAuth
    public Response deleteTrip(@PathParam("id") Long tripId)
            throws EntityNotFoundException {

        return realService.deleteTrip(tripId);
    }


    @Override
    @POST
    @Path("/{id}/stops")
    @Produces(MediaType.APPLICATION_JSON)
    @RequireAuth
    public Response addStop(@PathParam("id") Long tripId,
                            @FormParam("locationId") Long locationId)
            throws EntityNotFoundException {

        return realService.addStop(tripId, locationId);
    }

    @Override
    @DELETE
    @Path("/{id}/stops/{locationId}")
    @RequireAuth
    public Response removeStop(@PathParam("id") Long tripId,
                               @PathParam("locationId") Long locationId)
            throws EntityNotFoundException {

        return realService.removeStop(tripId, locationId);
    }

    @Override
    @PATCH
    @Path("/{id}/confirm")
    @RequireAuth
    public Response confirm(@PathParam("id") Long tripId)
            throws EntityNotFoundException, InvalidTripException {

        return realService.confirm(tripId);
    }


    @Override
    @POST
    @Path("/{id}/match")
    @Consumes(MediaType.APPLICATION_JSON)
    @RequireAuth
    public Response match(@PathParam("id") Long tripId,
                          MatchDTO matchDTO)
            throws EntityNotFoundException, DriverNotAvailableException {

        return realService.match(tripId, matchDTO);
    }

    @Override
    @PATCH
    @Path("/{id}/cancel")
    @RequireAuth
    public Response cancel(@PathParam("id") Long tripId)
            throws EntityNotFoundException {

        return realService.cancel(tripId);
    }

    @Override
    @POST
    @Path("/{id}/complete")
    @Consumes(MediaType.APPLICATION_JSON)
    @RequireAuth
    public Response complete(@PathParam("id") Long tripId, TripInfoDTO tripInfoDTO)
            throws EntityNotFoundException {

        return realService.complete(tripId, tripInfoDTO);
    }

}
