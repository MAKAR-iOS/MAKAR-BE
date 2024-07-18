package makar.dev.controller;

import lombok.RequiredArgsConstructor;
import makar.dev.service.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StationController {
    private final StationService stationService;


}
