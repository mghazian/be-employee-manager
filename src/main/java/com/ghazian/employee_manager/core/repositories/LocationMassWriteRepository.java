package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Location;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationMassWriteRepository {
    void massInsert(List<Location> input);
}
