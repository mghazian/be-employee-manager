package com.ghazian.employee_manager.tiers.services;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.tiers.dto.TierDTO;
import com.ghazian.employee_manager.tiers.dto.WriteTierParam;
import org.springframework.web.multipart.MultipartFile;

public interface TierService {
    Pagination<TierDTO> getPaginated(int pageIndex, int size);
    void importFile(MultipartFile file);
    TierDTO create(WriteTierParam param);
    TierDTO getOne(long id);
    TierDTO update(long id, WriteTierParam newData);
    void delete(long id);
}
