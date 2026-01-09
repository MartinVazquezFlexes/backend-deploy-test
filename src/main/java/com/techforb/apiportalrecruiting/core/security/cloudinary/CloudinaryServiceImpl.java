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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

	private final Cloudinary cloudinary;
	private final LocalizedMessageService localizedMessageService;
	private static final Logger log = LoggerFactory.getLogger(CloudinaryServiceImpl.class);


	@Value("${cloudinary.folder-name}")
	private String cloudinaryFolderName;
	private static final String RESOURCE_TYPE = "resource_type";
	// Cambiar de "authenticated" a "upload" para archivos públicos
	private static final String UPLOAD_TYPE = "upload";

	public Map<String, Object> uploadCv(MultipartFile multipartFile, String paramFolder) {
		try {
			String uniqueName = "cv_" + UUID.randomUUID() + ".pdf";

			Map<String, Object> uploadParams = ObjectUtils.asMap(
					"folder", cloudinaryFolderName + paramFolder,
					RESOURCE_TYPE, "raw",
					"type", UPLOAD_TYPE,
					"access_mode", "public",
					"public_id", uniqueName
			);

			Map<String, Object> result = cloudinary.uploader().upload(multipartFile.getBytes(), uploadParams);

			// DEBUG: Ver qué devuelve Cloudinary
			log.info("=== CLOUDINARY UPLOAD RESULT ===");
			log.info("secure_url: {}", result.get("secure_url"));
			log.info("public_id: {}", result.get("public_id"));
			log.info("url: {}", result.get("url"));
			log.info("type: {}", result.get("type"));
			log.info("================================");

			return result;

		} catch (IOException e) {
			throw new IllegalStateException(localizedMessageService.getMessage("cloudinary.error_uploading"), e);
		}
	}


	// Para archivos públicos, la URL que viene en el response del upload es suficiente
	public String generateSignedUrl(String publicId, String version) {
		return cloudinary.url().resourceType("raw")
				.type(UPLOAD_TYPE)  //usar "upload" en lugar de "authenticated"
				.publicId(publicId)
				.version(Long.parseLong(version))
				// .signed(true)  //los archivos públicos no necesitan firma
				.generate();
	}

	@Override
	public void deleteAuthenticatedFile(String publicId) {
		try {
			// Primero intentar eliminar como "upload" (archivos nuevos)
			Map<String, Object> options = ObjectUtils.asMap(
					"invalidate", true,
					RESOURCE_TYPE, "raw",
					"type", UPLOAD_TYPE
			);

			Map<String, Object> result = cloudinary.uploader().destroy(publicId, options);

			// Si no se encontró, intentar con "authenticated" (archivos viejos)
			if ("not found".equals(result.get("result"))) {
				options = ObjectUtils.asMap(
						"invalidate", true,
						RESOURCE_TYPE, "raw",
						"type", "authenticated"
				);

				result = cloudinary.uploader().destroy(publicId, options);
			}

			if (!"ok".equals(result.get("result"))) {
				throw new IllegalStateException(localizedMessageService.getMessage("cloudinary.error_delete_failure")
						+ " Response: " + result);
			}
		} catch (IOException e) {
			throw new IllegalStateException(localizedMessageService.getMessage("cloudinary.error_delete"), e);
		}
	}

	@Override
	public String generatePublicUrl(String publicId) {
		return cloudinary.url()
				.resourceType("raw")
				.type(UPLOAD_TYPE)
				.publicId(publicId)
				.secure(true) // ← AGREGAR ESTO
				.generate();
	}


}