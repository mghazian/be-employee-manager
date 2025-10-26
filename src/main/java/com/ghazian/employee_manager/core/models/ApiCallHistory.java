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
@Table(name = "api_call_history")
public class ApiCallHistory {
    @Id
    @SequenceGenerator(name = "api_call_history_id_seq", sequenceName = "api_call_history_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "api_call_history_id_seq")
    Long id;

    @Column
    String endpoint;

    @Column
    String httpMethod;

    @Column
    Integer responseStatus;

    @CreationTimestamp
    ZonedDateTime createdAt;
}
