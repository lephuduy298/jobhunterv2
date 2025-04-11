package com.lephuduy.jobhunter.controller;

import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.ResLoginDTO;
import com.lephuduy.jobhunter.domain.dto.request.ReqLoginDTO;
import com.lephuduy.jobhunter.service.UserService;
import com.lephuduy.jobhunter.util.SecurityUtil;
import com.lephuduy.jobhunter.util.anotaton.ApiMessage;
import com.lephuduy.jobhunter.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private  final SecurityUtil securityUtil;

    private final UserService userService;

    @Value("${lephuduy.jwt.refresh-token-validity-in-seconds}")
    private long jwtRefreshExperation;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService){
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO dto){
//        Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
//xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //lưu người dùng vào phần context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //lấy user hiện tại
        User currentUser = this.userService.getUserByUserName(dto.getUsername());

        //loginDTO tạo res trả ra cho người dùng
        ResLoginDTO loginDTO = new ResLoginDTO();
        if(currentUser != null){
            ResLoginDTO.UserLogin userLoginDTO = new ResLoginDTO.UserLogin(
                    currentUser.getId(),
                    currentUser.getName(),
                    currentUser.getEmail()
            );
            loginDTO.setUser(userLoginDTO);
        }

        //tạo accessToken
        String access_Token = this.securityUtil.createAccessToken(authentication.getName(), loginDTO);
        loginDTO.setAccessToken(access_Token);

        // tạo refreshToken
        String refresh_Token = this.securityUtil.createRefreshToken(authentication.getName(), loginDTO);

        //update user với refresh token
        this.userService.handleUpdateRefreshTokenOfUser(dto.getUsername(), refresh_Token);

        ResponseCookie springCookie = ResponseCookie.from("refresh_token", refresh_Token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtRefreshExperation)
//                    .domain("example.com")
                .build();

        return  ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(loginDTO);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Get information of account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount(){

        User currentUser = this.userService.getUserByUserName(SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "");

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getEmail()
        );

        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        userGetAccount.setUser(userLogin);

        return ResponseEntity.ok().body(userGetAccount);


    }

    @GetMapping("/auth/refresh")
    @ApiMessage("refresh token")
    public ResponseEntity<ResLoginDTO> refreshToken(@CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token) throws IdInvalidException {

        if(refresh_token.equals("abc")){
            throw new IdInvalidException("Bạn không có refresh token ở cookies");
        }

        Jwt decoded = this.securityUtil.checkValidToken(refresh_token);
        String email = decoded.getSubject();

        //check exist email and refresh token
        User user = this.userService.getUserByEmailAndToken(email, refresh_token);

        if(user == null){
            throw new IdInvalidException("Refresh token không hợp lệ");
        }

        ResLoginDTO loginDTO = new ResLoginDTO();
        if(user != null){
            ResLoginDTO.UserLogin userLoginDTO = new ResLoginDTO.UserLogin(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );
            loginDTO.setUser(userLoginDTO);
        }

        //tạo accessToken
        String access_Token = this.securityUtil.createAccessToken(email, loginDTO);
        loginDTO.setAccessToken(access_Token);

        // tạo refreshToken
        String new_refresh_Token = this.securityUtil.createRefreshToken(email, loginDTO);

        //update user với refresh token
        this.userService.handleUpdateRefreshTokenOfUser(email, new_refresh_Token);

        ResponseCookie springCookie = ResponseCookie.from("refresh_token", new_refresh_Token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtRefreshExperation)
//                    .domain("example.com")
                .build();

        return  ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(loginDTO);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("logout successfully")
    public ResponseEntity<Void> logoutUser(){
        String email = SecurityUtil.getCurrentUserLogin().get();

        User user = this.userService.getUserByUserName(email);

        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
//                    .domain("example.com")
                .build();

        this.userService.handleUpdateRefreshTokenOfUser(email, null);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

}
