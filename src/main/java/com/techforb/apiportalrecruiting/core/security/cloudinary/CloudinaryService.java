package com.techforb.apiportalrecruiting.core.security.cloudinary;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public interface CloudinaryService {

	Map uploadCv(MultipartFile multipartFile, String paramFolder);

	String generateSignedUrl(String publicId, String version);
	void deleteAuthenticatedFile(String publicId)throws IOException;
}
