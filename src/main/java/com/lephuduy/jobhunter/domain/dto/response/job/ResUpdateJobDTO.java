package com.lephuduy.jobhunter.domain.dto.response.job;

import com.lephuduy.jobhunter.util.constant.EnumLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResUpdateJobDTO {
    private long id;
    private String name;
    private String location;
    private Double salary;
    private int quantity;
    private EnumLevel level;

    private Instant startDate;
    private Instant endDate;

    private boolean active;

    private Instant updatedAt;
    private String updatedBy;

    private List<String> skills;
}