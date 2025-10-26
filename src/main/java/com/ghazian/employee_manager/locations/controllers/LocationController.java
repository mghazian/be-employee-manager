package com.ghazian.employee_manager.locations.controllers;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.core.dto.RestResponse;
import com.ghazian.employee_manager.locations.dto.LocationDTO;
import com.ghazian.employee_manager.locations.dto.WriteLocationParam;
import com.ghazian.employee_manager.locations.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<Pagination<LocationDTO>> getDto(@RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(locationService.getPaginated(page, size));
    }

    @PostMapping("/import")
    public ResponseEntity<RestResponse> importCsv(@RequestParam("csv") MultipartFile file) {
        locationService.importFile(file);
        return ResponseEntity.ok(new RestResponse("File imported successfully"));
    }

    @PostMapping
    public ResponseEntity<LocationDTO> create(@RequestBody WriteLocationParam param) {
        return ResponseEntity.ok(locationService.create(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(locationService.getOne(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> update(@PathVariable("id") Long id,
                                              @RequestBody WriteLocationParam input) {
        return ResponseEntity.ok(locationService.update(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse> deleteById(@PathVariable("id") Long id) {
        locationService.delete(id);
        return ResponseEntity.ok(new RestResponse("Location deleted successfully"));
    }
}
