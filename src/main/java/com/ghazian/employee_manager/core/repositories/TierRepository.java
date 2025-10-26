package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.Tier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TierRepository extends JpaRepository<Tier, Long>, TierMassWriteRepository {
}
