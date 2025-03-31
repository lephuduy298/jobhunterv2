package com.lephuduy.jobhunter.controller;

import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.lephuduy.jobhunter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) {
        this.userService.handleCreateUser(user);
        return ;
    }

}
