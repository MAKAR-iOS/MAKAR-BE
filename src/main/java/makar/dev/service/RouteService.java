package makar.dev.service;

import lombok.RequiredArgsConstructor;
import makar.dev.repository.RouteRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;

}
