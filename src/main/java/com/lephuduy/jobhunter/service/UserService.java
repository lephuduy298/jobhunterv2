package com.lephuduy.jobhunter.service;

import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.lephuduy.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.lephuduy.jobhunter.domain.dto.response.user.ResUpdateUserDTO;
import com.lephuduy.jobhunter.domain.dto.response.user.ResUserDTO;
import com.lephuduy.jobhunter.repository.UserRepository;
import com.lephuduy.jobhunter.service.mapper.MapperUser;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<User> getUserById(long id) {
        return this.userRepository.findById(id);
    }

    public ResUserDTO handleFetchAUser(User user) {
        return this.mapperUser.convertToResUserDTO(user);
    }

    public void handleDeleteAUser(User user) {
        this.userRepository.deleteById(user.getId());
    }

    public List<User> findAllUser() {
        return this.userRepository.findAll();
    }

    public ResultPaginationDTO findAllUserWithFilter(Specification<User> spec, Pageable pageable) {
        Page<User> userPage = this.userRepository.findAll(spec, pageable);
        List<User> userList = userPage.getContent();

        List<ResUserDTO> listUserDTO = userList.stream().map(user -> this.mapperUser.convertToResUserDTO(user)).collect(Collectors.toList());

        ResultPaginationDTO res = new ResultPaginationDTO();
        res.setResult(listUserDTO);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(userPage.getNumber() + 1);
        meta.setPageSize(userPage.getSize());
        meta.setPages(userPage.getTotalPages());
        meta.setTotal(userPage.getTotalElements());

        res.setMeta(meta);

        return res;
    }
}
