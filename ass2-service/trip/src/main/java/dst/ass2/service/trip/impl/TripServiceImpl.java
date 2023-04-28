package dst.ass2.service.trip.impl;

import dst.ass1.jpa.dao.ILocationDAO;
import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ILocation;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;
import dst.ass1.jpa.model.impl.Trip;
import dst.ass2.service.api.trip.*;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.stream.Collectors;

@Singleton
@Named
public class TripServiceImpl implements ITripService {

    @Inject
    private ITripDAO tripRepository;
    @Inject
    private IRiderDAO riderRepository;
    @Inject
    private ILocationDAO locationRepository;


    @Override
    public TripDTO create(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException {
        /**
         * Creates and persists a Trip, sets the state to CREATED and calculates an initial fare estimation for
         * the route 'pickupId - destinationId'
         *
         * @param riderId       the id of the rider, who is planning a trip
         * @param pickupId      the id of the pickupId location
         * @param destinationId the id of the destinationId location
         * @return a TripDTO corresponding to the persisted Trip and includes the fare (null if route is invalid)
         * @throws EntityNotFoundException if the rider or one of the locations doesn't exist
         */

        var rider = riderRepository.findById(riderId);
        var pickup = locationRepository.findById(pickupId);
        var destination = locationRepository.findById(destinationId);

        if (rider == null || pickup == null || destination == null) {
            var missingAttribute = rider == null ? "rider " : "";
            missingAttribute += pickup == null ? "pickup " : "";
            missingAttribute += destination == null ? "destination " : "";

            throw new EntityNotFoundException("No such " + missingAttribute);
        }

        ITrip newTrip = new Trip();
        newTrip.setRider(rider);
        newTrip.setPickup(pickup);
        newTrip.setDestination(destination);
        newTrip.setState(TripState.CREATED);

        ITrip persistedTrip = tripRepository.save(newTrip);


        return mapTripToDto(persistedTrip);
    }

    @Override
    public void confirm(Long tripId) throws EntityNotFoundException, IllegalStateException, InvalidTripException {

    }

    @Override
    public void match(Long tripId, MatchDTO match) throws EntityNotFoundException, DriverNotAvailableException, IllegalStateException {

    }

    @Override
    public void complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {

    }

    @Override
    public void cancel(Long tripId) throws EntityNotFoundException {

    }

    @Override
    public boolean addStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {
        return false;
    }

    @Override
    public boolean removeStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {
        return false;
    }

    @Override
    public void delete(Long tripId) throws EntityNotFoundException {

    }

    @Override
    public TripDTO find(Long tripId) {
        return null;
    }

    private TripDTO mapTripToDto(ITrip trip) {
        var dto = new TripDTO();
        dto.setDestinationId(trip.getDestination().getId());
        dto.setPickupId(trip.getPickup().getId());
        dto.setStops(trip.getStops().stream().map(ILocation::getId).collect(Collectors.toList()));
        dto.setRiderId(trip.getRider().getId());
        dto.setId(trip.getId());

        return dto;
    }
}
