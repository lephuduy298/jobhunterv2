package com.lephuduy.jobhunter.service;

import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.lephuduy.jobhunter.repository.UserRepository;
import com.lephuduy.jobhunter.service.mapper.UserMapper;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public ResCreateUserDTO handleCreateUser(User user) {
        this.userRepository.save(user);
        return this.userMapper.convertToResCreateUserDTO(user);
    }

    public boolean checkExistEmail(@NotBlank(message = "email không được để trống") String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User getUserByUserName(String email) {
        return this.userRepository.findByEmail(email);
    }
}
