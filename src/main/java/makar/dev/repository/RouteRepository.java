package makar.dev.repository;


import makar.dev.domain.Route;
import makar.dev.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findBySourceStationAndDestinationStation(Station sourceStation, Station destinationStation);
}
