package com.lephuduy.jobhunter.service;

import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void handleCreateUser(User user) {
        this.userRepository.save(user);
    }
}
