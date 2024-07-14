package makar.dev.controller;

import lombok.RequiredArgsConstructor;
import makar.dev.service.RouteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RouteController {
    private final RouteService routeService;

}
