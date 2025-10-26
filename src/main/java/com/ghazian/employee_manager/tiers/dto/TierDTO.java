package com.ghazian.employee_manager.tiers.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TierDTO {
    private Long id;
    private String name;
    private Long code;
}
