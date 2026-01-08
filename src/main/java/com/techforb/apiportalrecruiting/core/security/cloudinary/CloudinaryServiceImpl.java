package com.techforb.apiportalrecruiting.core.security.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

	private final Cloudinary cloudinary;
	private final LocalizedMessageService localizedMessageService;

	@Value("${cloudinary.folder-name}")
	private String cloudinaryFolderName;
	private static final String RESOURCE_TYPE = "resource_type";
	// Cambiar de "authenticated" a "upload" para archivos públicos
	private static final String UPLOAD_TYPE = "upload";

	public Map<String, Object> uploadCv(MultipartFile multipartFile, String paramFolder) {
		try {
			String uniqueName = "cv_" + UUID.randomUUID();

			Map<String, Object> uploadParams = ObjectUtils.asMap(
					"folder", cloudinaryFolderName + paramFolder,
					RESOURCE_TYPE, "raw",
					"type", UPLOAD_TYPE,  //usar "upload" en lugar de "authenticated"
					"access_mode", "public",  //hacer el archivo público
					"public_id", uniqueName + ".pdf"
					// Removido "private_cdn", true  //quitar CDN privado
			);

			return cloudinary.uploader().upload(multipartFile.getBytes(), uploadParams);

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
	public byte[] downloadFile(String publicId) throws IOException {
		try {
			// Primero intentar generar URL pública (archivos nuevos)
			String url = cloudinary.url()
					.resourceType("raw")
					.type(UPLOAD_TYPE)
					.publicId(publicId)
					.generate();

			try {
				return downloadFromUrl(url);
			} catch (IOException e) {
				// Si falla, intentar con URL firmada (archivos viejos autenticados)
				url = cloudinary.url()
						.resourceType("raw")
						.type("authenticated")
						.publicId(publicId)
						.signed(true)
						.generate();

				return downloadFromUrl(url);
			}
		} catch (IOException e) {
			throw new IOException(localizedMessageService.getMessage("cloudinary.error_downloading"), e);
		}
	}

	private byte[] downloadFromUrl(String url) throws IOException {
		URL fileUrl = new URL(url);
		try (InputStream in = fileUrl.openStream();
			 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}

			return out.toByteArray();
		}
	}
}