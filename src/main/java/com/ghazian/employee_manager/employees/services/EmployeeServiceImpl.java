package com.ghazian.employee_manager.employees.services;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.core.exceptions.ResourceNotFoundException;
import com.ghazian.employee_manager.core.exceptions.ValidationException;
import com.ghazian.employee_manager.core.models.Employee;
import com.ghazian.employee_manager.core.repositories.EmployeeRepository;
import com.ghazian.employee_manager.core.repositories.projections.GetEmployeeQueryDTO;
import com.ghazian.employee_manager.departments.dto.DepartmentDTO;
import com.ghazian.employee_manager.employees.dto.EmployeeApiResponseDTO;
import com.ghazian.employee_manager.employees.dto.EmployeeOption;
import com.ghazian.employee_manager.employees.dto.WriteEmployeeParam;
import com.ghazian.employee_manager.locations.dto.LocationDTO;
import com.ghazian.employee_manager.tiers.dto.TierDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    DateTimeFormatter entryDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm");
    ZoneId zoneId = ZoneId.of("GMT+07"); // TODO: Allow set the zone id dynamically

    // Inject builders to prevent recreating them every time the function called - help reduce memory usage
    private EmployeeApiResponseDTO transformGetEmployeeQueryDtoToEmployeeApiResponseDto (
            GetEmployeeQueryDTO v,
            EmployeeApiResponseDTO.EmployeeApiResponseDTOBuilder employeeBuilder,
            TierDTO.TierDTOBuilder tierBuilder,
            LocationDTO.LocationDTOBuilder locationBuilder,
            DepartmentDTO.DepartmentDTOBuilder departmentBuilder,
            EmployeeApiResponseDTO.SimpleSupervisorDTO.SimpleSupervisorDTOBuilder supervisorBuilder
    ) {
        return employeeBuilder
                .id(v.getId())
                .name(v.getName())
                .no(v.getNo())
                .department(departmentBuilder
                        .id(v.getDepartmentId())
                        .name(v.getDepartmentName())
                        .code(v.getDepartmentCode())
                        .build())
                .tier(tierBuilder
                        .id(v.getTierId())
                        .name(v.getTierName())
                        .code(v.getTierCode())
                        .build())
                .location(locationBuilder
                        .id(v.getLocationId())
                        .name(v.getLocationName())
                        .code(v.getLocationCode())
                        .build())
                .salary(v.getSalary())
                .supervisor(v.getSupervisorId() == null
                        ? null
                        : supervisorBuilder
                            .id(v.getSupervisorId())
                            .no(v.getSupervisorNo())
                            .name(v.getSupervisorName())
                            .build())
                .entryDate(v.getEntryDate().atZone(zoneId))
                .build();
    }

    private EmployeeApiResponseDTO transformGetEmployeeQueryDtoToEmployeeApiResponseDto (
            GetEmployeeQueryDTO v
    ) {
        return transformGetEmployeeQueryDtoToEmployeeApiResponseDto(
                v,
                EmployeeApiResponseDTO.builder(),
                TierDTO.builder(),
                LocationDTO.builder(),
                DepartmentDTO.builder(),
                EmployeeApiResponseDTO.SimpleSupervisorDTO.builder()
        );
    }

    @Override
    public Pagination<EmployeeApiResponseDTO> getPaginated(int pageIndex, int size) {
        Page<GetEmployeeQueryDTO> entries = employeeRepository.findAllAsCompleteEntries(PageRequest.of(pageIndex, size));

        EmployeeApiResponseDTO.EmployeeApiResponseDTOBuilder employeeBuilder = EmployeeApiResponseDTO.builder();
        TierDTO.TierDTOBuilder tierBuilder = TierDTO.builder();
        LocationDTO.LocationDTOBuilder locationBuilder = LocationDTO.builder();
        DepartmentDTO.DepartmentDTOBuilder departmentBuilder = DepartmentDTO.builder();
        EmployeeApiResponseDTO.SimpleSupervisorDTO.SimpleSupervisorDTOBuilder supervisorBuilder = EmployeeApiResponseDTO.SimpleSupervisorDTO.builder();


        final List<EmployeeApiResponseDTO> employeeApiResponseDTOS = entries.stream().map(v ->
                transformGetEmployeeQueryDtoToEmployeeApiResponseDto(
                        v,
                        employeeBuilder,
                        tierBuilder,
                        locationBuilder,
                        departmentBuilder,
                        supervisorBuilder
                )
        ).toList();

        return Pagination.<EmployeeApiResponseDTO>builder()
                .data(employeeApiResponseDTOS)
                .totalPages(entries.getTotalPages())
                .build();
    }

    @Override
    public void importFile(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Map<String, List<String>> errors = new HashMap<>();
            List<Employee> inputs = new ArrayList<>();

            Employee.EmployeeBuilder builder = Employee.builder();

            String line = reader.readLine(); // Read header

            long lineNo = 2; // 1-based
            for ( line = reader.readLine(); line != null; line = reader.readLine() ) {
                String[] values = line.split(",", -1);

                if ( values.length < 3 ) {
                    errors.put(String.valueOf(lineNo), List.of(String.format("Invalid row. Expects three values, only received %d", values.length)));
                    continue;
                }

                List<String> lineErrors = validateWriteEmployeeParam(WriteEmployeeParam.builder()
                                .no(values[0])
                                .name(values[1])
                                .tierCode(values[2])
                                .locationCode(values[3])
                                .departmentCode(values[4])
                                .supervisorNo(values[5])
                                .salary(values[6])
                                .entryDate(values[7])
                                .build())
                        .values()
                        .stream()
                        .reduce(new ArrayList<String>(), (acc, val) -> { acc.addAll(val); return acc; });

                Long supervisorNo = null;
                if ( Optional.ofNullable(values[5])
                        .map(v -> !v.isBlank())
                        .orElse(false)
                ) {
                    supervisorNo = Long.parseLong(values[5]);
                }
                inputs.add(builder
                        .no(Long.parseLong(values[0]))
                        .name(values[1])
                        .tierCode(Long.parseLong(values[2]))
                        .locationCode(values[3])
                        .departmentCode(values[4])
                        .supervisorNo(supervisorNo)
                        .salary(Long.parseLong(values[6]))
                        .entryDate(LocalDateTime.parse(values[7], entryDateFormatter).atZone(zoneId))
                        .build());

                if ( !lineErrors.isEmpty() ) {
                    errors.put(String.valueOf(lineNo), lineErrors);
                }

                lineNo++;
            }

            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }

            employeeRepository.massInsert(inputs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Long nullSafeConvertStringToLong (String numberString) {
        return numberString == null
                ? null
                : Long.parseLong(numberString);
    }

    Map<String, List<String>> validateWriteEmployeeParam (WriteEmployeeParam param) {
        Map<String, List<String>> errors = new HashMap<>();

        // No
        if ( Optional.ofNullable(param.getNo())
                .map(v -> v.isBlank())
                .orElse(true)
        ) {
            errors.put("no", List.of("Employee number cannot be empty"));
        } else {
            if ( !param.getNo().matches("^\\d+$") ) {
                errors.put("no", List.of("Employee number must be all number"));
            }
        }

        // Name
        if ( Optional.ofNullable(param.getName())
                .map(v -> v.isBlank())
                .orElse(true)
        ) {
            errors.put("name", List.of("Employee name cannot be empty"));
        }

        // Tier code
        if ( Optional.ofNullable(param.getTierCode())
                .map(v -> v.isBlank())
                .orElse(true)
        ) {
            errors.put("tier_code", List.of("Tier code cannot be empty"));
        } else {
            if ( !param.getTierCode().matches("^\\d+$") ) {
                errors.put("tier_code", List.of("Tier code must be all numbers"));
            }
        }

        // Location code
        if ( Optional.ofNullable(param.getNo())
                .map(v -> v.isBlank())
                .orElse(true)
        ) {
            errors.put("location_code", List.of("Location code cannot be empty"));
        }

        // Department code
        if ( Optional.ofNullable(param.getDepartmentCode())
                .map(v -> v.isBlank())
                .orElse(true)
        ) {
            errors.put("department_code", List.of("Department code cannot be empty"));
        }

        // Supervisor code
        if ( !Optional.ofNullable(param.getSupervisorNo())
                .map(v -> v.isBlank())
                .orElse(true)
        ) {
            if ( !param.getSupervisorNo().matches("^\\d+$") ) {
                errors.put("supervisor_no", List.of("Employee supervisor number must be all number"));
            }
        }

        // Salary
        if ( Optional.ofNullable(param.getSalary())
                .map(v -> v.isBlank())
                .orElse(true)
        ) {
            errors.put("salary", List.of("Salary cannot be empty"));
        } else {
            if ( !param.getSalary().matches("^\\d+$") ) {
                errors.put("salary", List.of("Salary must be all number"));
            }
        }

        // Entry date
        if ( Optional.ofNullable(param.getEntryDate())
                .map(v -> v.isBlank())
                .orElse(true)
        ) {
            errors.put("entry_date", List.of("Entry date cannot be empty"));
        } else {
            try {
                LocalDateTime.parse(param.getEntryDate(), entryDateFormatter);
            } catch (DateTimeParseException ex) {
                errors.put("entry_date", List.of("Entry date is not valid"));
            }
        }

        return errors;
    }

    @Override
    public EmployeeApiResponseDTO create(WriteEmployeeParam param) {
        Map<String, List<String>> errors = validateWriteEmployeeParam(param);

        if ( errors.size() > 0 ) {
            throw new ValidationException(errors);
        }

        Employee entity = Employee.builder()
                .no(Long.parseLong(param.getNo()))
                .name(param.getName())
                .supervisorNo(nullSafeConvertStringToLong(param.getSupervisorNo()))
                .tierCode(Long.parseLong(param.getTierCode()))
                .locationCode(param.getLocationCode())
                .departmentCode(param.getDepartmentCode())
                .salary(Long.parseLong(param.getSalary()))
                .entryDate(LocalDateTime.parse(param.getEntryDate(), entryDateFormatter).atZone(zoneId))
                .build();

        entity = employeeRepository.insert(entity);

        return getOne(entity.getId());
    }

    @Override
    public EmployeeApiResponseDTO getOne(long id) {
        return employeeRepository.findOneAsCompleteEntry(id)
                .map(this::transformGetEmployeeQueryDtoToEmployeeApiResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Employee does not exist"));
    }

    @Override
    public EmployeeApiResponseDTO update(long id, WriteEmployeeParam newData) {
        Map<String, List<String>> errors = validateWriteEmployeeParam(newData);

        if ( !errors.isEmpty() ) {
            throw new ValidationException(errors);
        }

        Employee row = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee does not exist"));

        row.setNo(Long.parseLong(newData.getNo()));
        row.setName(newData.getName());
        row.setDepartmentCode(newData.getDepartmentCode());
        row.setLocationCode(newData.getLocationCode());
        row.setTierCode(Long.parseLong(newData.getTierCode()));
        row.setSupervisorNo(nullSafeConvertStringToLong(newData.getSupervisorNo()));
        row.setSalary(Long.parseLong(newData.getSalary()));
        row.setEntryDate(LocalDateTime.parse(newData.getEntryDate(), entryDateFormatter).atZone(zoneId));

        row = employeeRepository.update(id, row);

        return getOne(row.getId());
    }

    @Override
    public void delete(long id) {
        employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee does not exist"));

        employeeRepository.delete(id);
    }

    @Override
    public List<EmployeeOption> getAllOptions() {
        List<EmployeeOption> output = new ArrayList<>();
        EmployeeOption.EmployeeOptionBuilder builder = EmployeeOption.builder();

        for (Employee employee : employeeRepository.findAll(null).getContent()) {
            output.add(builder.no(employee.getNo())
                    .name(employee.getName())
                    .build());
        }

        return output;
    }
}
