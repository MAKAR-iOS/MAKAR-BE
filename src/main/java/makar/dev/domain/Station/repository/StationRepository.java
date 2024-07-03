package makar.dev.domain.Station.repository;


import makar.dev.domain.Station.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
}
