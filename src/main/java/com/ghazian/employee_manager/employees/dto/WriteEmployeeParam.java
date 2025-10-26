package com.ghazian.employee_manager.employees.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WriteEmployeeParam {
    String id;
    String no;
    String name;
    String tierCode;
    String departmentCode;
    String locationCode;
    String supervisorNo;
    String salary;
    String entryDate;
}
