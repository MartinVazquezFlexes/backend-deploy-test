package com.techforb.apiportalrecruiting.modules.portal.phone.service;

import com.techforb.apiportalrecruiting.core.entities.Phone;
import com.techforb.apiportalrecruiting.modules.portal.phone.dto.PhoneDTO;

public interface PhoneService {
    Phone updatePhoneForPerson(Long personId, String phoneNumber);
    PhoneDTO getPhoneByPersonId(Long personId);
    void deletePhoneByPersonId(Long personId);
}
