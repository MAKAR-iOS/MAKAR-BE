package makar.dev.repository;

import makar.dev.domain.LineMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LineMapRepository extends JpaRepository<LineMap, Long> {
    LineMap findByLineNumAndStartStationName(int lineNum, String startStationName);
    List<LineMap> findByLineNum(int lineNum);
}
