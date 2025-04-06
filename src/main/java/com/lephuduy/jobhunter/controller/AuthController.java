package com.lephuduy.jobhunter.controller;

import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.ResLoginDTO;
import com.lephuduy.jobhunter.domain.dto.request.ReqLoginDTO;
import com.lephuduy.jobhunter.service.UserService;
import com.lephuduy.jobhunter.util.SecurityUtil;
import com.lephuduy.jobhunter.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private  final SecurityUtil securityUtil;

    private final UserService userService;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService){
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO dto){
//        Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
//xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //lấy user hiện tại
        ResLoginDTO loginDTO = new ResLoginDTO();
        User currentUser = this.userService.getUserByUserName(dto.getEmail());
        if(currentUser != null){
            ResLoginDTO.UserInsideToken userInsideToken = new ResLoginDTO.UserInsideToken(
                    currentUser.getId(),
                    currentUser.getName(),
                    currentUser.getEmail()
            );
            loginDTO.setUser(userInsideToken);
        }
        String access_Token = this.securityUtil.createToken(authentication.getName(), loginDTO);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        loginDTO.setAccess_token(access_Token);
        return ResponseEntity.ok().body(loginDTO);
    }
}
