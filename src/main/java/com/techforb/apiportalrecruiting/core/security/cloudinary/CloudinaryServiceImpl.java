package com.techforb.apiportalrecruiting.core.security.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

	private final Cloudinary cloudinary;
	private final LocalizedMessageService localizedMessageService;

	@Value("${cloudinary.folder-name}")
	private String cloudinaryFolderName;


	public Map uploadCv(MultipartFile multipartFile, String paramFolder) {
		try {
			String uniqueName = "cv_" + UUID.randomUUID();

			Map uploadParams = ObjectUtils.asMap(
					"folder", cloudinaryFolderName + paramFolder,
					"resource_type", "raw",
					"type", "authenticated",
					"private_cdn", true,
					"public_id", uniqueName + ".pdf"
			);

			Map result = cloudinary.uploader().upload(multipartFile.getBytes(), uploadParams);

			return result;

		} catch (IOException e) {
			throw new RuntimeException(localizedMessageService.getMessage("cloudinary.error_uploading"), e);
		}
	}

	public String generateSignedUrl(String publicId, String version) {
		Map<String, Object> options = ObjectUtils.asMap(
				"resource_type", "raw",
				"type", "authenticated",
				"version", version,
				"sign_url", true
		);

		return cloudinary.url().resourceType("raw")
				.type("authenticated")
				.publicId(publicId)
				.version(Long.parseLong(version))
				.signed(true)
				.generate();
	}
	@Override
	public void deleteAuthenticatedFile(String publicId) {
		try {

			Map<String, Object> options = ObjectUtils.asMap(
					"invalidate", true,
					"resource_type", "raw",
					"type", "authenticated"
			);


			Map result = cloudinary.uploader().destroy(publicId, options);

			if (!"ok".equals(result.get("result"))) {
				throw new RuntimeException(localizedMessageService.getMessage("cloudinary.error_delete_failure")
						+ " Response: " + result);
			}
		} catch (IOException e) {
			throw new RuntimeException(localizedMessageService.getMessage("cloudinary.error_delete"), e);
		}
	}
}
