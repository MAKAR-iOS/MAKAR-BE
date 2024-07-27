package makar.dev.repository;


import makar.dev.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByStationNameAndLineNum(String stationName, String lineNum);
    Optional<Station> findByStationNameAndOdsayLaneType(String stationName, int code);

    List<Station> findByStationNameContaining(String stationName);

}
