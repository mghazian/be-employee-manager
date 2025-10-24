package com.ghazian.employee_manager.departments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WriteDepartmentParam {
    String code;
    String name;
}
