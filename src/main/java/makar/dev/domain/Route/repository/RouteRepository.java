package makar.dev.domain.Route.repository;


import makar.dev.domain.Route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
}
