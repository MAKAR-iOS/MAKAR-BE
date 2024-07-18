package makar.dev.repository;

import makar.dev.domain.LineMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineMapRepository extends JpaRepository<LineMap, Long> {
    LineMap findByLineNumAndStartStationName(int lineNum, String startStationName);
}
