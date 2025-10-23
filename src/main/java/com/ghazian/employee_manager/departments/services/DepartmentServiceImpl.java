package com.ghazian.employee_manager.departments.services;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.core.models.Department;
import com.ghazian.employee_manager.core.repositories.DepartmentRepository;
import com.ghazian.employee_manager.departments.dto.DepartmentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public Pagination<DepartmentDTO> getPaginated(int pageIndex, int size) {
        Page<Department> departments = departmentRepository.findAll(PageRequest.of(pageIndex, size));

        final List<DepartmentDTO> departmentDTOs = departments.stream().map(v -> {
            return DepartmentDTO.builder()
                    .id(v.getId())
                    .code(v.getCode())
                    .name(v.getName())
                    .build();
        }).toList();

        return Pagination.<DepartmentDTO>builder()
                .data(departmentDTOs)
                .totalPages(departments.getTotalPages())
                .build();
    }
}
