package com.lephuduy.jobhunter.controller;

import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.lephuduy.jobhunter.domain.dto.response.user.ResUpdateUserDTO;
import com.lephuduy.jobhunter.service.UserService;
import com.lephuduy.jobhunter.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/users")
    public ResponseEntity<ResUpdateUserDTO> updateUser (@RequestBody User user) throws IdInvalidException {
        boolean isExistUser = this.userService.checkExistUserById(user.getId());
        if(!isExistUser){
            throw new IdInvalidException("User với id=" + user.getId() + " không tồn tại");
        }

        ResUpdateUserDTO res = this.userService.updateUser(user);

        return ResponseEntity.ok().body(res);
    }

}
