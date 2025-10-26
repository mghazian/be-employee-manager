package com.ghazian.employee_manager.locations.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDTO {
    private Long id;
    private String name;
    private String code;
    private String address;
}
