package com.ghazian.employee_manager.departments.services;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.core.exceptions.ValidationException;
import com.ghazian.employee_manager.core.models.Department;
import com.ghazian.employee_manager.core.repositories.DepartmentRepository;
import com.ghazian.employee_manager.departments.dto.DepartmentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public void importFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Map<String, Object> errors = new HashMap<>();
            List<Department> inputs = new ArrayList<>();

            Department.DepartmentBuilder builder = Department.builder();

            String line = reader.readLine(); // Read header

            long lineNo = 2; // 1-based
            for ( line = reader.readLine(); line != null; line = reader.readLine() ) {
                String[] values = line.split(",", 0);

                if ( values.length < 2 ) {
                    errors.put(String.valueOf(lineNo), List.of(String.format("Invalid row. Expects two values, only received %d", values.length)));
                    continue;
                }

                List<String> lineErrors = new ArrayList<>();

                if ( values[0].isBlank() ) {
                    lineErrors.add("Department code cannot be empty");
                }

                if ( values[1].isBlank() ) {
                    lineErrors.add("Department name cannot be empty");
                }

                inputs.add(builder
                        .code(values[0])
                        .name(values[1])
                        .build());

                lineNo++;
            }

            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }

            departmentRepository.massInsert(inputs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
