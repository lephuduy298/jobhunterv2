package com.lephuduy.jobhunter.controller;

import com.lephuduy.jobhunter.domain.dto.request.LoginDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder){
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginDTO> login(@Valid @RequestBody LoginDTO dto){
//        Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
//xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return ResponseEntity.ok().body(dto);
    }
}
