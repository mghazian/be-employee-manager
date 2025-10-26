package com.ghazian.employee_manager.tiers.controllers;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.core.dto.RestResponse;
import com.ghazian.employee_manager.tiers.dto.TierDTO;
import com.ghazian.employee_manager.tiers.dto.WriteTierParam;
import com.ghazian.employee_manager.tiers.services.TierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tiers")
public class TierController {

    private final TierService tierService;

    @GetMapping
    public ResponseEntity<Pagination<TierDTO>> getDto(@RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(tierService.getPaginated(page, size));
    }

    @PostMapping("/import")
    public ResponseEntity<RestResponse> importCsv(@RequestParam("csv") MultipartFile file) {
        tierService.importFile(file);
        return ResponseEntity.ok(new RestResponse("File imported successfully"));
    }

    @PostMapping
    public ResponseEntity<TierDTO> create(@RequestBody WriteTierParam param) {
        return ResponseEntity.ok(tierService.create(param));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TierDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(tierService.getOne(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TierDTO> update(@PathVariable("id") Long id,
                                          @RequestBody WriteTierParam input) {
        return ResponseEntity.ok(tierService.update(id, input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse> deleteById(@PathVariable("id") Long id) {
        tierService.delete(id);
        return ResponseEntity.ok(new RestResponse("Tier deleted successfully"));
    }
}
