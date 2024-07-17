package makar.dev.repository;


import makar.dev.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findByStationNameAndLineNum(String stationName, String lineNum);

}
