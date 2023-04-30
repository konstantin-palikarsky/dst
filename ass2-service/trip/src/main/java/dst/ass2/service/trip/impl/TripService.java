package dst.ass2.service.trip.impl;

import dst.ass1.jpa.dao.*;
import dst.ass1.jpa.model.*;
import dst.ass2.service.api.match.IMatchingService;
import dst.ass2.service.api.trip.*;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.Date;
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
    private IVehicleDAO vehicleRepository;
    private IDriverDAO driverRepository;
    private IMatchDAO matchRepository;

    @PostConstruct
    public void startup() {
        tripRepository = daoFactory.createTripDAO();
        riderRepository = daoFactory.createRiderDAO();
        locationRepository = daoFactory.createLocationDAO();
        vehicleRepository = daoFactory.createVehicleDAO();
        driverRepository = daoFactory.createDriverDAO();
        matchRepository = daoFactory.createMatchDAO();
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
        var tripEntity = tripRepository.findById(tripId);
        if (tripEntity == null) {
            throw new EntityNotFoundException("Attempting to confirm non-existent trip");
        }

        if (!tripEntity.getState().equals(TripState.CREATED) ||
                tripEntity.getRider() == null) {
            throw new IllegalStateException("Trip not in correct internal state to confirm");
        }
        matchingService.calculateFare(mapTripToDto(tripEntity));

        tripEntity.setState(TripState.QUEUED);
        tripRepository.save(tripEntity);
        matchingService.queueTripForMatching(tripId);
    }

    @Override
    public void match(Long tripId, MatchDTO match) throws EntityNotFoundException, DriverNotAvailableException, IllegalStateException {
        /**
         * Creates a match for the given trip and sets the trip's state to MATCHED, if possible (i.e., if the trips is
         * QUEUED). In case something goes wrong, re-queues the trip for a new match.
         * You can assume that the locations (pickup, destination and stops) haven't been deleted and will not be deleted
         * during the execution of this method.
         *
         * @param tripId the id of the trip the match will be created for
         * @param match  the match, containing the driver, the vehicle and the fare
         * @throws EntityNotFoundException     in case one of the following doesn't exist anymore: trip, driver or
         *                                     vehicle
         * @throws DriverNotAvailableException in case the driver was assigned in the meantime to another customer
         * @throws IllegalStateException       in case the rider of the trip is null or the trip is not in QUEUED state
         */
        var tripEntity = tripRepository.findById(tripId);
        var driverEntity = driverRepository.findById(match.getDriverId());
        var vehicleEntity = vehicleRepository.findById(match.getVehicleId());
        if (tripEntity == null || driverEntity == null || vehicleEntity == null) {
            matchingService.queueTripForMatching(tripId);
            throw new EntityNotFoundException("One of the required match entities no-longer exists, re-queuing");
        }

        if (tripEntity.getRider() == null || !tripEntity.getState().equals(TripState.QUEUED)) {
            matchingService.queueTripForMatching(tripId);
            throw new IllegalStateException("Cannot match a trip in this state, re-queuing");
        }

        if (isDriverCurrentlyAssigned(driverEntity.getId())) {
            matchingService.queueTripForMatching(tripId);
            throw new DriverNotAvailableException("The designated driver is currently busy, re-queuing");
        }

        tripEntity.setState(TripState.MATCHED);
        var matchEntity = mapDtoToMatch(match, driverEntity, vehicleEntity, tripEntity);

        tripRepository.save(tripEntity);
        matchRepository.save(matchEntity);
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
        var tripEntity = tripRepository.findById(tripId);
        if (tripEntity == null) {
            return null;
        }

        var tripDto = mapTripToDto(tripEntity);
        var fare = safelyCalculateFare(tripDto);
        tripDto.setFare(fare);

        return tripDto;
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

    private boolean isDriverCurrentlyAssigned(Long driverId) {
        var lastTrip = tripRepository.getLastTripOfDriver(driverId);

        //Early termination protects from NPE
        return !(lastTrip == null) &&
                !lastTrip.getState().equals(TripState.COMPLETED) &&
                !lastTrip.getState().equals(TripState.CANCELLED);
    }

    private IMatch mapDtoToMatch(MatchDTO dto, IDriver driver, IVehicle vehicle, ITrip trip) {
        IMatch matchEntity = modelFactory.createMatch();
        matchEntity.setDriver(driver);
        matchEntity.setVehicle(vehicle);
        matchEntity.setTrip(trip);

        var fareDto = dto.getFare();
        if (fareDto != null) {
            IMoney fare = modelFactory.createMoney();
            fare.setCurrency(fareDto.getCurrency());
            fare.setCurrencyValue(fareDto.getValue());

            matchEntity.setFare(fare);
        }

        matchEntity.setDate(new Date());

        return matchEntity;
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
