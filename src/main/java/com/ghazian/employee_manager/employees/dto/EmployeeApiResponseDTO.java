package com.ghazian.employee_manager.employees.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ghazian.employee_manager.departments.dto.DepartmentDTO;
import com.ghazian.employee_manager.locations.dto.LocationDTO;
import com.ghazian.employee_manager.tiers.dto.TierDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EmployeeApiResponseDTO {
    Long id;
    Long no;
    String name;
    TierDTO tier;
    DepartmentDTO department;
    LocationDTO location;
    SimpleSupervisorDTO supervisor;
    Long salary;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    ZonedDateTime entryDate;

    @Data
    @Builder
    static public class SimpleSupervisorDTO {
        Long id;
        Long no;
        String name;
    }
}
