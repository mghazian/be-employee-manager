package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Location;
import com.ghazian.employee_manager.core.repositories.base.MassWriteRepository;
import com.ghazian.employee_manager.core.repositories.base.RawQueryRepository;

public interface LocationRepository extends RawQueryRepository<Location, Long>, MassWriteRepository<Location> {
}
