package com.lephuduy.jobhunter.controller;

import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.lephuduy.jobhunter.service.UserService;
import com.lephuduy.jobhunter.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean existedEmail = this.userService.checkExistEmail(user.getEmail());
        if(existedEmail){
            throw new IdInvalidException("Email " + user.getEmail() + " đã tồn tại. Vui lòng chọn email khác.");
        }
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        ResCreateUserDTO res = this.userService.handleCreateUser(user);



        return ResponseEntity.created(null).body(res);
    }

}
