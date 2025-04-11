package com.lephuduy.jobhunter.service;

import com.lephuduy.jobhunter.domain.Company;
import com.lephuduy.jobhunter.domain.Role;
import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.response.ResultPaginationDTO;
import com.lephuduy.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.lephuduy.jobhunter.domain.dto.response.user.ResUpdateUserDTO;
import com.lephuduy.jobhunter.domain.dto.response.user.ResUserDTO;
import com.lephuduy.jobhunter.repository.UserRepository;
import com.lephuduy.jobhunter.service.mapper.MapperUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final MapperUser mapperUser;

    private final CompanyService companyService;

    private final RoleService roleService;

    public UserService(UserRepository userRepository, MapperUser mapperUser, CompanyService companyService, RoleService roleService){
        this.userRepository = userRepository;
        this.mapperUser = mapperUser;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public ResCreateUserDTO handleCreateUser(User user) {
        if (user.getCompany() != null) {
            Optional<Company> company = this.companyService.fetchById(user.getCompany().getId());
            user.setCompany(company.isPresent() ? company.get() : null);
        }

        if (user.getRole() != null) {
            Optional<Role> role = this.roleService.findRoleById(user.getRole().getId());
            user.setRole(role.isPresent() ? role.get() : null);
        }
        this.userRepository.save(user);
        return this.mapperUser.convertToResCreateUserDTO(user);
    }

    public boolean checkExistEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public User getUserByUserName(String email) {
        return this.userRepository.findByEmail(email);
    }


    public ResUpdateUserDTO updateUser(User currentUser, User userUpdate) {
//        Optional<User> currentUserOptional = this.userRepository.findById(user.getId());
//        User currentUser = currentUserOptional.get();
        currentUser.setName(userUpdate.getName());
        currentUser.setAge(userUpdate.getAge());
        currentUser.setGender(userUpdate.getGender());
        currentUser.setAddress(userUpdate.getAddress());
        if (userUpdate.getCompany() != null) {
            Optional<Company> companyOptional = this.companyService.fetchById(userUpdate.getCompany().getId());
            currentUser.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }

        if (userUpdate.getRole() != null) {
            Optional<Role> role = this.roleService.findRoleById(userUpdate.getRole().getId());
            currentUser.setRole(role.isPresent() ? role.get() : null);
        }
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

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(userPage.getTotalPages());
        meta.setTotal(userPage.getTotalElements());

        res.setMeta(meta);

        return res;
    }

    public void handleUpdateRefreshTokenOfUser(String email, String refresh_Token) {
        User user = this.userRepository.findByEmail(email);

        if(user != null){
            user.setRefreshToken(refresh_Token);
            this.userRepository.save(user);

        }

    }

    public User getUserByEmailAndToken(String email, String refreshToken) {
        return this.userRepository.findByEmailAndRefreshToken(email, refreshToken);
    }

    public Optional<User> checkExistUserById(long id) {
        return this.userRepository.findById(id);
    }
}
