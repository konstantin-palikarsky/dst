package dst.ass2.service.trip.impl;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.ILocationDAO;
import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ILocation;
import dst.ass1.jpa.model.IModelFactory;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;
import dst.ass2.service.api.match.IMatchingService;
import dst.ass2.service.api.trip.*;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.stream.Collectors;

@Singleton
@Named
@Transactional
@ManagedBean
public class TripService implements ITripService {

    @Inject
    private IDAOFactory daoFactory;

    @Inject
    private IMatchingService matchingService;

    @Inject
    private IModelFactory modelFactory;

    private ITripDAO tripRepository;
    private IRiderDAO riderRepository;
    private ILocationDAO locationRepository;

    @PostConstruct
    public void startup() {
        tripRepository = daoFactory.createTripDAO();
        riderRepository = daoFactory.createRiderDAO();
        locationRepository = daoFactory.createLocationDAO();
    }

    @Override
    public TripDTO create(Long riderId, Long pickupId, Long destinationId) throws EntityNotFoundException {
        var rider = riderRepository.findById(riderId);
        var pickup = locationRepository.findById(pickupId);
        var destination = locationRepository.findById(destinationId);

        if (rider == null || pickup == null || destination == null) {
            var missingAttribute = rider == null ? "rider " : "";
            missingAttribute += pickup == null ? "pickup " : "";
            missingAttribute += destination == null ? "destination " : "";

            throw new EntityNotFoundException("No such " + missingAttribute);
        }

        ITrip newTrip = modelFactory.createTrip();
        newTrip.setRider(rider);
        newTrip.setPickup(pickup);
        newTrip.setDestination(destination);
        newTrip.setState(TripState.CREATED);

        ITrip persistedTrip = tripRepository.save(newTrip);
        var dto = mapTripToDto(persistedTrip);
        MoneyDTO fare = safelyCalculateFare(dto);
        dto.setFare(fare);

        return dto;
    }

    @Override
    public void confirm(Long tripId) throws EntityNotFoundException, IllegalStateException, InvalidTripException {
        throw new RuntimeException();
    }

    @Override
    public void match(Long tripId, MatchDTO match) throws EntityNotFoundException, DriverNotAvailableException, IllegalStateException {
        throw new RuntimeException();
    }

    @Override
    public void complete(Long tripId, TripInfoDTO tripInfoDTO) throws EntityNotFoundException {
        throw new RuntimeException();
    }

    @Override
    public void cancel(Long tripId) throws EntityNotFoundException {
        throw new RuntimeException();
    }

    @Override
    public boolean addStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {

        var locationEntity = locationRepository.findById(locationId);
        if (locationEntity == null) {
            throw new EntityNotFoundException("Cannot add a stop at an non-existent location");
        }

        if (trip.getStops().contains(locationId)) {
            return false;
        }

        var tripEntity = tripRepository.findById(trip.getId());
        if (tripEntity == null) {
            throw new RuntimeException("DTO of non-existent entity at runtime");
        }

        if (!tripEntity.getState().equals(TripState.CREATED)) {
            throw new IllegalStateException("This trip's route can no longer be modified");
        }

        tripEntity.addStop(locationEntity);
        tripRepository.save(tripEntity);

        var newTripStops = trip.getStops();
        newTripStops.add(locationId);
        trip.setStops(newTripStops);

        var fare = safelyCalculateFare(trip);
        trip.setFare(fare);

        return true;
    }

    @Override
    public boolean removeStop(TripDTO trip, Long locationId) throws EntityNotFoundException, IllegalStateException {

        /*
        If we check for contains first we will never throw this exception, assuming proper
        stop adding techniques, so even though it would save us a repository call, this
        way we have a more in-depth view of the error cause
        */
        var locationEntity = locationRepository.findById(locationId);
        if (locationEntity == null) {
            throw new EntityNotFoundException("Cannot remove a stop that doesn't exist");
        }

        if (!trip.getStops().contains(locationId)) {
            return false;
        }

        var tripEntity = tripRepository.findById(trip.getId());
        if (tripEntity == null) {
            throw new RuntimeException("DTO of non-existent entity at runtime");
        }

        if (!tripEntity.getState().equals(TripState.CREATED)) {
            throw new IllegalStateException("This trip's route can no longer be modified");
        }

        var newEntityStops = tripEntity.getStops();
        newEntityStops.remove(locationEntity);

        tripEntity.setStops(newEntityStops);
        tripRepository.save(tripEntity);

        var newDtoStops = trip.getStops();
        newDtoStops.remove(locationId);
        trip.setStops(newDtoStops);

        var fare = safelyCalculateFare(trip);
        trip.setFare(fare);

        return true;
    }

    @Override
    public void delete(Long tripId) throws EntityNotFoundException {
        var deleted = tripRepository.delete(tripId);

        if (!deleted) {
            throw new EntityNotFoundException("Trying to delete a non-existent trip");
        }
    }

    @Override
    public TripDTO find(Long tripId) {
        throw new RuntimeException();
    }

    private TripDTO mapTripToDto(ITrip trip) {
        var dto = new TripDTO();
        dto.setDestinationId(trip.getDestination().getId());
        dto.setPickupId(trip.getPickup().getId());
        dto.setRiderId(trip.getRider().getId());
        dto.setId(trip.getId());

        if (trip.getStops() != null) {
            dto.setStops(trip.getStops().stream().map(ILocation::getId).collect(Collectors.toList()));
        }

        return dto;
    }

    private MoneyDTO safelyCalculateFare(TripDTO trip) {
        MoneyDTO fare;
        try {
            fare = matchingService.calculateFare(trip);
        } catch (InvalidTripException e) {
            fare = null;
        }
        return fare;
    }
}
