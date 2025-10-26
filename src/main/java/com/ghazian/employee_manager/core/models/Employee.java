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
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employees")
public class Employee {
    @Id
    @SequenceGenerator(name = "employees_id_seq", sequenceName = "employees_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employees_id_seq")
    Long id;

    @Column
    Long no;

    @Column
    String name;

    @Column
    Long tierCode;

    @Column
    String departmentCode;

    @Column
    String locationCode;

    @Column
    Long supervisorNo;

    @Column
    Long salary;

    @Column
    ZonedDateTime entryDate;

    @CreationTimestamp
    @Column
    ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column
    ZonedDateTime updatedAt;
}
