package com.techforb.apiportalrecruiting.core.security.linkedin;

import java.util.Map;


public interface LinkedInService {
    String getAuthorizationUrl(String state);
    
    String getAccessToken(String code);
    
   
    Map<String, Object> getUserProfile(String accessToken);
    
    String getUserEmail(String accessToken);
} 