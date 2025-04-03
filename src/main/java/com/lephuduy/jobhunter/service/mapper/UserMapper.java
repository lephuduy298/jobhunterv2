package com.lephuduy.jobhunter.service.mapper;

import com.lephuduy.jobhunter.domain.User;
import com.lephuduy.jobhunter.domain.dto.response.user.ResCreateUserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ResCreateUserDTO convertToResCreateUserDTO(User user);
}
