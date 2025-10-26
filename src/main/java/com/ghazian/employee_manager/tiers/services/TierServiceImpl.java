package com.ghazian.employee_manager.tiers.services;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.core.exceptions.ResourceNotFoundException;
import com.ghazian.employee_manager.core.exceptions.ValidationException;
import com.ghazian.employee_manager.core.models.Tier;
import com.ghazian.employee_manager.core.repositories.TierRepository;
import com.ghazian.employee_manager.tiers.dto.TierDTO;
import com.ghazian.employee_manager.tiers.dto.WriteTierParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TierServiceImpl implements TierService {

    private final TierRepository tierRepository;

    @Override
    public Pagination<TierDTO> getPaginated(int pageIndex, int size) {
        Page<Tier> entries = tierRepository.findAll(PageRequest.of(pageIndex, size));

        final TierDTO.TierDTOBuilder builder = TierDTO.builder();

        final List<TierDTO> tierDTOs = entries.stream().map(v ->
                builder
                    .id(v.getId())
                    .code(v.getCode())
                    .name(v.getName())
                    .build()
        ).toList();

        return Pagination.<TierDTO>builder()
                .data(tierDTOs)
                .totalPages(entries.getTotalPages())
                .build();
    }

    @Override
    public void importFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Map<String, Object> errors = new HashMap<>();
            List<Tier> inputs = new ArrayList<>();

            Tier.TierBuilder builder = Tier.builder();

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
                    lineErrors.add("Tier code cannot be empty");
                }

                if ( values[1].isBlank() ) {
                    lineErrors.add("Tier name cannot be empty");
                }

                inputs.add(builder
                        .code(Long.parseLong(values[0])) // Parsing may slow down processing. Should we remove the parsing?
                        .name(values[1])
                        .build());

                lineNo++;
            }

            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }

            tierRepository.massInsert(inputs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Map<String, Object> validateWriteTierParam (WriteTierParam param) {
        Map<String, Object> errors = new HashMap<>();

        List<String> codeErrorList = new ArrayList<>();
        if ( Optional
                .ofNullable(param.getCode())
                .map(String::isBlank)
                .orElse(true)
        ) {
            codeErrorList.add("Tier code cannot be empty");
        }
        else {
            try {
                Long.parseLong(param.getCode());
            } catch (NumberFormatException e) {
                codeErrorList.add("Tier code is not an integer");
            }
        }

        if ( !codeErrorList.isEmpty() ) {
            errors.put("code", codeErrorList);
        }

        if ( Optional
                .ofNullable(param.getName())
                .map(String::isBlank)
                .orElse(true) ) {
            errors.put("name", List.of("Tier name cannot be empty"));
        }

        return errors;
    }

    @Override
    public TierDTO create(WriteTierParam param) {
        Map<String, Object> errors = validateWriteTierParam(param);

        if ( errors.size() > 0 ) {
            throw new ValidationException(errors);
        }

        Tier entity = Tier.builder()
                .code(Long.parseLong(param.getCode()))
                .name(param.getName())
                .build();

        entity = tierRepository.save(entity);

        return TierDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .build();
    }

    @Override
    public TierDTO getOne(long id) {
        return tierRepository.findById(id)
                .map(v -> TierDTO.builder()
                        .name(v.getName())
                        .code(v.getCode())
                        .id(v.getId())
                        .build())
                .orElseThrow(() -> new ResourceNotFoundException("Tier does not exist"));
    }

    @Override
    public TierDTO update(long id, WriteTierParam newData) {
        Map<String, Object> errors = validateWriteTierParam(newData);

        if ( !errors.isEmpty() ) {
            throw new ValidationException(errors);
        }

        Tier row = tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier does not exist"));

        row.setCode(Long.parseLong(newData.getCode()));
        row.setName(newData.getName());

        row = tierRepository.save(row);

        return TierDTO.builder()
                .id(row.getId())
                .name(row.getName())
                .code(row.getCode())
                .build();
    }

    @Override
    public void delete(long id) {
        Tier row = tierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tier does not exist"));

        tierRepository.delete(row);
    }
}
