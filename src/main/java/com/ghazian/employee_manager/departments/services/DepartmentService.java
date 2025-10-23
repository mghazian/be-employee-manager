package com.ghazian.employee_manager.departments.services;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.departments.dto.DepartmentDTO;

public interface DepartmentService {
    Pagination<DepartmentDTO> getPaginated(int pageIndex, int size);
}
