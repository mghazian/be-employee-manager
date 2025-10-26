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
@Table(name = "locations")
public class Location {
    @Id
    @SequenceGenerator(name = "locations_id_seq", sequenceName = "locations_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locations_id_seq")
    Long id;

    String code;

    String name;

    String address;

    @CreationTimestamp
    ZonedDateTime createdAt;

    @UpdateTimestamp
    ZonedDateTime updatedAt;
}
