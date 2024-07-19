package makar.dev.repository;

import makar.dev.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    boolean existsByFromLineNumAndToLineNumAndOdsayStationName(int toLineNum, int fromLineNum, String odsayStationName);
    List<Transfer> findByFromStationIdAndToStationId(int fromStationId, int foStationId);
}
