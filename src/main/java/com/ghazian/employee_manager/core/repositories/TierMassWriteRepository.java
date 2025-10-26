package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Tier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TierMassWriteRepository {
    void massInsert(List<Tier> input);
}
