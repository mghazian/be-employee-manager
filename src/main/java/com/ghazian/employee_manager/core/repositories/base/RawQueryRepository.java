package com.ghazian.employee_manager.core.repositories.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RawQueryRepository<T, ID>  {
    T insert(T input);
    T update(ID id, T input);
    void delete(ID id);

    Page<T> findAll(Pageable pageable);
    Optional<T> findById(ID id);
}
