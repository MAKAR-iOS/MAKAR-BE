package makar.dev.domain.User.service;

import lombok.RequiredArgsConstructor;
import makar.dev.domain.User.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
}
