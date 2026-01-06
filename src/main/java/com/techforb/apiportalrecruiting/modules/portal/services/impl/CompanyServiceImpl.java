package com.techforb.apiportalrecruiting.modules.portal.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.Company;
import com.techforb.apiportalrecruiting.core.repositories.CompanyRepository;
import com.techforb.apiportalrecruiting.modules.portal.services.CompanyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Lazy
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final LocalizedMessageService localizedMessageService;
    @Override
    public Company findById(Long companyId) {
        return companyRepository.findById(companyId).orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("company.not_found_by_id")));
    }
}
