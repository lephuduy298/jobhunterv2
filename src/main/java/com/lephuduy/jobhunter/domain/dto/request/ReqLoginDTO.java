package com.lephuduy.jobhunter.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {
    @NotBlank(message = "email không đươc để trống")
    private String email;

    @NotBlank(message = "password không được để trống")
    private String password;
}
