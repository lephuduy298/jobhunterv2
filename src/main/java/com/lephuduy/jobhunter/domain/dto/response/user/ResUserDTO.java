package com.lephuduy.jobhunter.domain.dto.response.user;

import com.lephuduy.jobhunter.util.constant.EnumGender;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private EnumGender gender;
    private String address;
    private Instant createdAt;
    private Instant updatedAt;
}
