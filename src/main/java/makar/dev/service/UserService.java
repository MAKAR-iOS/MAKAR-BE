package makar.dev.service;

import lombok.RequiredArgsConstructor;
import makar.dev.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
