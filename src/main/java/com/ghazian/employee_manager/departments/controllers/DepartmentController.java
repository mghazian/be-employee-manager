package com.ghazian.employee_manager.departments.controllers;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.departments.dto.DepartmentDTO;
import com.ghazian.employee_manager.departments.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<Pagination<DepartmentDTO>> getDto(@RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(departmentService.getPaginated(page, size));
    }
}
