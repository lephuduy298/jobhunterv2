package com.lephuduy.jobhunter.domain.dto.response.job;

import java.time.Instant;
import java.util.List;

import com.lephuduy.jobhunter.util.constant.EnumLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateJobDTO {
    private long id;
    private String name;
    private String location;
    private Double salary;
    private int quantity;
    private EnumLevel level;

    private Instant startDate;
    private Instant endDate;

    private boolean active;

    private Instant createdAt;
    private String createdBy;

    private List<String> skills;

}
