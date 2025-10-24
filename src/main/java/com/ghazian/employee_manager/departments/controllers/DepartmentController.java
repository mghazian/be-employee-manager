package com.ghazian.employee_manager.departments.controllers;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.core.dto.RestResponse;
import com.ghazian.employee_manager.departments.dto.DepartmentDTO;
import com.ghazian.employee_manager.departments.services.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @PostMapping("/import")
    public ResponseEntity<RestResponse> importDepartment(@RequestParam("csv") MultipartFile file) {
        departmentService.importFile(file);
        return ResponseEntity.ok(new RestResponse("File imported successfully"));
    }
}
