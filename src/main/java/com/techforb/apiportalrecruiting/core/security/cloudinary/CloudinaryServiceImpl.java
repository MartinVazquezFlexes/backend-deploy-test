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
	private  static final String RESOURCE_TYPE="resource_type";
	private  static  final String AUTHENTICATED="authenticated";

	public Map<String, Object>  uploadCv(MultipartFile multipartFile, String paramFolder) {
		try {
			String uniqueName = "cv_" + UUID.randomUUID();

			Map<String,Object> uploadParams = ObjectUtils.asMap(
					"folder", cloudinaryFolderName + paramFolder,
					RESOURCE_TYPE, "raw",
					"type", AUTHENTICATED,
					"private_cdn", true,
					"public_id", uniqueName + ".pdf"
			);

			return  cloudinary.uploader().upload(multipartFile.getBytes(), uploadParams);

		} catch (IOException e) {
			throw new IllegalStateException(localizedMessageService.getMessage("cloudinary.error_uploading"), e);
		}
	}

	public String generateSignedUrl(String publicId, String version) {
		return cloudinary.url().resourceType("raw")
				.type(AUTHENTICATED)
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
					RESOURCE_TYPE, "raw",
					"type",AUTHENTICATED
			);


			Map<String, Object> result = cloudinary.uploader().destroy(publicId, options);

			if (!"ok".equals(result.get("result"))) {
					throw new IllegalStateException(localizedMessageService.getMessage("cloudinary.error_delete_failure")
						+ " Response: " + result);
			}
		} catch (IOException e) {
			throw new IllegalStateException(localizedMessageService.getMessage("cloudinary.error_delete"), e);
		}
	}
}
