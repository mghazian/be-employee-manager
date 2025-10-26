package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, LocationMassWriteRepository {
}
