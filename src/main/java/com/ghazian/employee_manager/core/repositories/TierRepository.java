package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Tier;
import com.ghazian.employee_manager.core.repositories.base.MassWriteRepository;
import com.ghazian.employee_manager.core.repositories.base.RawQueryRepository;

import java.util.List;

public interface TierRepository extends RawQueryRepository<Tier, Long>, MassWriteRepository<Tier> {
}
