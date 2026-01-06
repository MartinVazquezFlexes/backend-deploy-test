package com.techforb.apiportalrecruiting.modules.portal.services;

import com.techforb.apiportalrecruiting.core.entities.Company;
import org.springframework.stereotype.Service;

@Service
public interface CompanyService {
    Company findById(Long companyId);
}
