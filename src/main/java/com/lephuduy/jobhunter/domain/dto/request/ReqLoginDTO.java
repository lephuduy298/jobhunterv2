package com.lephuduy.jobhunter.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {
    @NotBlank(message = "email không đươc để trống 123")
    private String username;

    @NotBlank(message = "password không được để trống")
    private String password;
}
