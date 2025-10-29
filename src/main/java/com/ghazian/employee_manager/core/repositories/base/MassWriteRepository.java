package com.ghazian.employee_manager.core.repositories.base;

import java.util.List;

public interface MassWriteRepository<T> {
    void massInsert(List<T> input);
}
