package com.ghazian.employee_manager.core.repositories.projections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Data
@Builder
public class GetEmployeeQueryDTO {
    Long id;
    Long no;
    String name;
    Long tierCode;
    String departmentCode;
    String locationCode;
    Long supervisorNo;
    Long salary;
    Instant entryDate;
    Instant createdAt;
    Instant updatedAt;

    Long departmentId;
    String departmentName;

    Long tierId;
    String tierName;

    Long locationId;
    String locationName;

    Long supervisorId;
    String supervisorName;
}
