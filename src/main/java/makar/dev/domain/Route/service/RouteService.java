package makar.dev.domain.Route.service;

import lombok.RequiredArgsConstructor;
import makar.dev.domain.Route.repository.RouteRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;

}
