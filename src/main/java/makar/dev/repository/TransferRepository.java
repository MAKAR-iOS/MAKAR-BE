package makar.dev.repository;

import makar.dev.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    boolean existsByFromLineNumAndToLineNumAndOdsayStationName(int toLineNum, int fromLineNum, String odsayStationName);
}
