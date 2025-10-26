package com.ghazian.employee_manager.locations.services;

import com.ghazian.employee_manager.core.dto.Pagination;
import com.ghazian.employee_manager.locations.dto.LocationDTO;
import com.ghazian.employee_manager.locations.dto.WriteLocationParam;
import org.springframework.web.multipart.MultipartFile;

public interface LocationService {
    Pagination<LocationDTO> getPaginated(int pageIndex, int size);
    void importFile(MultipartFile file);
    LocationDTO create(WriteLocationParam param);
    LocationDTO getOne(long id);
    LocationDTO update(long id, WriteLocationParam newData);
    void delete(long id);
}
