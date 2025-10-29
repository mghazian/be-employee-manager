package com.ghazian.employee_manager.core.repositories;

import com.ghazian.employee_manager.core.models.ApiCallHistory;
import com.ghazian.employee_manager.core.repositories.base.RawQueryRepository;
import org.springframework.stereotype.Repository;

public interface ApiCallHistoryRepository extends RawQueryRepository<ApiCallHistory, Long> {
}
