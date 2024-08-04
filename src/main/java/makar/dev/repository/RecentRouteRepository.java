package makar.dev.repository;

import makar.dev.domain.RecentRoute;
import makar.dev.domain.Route;
import makar.dev.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecentRouteRepository extends JpaRepository<RecentRoute, Long> {
    Optional<RecentRoute> findByUserAndRoute(User user, Route route);
    void deleteByUser(User user);
}
