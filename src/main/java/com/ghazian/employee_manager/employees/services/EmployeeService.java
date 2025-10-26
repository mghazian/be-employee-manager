package com.ghazian.employee_manager.employees.services;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.employees.dto.EmployeeApiResponseDTO;
import com.ghazian.employee_manager.employees.dto.EmployeeOption;
import com.ghazian.employee_manager.employees.dto.WriteEmployeeParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {
    Pagination<EmployeeApiResponseDTO> getPaginated(int pageIndex, int size);
    void importFile(MultipartFile file);
    EmployeeApiResponseDTO create(WriteEmployeeParam param);
    EmployeeApiResponseDTO getOne(long id);
    EmployeeApiResponseDTO update(long id, WriteEmployeeParam newData);
    void delete(long id);

    List<EmployeeOption> getAllOptions();
}
