package com.lephuduy.jobhunter.service.mapper;

import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import com.lephuduy.jobhunter.domain.dto.response.user.ResUpdateUserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;

//@Mapper(componentModel = "spring")
@Service
public class MapperUser {

    public ResCreateUserDTO convertToResCreateUserDTO (User user){
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setAge(user.getAge());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());

        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO (User user){
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setAge(user.getAge());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setUpdatedAt(user.getUpdatedAt());

        return res;
    }
//    ResCreateUserDTO convertToResCreateUserDTO(User user);
//
//    void updateUser(@MappingTarget User user, User updateInfUser);
//
//    ResUpdateUserDTO convertToResUpdateUserDTO(User user);

}
