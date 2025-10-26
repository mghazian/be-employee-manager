package com.ghazian.employee_manager.locations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WriteLocationParam {
    String code;
    String name;
    String address;
}
