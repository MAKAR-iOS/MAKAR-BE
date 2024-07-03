package makar.dev.domain.User.controller;

import lombok.RequiredArgsConstructor;
import makar.dev.domain.User.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

}
