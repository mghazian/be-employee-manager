package com.ghazian.employee_manager.departments.services;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.departments.dto.CreateDepartmentParam;
import com.ghazian.employee_manager.departments.dto.DepartmentDTO;
import org.springframework.web.multipart.MultipartFile;

public interface DepartmentService {
    Pagination<DepartmentDTO> getPaginated(int pageIndex, int size);
    void importFile(MultipartFile file);
    DepartmentDTO create(CreateDepartmentParam param);
}
