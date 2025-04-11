package com.lephuduy.jobhunter.controller;

import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.lephuduy.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.lephuduy.jobhunter.domain.dto.response.user.ResUpdateUserDTO;
import com.lephuduy.jobhunter.domain.dto.response.user.ResUserDTO;
import com.lephuduy.jobhunter.service.UserService;
import com.lephuduy.jobhunter.util.anotaton.ApiMessage;
import com.lephuduy.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @ApiMessage("Create a user")
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
    @ApiMessage("update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser (@RequestBody User user) throws IdInvalidException {
        Optional<User> currentUser = this.userService.checkExistUserById(user.getId());
        if(currentUser.isEmpty()){
            throw new IdInvalidException("User với id=" + user.getId() + " không tồn tại");
        }

        ResUpdateUserDTO res = this.userService.updateUser(currentUser.get(), user);

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch a user")
    public ResponseEntity<ResUserDTO> fetchAUser (@PathVariable("id") long id) throws IdInvalidException {
        Optional<User> userOptional = this.userService.getUserById(id);
        if(userOptional.isEmpty()){
            throw new IdInvalidException("User với id=" + id + " không tồn tại");
        }
        User user = userOptional.get();
        ResUserDTO res = this.userService.handleFetchAUser(user);
        return ResponseEntity.ok().body(res);

    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteAUser (@PathVariable("id") long id) throws IdInvalidException {
        Optional<User> userOptional = this.userService.getUserById(id);
        if(userOptional.isEmpty()){
            throw new IdInvalidException("User với id=" + id + " không tồn tại");
        }
        User user = userOptional.get();
    this.userService.handleDeleteAUser(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    @ApiMessage("fetch all user with filter")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser (
        @Filter Specification<User> spec,
        Pageable pageable
    ) {


        return ResponseEntity.ok().body(this.userService.findAllUserWithFilter(spec, pageable));
    }

}
