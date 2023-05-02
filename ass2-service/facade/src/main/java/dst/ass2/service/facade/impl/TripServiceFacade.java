package dst.ass2.service.facade.impl;

import dst.ass2.service.api.trip.*;
import dst.ass2.service.api.trip.rest.ITripServiceResource;
import dst.ass2.service.facade.impl.providers.filter.RequireAuth;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

@Path("/trips")
public class TripServiceFacade implements ITripServiceResource {
    private static final String TRIP_SERVICE_URL = "http://localhost:8091/";
    private static final Logger LOG = LoggerFactory.getLogger(TripServiceFacade.class);

    private ITripServiceResource realService;

    @PostConstruct
    private void setup() {
        var configuration = new ClientConfig()
                .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
                .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);


        Client client = ClientBuilder.newClient(configuration);
        WebTarget webTarget = client.target(TRIP_SERVICE_URL);

        realService = WebResourceFactory.newResource(ITripServiceResource.class, webTarget);
    }

    @Override
    @RequireAuth
    public Response getTrip(Long tripId)
            throws EntityNotFoundException {
        LOG.info("Getting trip {}", tripId);

        return realService.getTrip(tripId);
    }

    @Override
    @RequireAuth
    public Response createTrip(Long riderId,
                               Long pickupId,
                               Long destinationId)
            throws EntityNotFoundException {
        LOG.info("Creating trip for rider {}, pickup {}, and destination {}",
                riderId, pickupId, destinationId);

        return realService.createTrip(riderId, pickupId, destinationId);
    }

    @Override
    @RequireAuth
    public Response deleteTrip(Long tripId)
            throws EntityNotFoundException {
        LOG.info("Deleting trip {}", tripId);

        return realService.deleteTrip(tripId);
    }


    @Override
    @RequireAuth
    public Response addStop(Long tripId,
                            Long locationId)
            throws EntityNotFoundException {
        LOG.info("Adding stop {} to trip {}", locationId, tripId);

        return realService.addStop(tripId, locationId);
    }

    @Override
    @RequireAuth
    public Response removeStop(Long tripId,
                               Long locationId)
            throws EntityNotFoundException {
        LOG.info("Removing stop {} from trip {}", locationId, tripId);

        return realService.removeStop(tripId, locationId);
    }

    @Override
    @RequireAuth
    public Response confirm(Long tripId)
            throws EntityNotFoundException, InvalidTripException {
        LOG.info("Confirming trip {}", tripId);

        return realService.confirm(tripId);
    }


    @Override
    @RequireAuth
    public Response match(Long tripId,
                          MatchDTO matchDTO)
            throws EntityNotFoundException, DriverNotAvailableException {
        LOG.info("Matching trip {}", tripId);

        return realService.match(tripId, matchDTO);
    }

    @Override
    @RequireAuth
    public Response cancel(Long tripId)
            throws EntityNotFoundException {
        LOG.info("Cancelling trip {}", tripId);

        return realService.cancel(tripId);
    }

    @Override
    @RequireAuth
    public Response complete(Long tripId, TripInfoDTO tripInfoDTO)
            throws EntityNotFoundException {
        LOG.info("Completing trip {}, on {}", tripId, tripInfoDTO.getCompleted());

        return realService.complete(tripId, tripInfoDTO);
    }

}
