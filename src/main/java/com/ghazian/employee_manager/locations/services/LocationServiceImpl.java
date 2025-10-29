package com.ghazian.employee_manager.locations.services;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.core.exceptions.ResourceNotFoundException;
import com.ghazian.employee_manager.core.exceptions.ValidationException;
import com.ghazian.employee_manager.core.models.Location;
import com.ghazian.employee_manager.core.repositories.LocationRepository;
import com.ghazian.employee_manager.locations.dto.LocationDTO;
import com.ghazian.employee_manager.locations.dto.WriteLocationParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public Pagination<LocationDTO> getPaginated(int pageIndex, int size) {
        Page<Location> entries = locationRepository.findAll(PageRequest.of(pageIndex, size));

        final LocationDTO.LocationDTOBuilder builder = LocationDTO.builder();

        final List<LocationDTO> LocationDTOs = entries.stream().map(v ->
                builder
                    .id(v.getId())
                    .code(v.getCode())
                    .name(v.getName())
                    .address(v.getAddress())
                    .build()
        ).toList();

        return Pagination.<LocationDTO>builder()
                .data(LocationDTOs)
                .totalPages(entries.getTotalPages())
                .build();
    }

    @Override
    public void importFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Map<String, List<String>> errors = new HashMap<>();
            List<Location> inputs = new ArrayList<>();

            Location.LocationBuilder builder = Location.builder();

            String line = reader.readLine(); // Read header

            long lineNo = 2; // 1-based
            for ( line = reader.readLine(); line != null; line = reader.readLine() ) {
                String[] values = line.split(",", -1);

                if ( values.length < 3 ) {
                    errors.put(String.valueOf(lineNo), List.of(String.format("Invalid row. Expects three values, only received %d", values.length)));
                    continue;
                }

                List<String> lineErrors = new ArrayList<>();

                if ( values[0].isBlank() ) {
                    lineErrors.add("Location code cannot be empty");
                }

                if ( values[1].isBlank() ) {
                    lineErrors.add("Location name cannot be empty");
                }

                if ( values[2].isBlank() ) {
                    lineErrors.add("Location address cannot be empty");
                }

                inputs.add(builder
                        .code((values[0]))
                        .name(values[1])
                        .address(values[2])
                        .build());

                if ( !lineErrors.isEmpty() ) {
                    errors.put(String.valueOf(lineNo), lineErrors);
                }

                lineNo++;
            }

            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }

            locationRepository.massInsert(inputs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Map<String, List<String>> validateWriteLocationParam (WriteLocationParam param) {
        Map<String, List<String>> errors = new HashMap<>();

        if ( Optional
                .ofNullable(param.getCode())
                .map(String::isBlank)
                .orElse(true)
        ) {
            errors.put("code", List.of("Location code cannot be empty"));
        }

        if ( Optional
                .ofNullable(param.getName())
                .map(String::isBlank)
                .orElse(true) ) {
            errors.put("name", List.of("Location name cannot be empty"));
        }

        if ( Optional
                .ofNullable(param.getAddress())
                .map(String::isBlank)
                .orElse(true) ) {
            errors.put("address", List.of("Location address cannot be empty"));
        }

        return errors;
    }

    @Override
    public LocationDTO create(WriteLocationParam param) {
        Map<String, List<String>> errors = validateWriteLocationParam(param);

        if ( errors.size() > 0 ) {
            throw new ValidationException(errors);
        }

        Location entity = Location.builder()
                .code(param.getCode())
                .name(param.getName())
                .address(param.getAddress())
                .build();

        entity = locationRepository.insert(entity);

        return LocationDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .address(entity.getAddress())
                .build();
    }

    @Override
    public LocationDTO getOne(long id) {
        return locationRepository.findById(id)
                .map(v -> LocationDTO.builder()
                        .name(v.getName())
                        .code(v.getCode())
                        .id(v.getId())
                        .address(v.getAddress())
                        .build())
                .orElseThrow(() -> new ResourceNotFoundException("Location does not exist"));
    }

    @Override
    public LocationDTO update(long id, WriteLocationParam newData) {
        Map<String, List<String>> errors = validateWriteLocationParam(newData);

        if ( !errors.isEmpty() ) {
            throw new ValidationException(errors);
        }

        Location row = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location does not exist"));

        row.setCode(newData.getCode());
        row.setName(newData.getName());
        row.setAddress(newData.getAddress());

        row = locationRepository.update(id, row);

        return LocationDTO.builder()
                .id(row.getId())
                .name(row.getName())
                .code(row.getCode())
                .address(row.getAddress())
                .build();
    }

    @Override
    public void delete(long id) {
        locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location does not exist"));

        locationRepository.delete(id);
    }
}
