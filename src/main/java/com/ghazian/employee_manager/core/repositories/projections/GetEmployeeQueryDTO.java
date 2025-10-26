package com.ghazian.employee_manager.core.repositories.projections;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public interface GetEmployeeQueryDTO {
    Long getId();
    Long getNo();
    String getName();
    Long getTierCode();
    String getDepartmentCode();
    String getLocationCode();
    Long getSupervisorNo();
    Long getSalary();
    Instant getEntryDate();
    Instant getCreatedAt();
    Instant getUpdatedAt();

    Long getDepartmentId();
    String getDepartmentName();

    Long getTierId();
    String getTierName();

    Long getLocationId();
    String getLocationName();

    Long getSupervisorId();
    String getSupervisorName();
}
