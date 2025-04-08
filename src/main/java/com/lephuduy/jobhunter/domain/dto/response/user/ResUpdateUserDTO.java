package com.lephuduy.jobhunter.domain.dto.response.user;

import com.lephuduy.jobhunter.util.constant.EnumGender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private EnumGender gender;
    private String address;
    private Instant updatedAt;
    private ResCreateUserDTO.CompanyUser company;
    private ResCreateUserDTO.RoleUser role;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyUser {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleUser {
        private long id;
        private String name;
    }
}
