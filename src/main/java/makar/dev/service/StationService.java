package makar.dev.service;

import lombok.RequiredArgsConstructor;
import makar.dev.repository.StationRepository;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;


}
