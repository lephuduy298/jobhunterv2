package com.lephuduy.jobhunter.domain.dto.response.resume;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResCreateResumeDTO {
    private long id;
    private Instant createAt;
    private String createBy;
}
