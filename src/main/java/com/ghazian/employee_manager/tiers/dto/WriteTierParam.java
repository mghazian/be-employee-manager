package com.ghazian.employee_manager.tiers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WriteTierParam {
    String code;
    String name;
}
