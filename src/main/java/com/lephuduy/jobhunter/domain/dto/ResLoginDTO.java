package com.lephuduy.jobhunter.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
    private UserInsideToken user;
    private String access_token;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserInsideToken {
        private long id;
        private String name;
        private String email;
    }
}
