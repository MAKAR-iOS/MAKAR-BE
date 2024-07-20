package makar.dev.controller;

import lombok.RequiredArgsConstructor;
import makar.dev.common.response.ApiResponse;
import makar.dev.common.status.SuccessStatus;
import makar.dev.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    @GetMapping("/init")
    ApiResponse initStation(){
        userService.initDatabase();
        return ApiResponse.SuccessResponse(SuccessStatus._OK);
    }

    @PostMapping("/tmp/create")
    ApiResponse tempCreateUser(@RequestParam(value = "name")String name){
        userService.tmpCreateUser(name);
        return ApiResponse.SuccessResponse(SuccessStatus._OK);
    }


}
