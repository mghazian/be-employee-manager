package com.ghazian.employee_manager.core.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    Long id;
    Long no;
    String name;
    Long tierCode;
    String departmentCode;
    String locationCode;
    Long supervisorNo;
    Long salary;
    ZonedDateTime entryDate;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;
}
