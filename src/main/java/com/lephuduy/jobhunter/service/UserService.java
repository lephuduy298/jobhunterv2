package com.lephuduy.jobhunter.service;

import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.lephuduy.jobhunter.domain.dto.response.user.ResUpdateUserDTO;
import com.lephuduy.jobhunter.repository.UserRepository;
import com.lephuduy.jobhunter.service.mapper.MapperUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final MapperUser mapperUser;

    public UserService(UserRepository userRepository, MapperUser mapperUser){
        this.userRepository = userRepository;
        this.mapperUser = mapperUser;
    }

    public ResCreateUserDTO handleCreateUser(User user) {
        this.userRepository.save(user);
        return this.mapperUser.convertToResCreateUserDTO(user);
    }

    public boolean checkExistEmail(@NotBlank(message = "email không được để trống") String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User getUserByUserName(String email) {
        return this.userRepository.findByEmail(email);
    }

    public boolean checkExistUserById(long id) {
        return this.userRepository.existsById(id);
    }

    public ResUpdateUserDTO updateUser(User user) {
        Optional<User> currentUserOptional = this.userRepository.findById(user.getId());
        User currentUser = currentUserOptional.get();
        currentUser.setName(user.getName());
        currentUser.setAge(user.getAge());
        currentUser.setGender(user.getGender());
        currentUser.setAddress(user.getAddress());
        this.userRepository.save(currentUser);

        return this.mapperUser.convertToResUpdateUserDTO(currentUser);
    }
}
