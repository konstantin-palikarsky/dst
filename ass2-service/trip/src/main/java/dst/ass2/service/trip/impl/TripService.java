package dst.ass2.service.trip.impl;

import dst.ass1.jpa.dao.IDAOFactory;
import dst.ass1.jpa.dao.ILocationDAO;
import dst.ass1.jpa.dao.IRiderDAO;
import dst.ass1.jpa.dao.ITripDAO;
import dst.ass1.jpa.model.ILocation;
import dst.ass1.jpa.model.ITrip;
import dst.ass1.jpa.model.TripState;
import dst.ass1.jpa.model.impl.Trip;
import dst.ass2.service.api.match.IMatchingService;
import dst.ass2.service.api.trip.*;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.stream.Collectors;

@Singleton
@Named
@ManagedBean
public class TripService implements ITripService {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private IDAOFactory daoFactory;

    @Inject
    private IMatchingService matchingService;

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

        ITrip newTrip = new Trip();
        newTrip.setRider(rider);
        newTrip.setPickup(pickup);
        newTrip.setDestination(destination);
        newTrip.setState(TripState.CREATED);

        ITrip persistedTrip = tripRepository.save(newTrip);
        var dto = mapTripToDto(persistedTrip);
        MoneyDTO fare;

        try {
            fare = matchingService.calculateFare(dto);
        } catch (InvalidTripException e) {
            fare = null;
        }
        dto.setFare(fare);

        return dto;
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
