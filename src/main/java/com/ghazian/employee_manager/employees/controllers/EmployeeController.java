package com.ghazian.employee_manager.employees.controllers;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.core.dto.RestResponse;
import com.ghazian.employee_manager.employees.dto.EmployeeApiResponseDTO;
import com.ghazian.employee_manager.employees.dto.EmployeeOption;
import com.ghazian.employee_manager.employees.dto.WriteEmployeeParam;
import com.ghazian.employee_manager.employees.repositories.projections.CumulativeSalaryPerDepartmentDTO;
import com.ghazian.employee_manager.employees.repositories.projections.DepartmentAnalysisByLocationDTO;
import com.ghazian.employee_manager.employees.services.EmployeeAnalyticService;
import com.ghazian.employee_manager.employees.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeAnalyticService employeeAnalyticService;

    @GetMapping
    public ResponseEntity<Pagination<EmployeeApiResponseDTO>> getDto(@RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(employeeService.getPaginated(page, size));
    }

    @PostMapping("/import")
    public ResponseEntity<RestResponse> importCsv(@RequestParam("csv") MultipartFile file) {
        employeeService.importFile(file);
        return ResponseEntity.ok(new RestResponse("File imported successfully"));
    }

    @PostMapping
    public ResponseEntity<EmployeeApiResponseDTO> create(@RequestBody WriteEmployeeParam param) {
        return ResponseEntity.ok(employeeService.create(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeApiResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(employeeService.getOne(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeApiResponseDTO> update(@PathVariable("id") Long id,
                                                          @RequestBody WriteEmployeeParam input) {
        return ResponseEntity.ok(employeeService.update(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse> deleteById(@PathVariable("id") Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok(new RestResponse("Location deleted successfully"));
    }

    @GetMapping("/option")
    public ResponseEntity<List<EmployeeOption>> getAllOptions() {
        return ResponseEntity.ok(employeeService.getAllOptions());
    }

    @GetMapping("/analytics/department-by-location")
    public ResponseEntity<List<DepartmentAnalysisByLocationDTO>> getDepartmentByLocationAnalysis() {
        return ResponseEntity.ok(employeeAnalyticService.getDepartmentAnalysisByLocation());
    }

    @GetMapping("/analytics/cumulative-salary-per-department")
    public ResponseEntity<List<CumulativeSalaryPerDepartmentDTO>> getCumulativeSalaryPerDepartment() {
        return ResponseEntity.ok(employeeAnalyticService.getCumulativeSalaryPerDepartment());
    }

    @GetMapping("/analytics/salary-ranking")
    public ResponseEntity<List<CumulativeSalaryPerDepartmentDTO>> getSalaryRankingAndGapAnalysis() {
        return ResponseEntity.ok(employeeAnalyticService.getSalaryRankingAndGapAnalysis());
    }
}
