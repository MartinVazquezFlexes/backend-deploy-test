package com.techforb.apiportalrecruiting.core.security.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.Url;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@ActiveProfiles("test")
@SpringBootTest
class CloudinaryServiceTest {

	@Autowired
	private CloudinaryService cloudinaryService;

	@MockitoBean
	private Cloudinary cloudinary;

	@MockitoBean
	private MultipartFile multipartFile;
	@MockitoBean
	private LocalizedMessageService localizedMessageService;
	private String folder;
	private Uploader uploaderMock;
	private Url urlMock;

	@BeforeEach
	void setUp() {
		folder = "/person_test";

		uploaderMock = mock(Uploader.class);
		when(cloudinary.uploader()).thenReturn(uploaderMock);

		urlMock = mock(Url.class);
		when(cloudinary.url()).thenReturn(urlMock);

		when(urlMock.resourceType("raw")).thenReturn(urlMock);
		when(urlMock.type("authenticated")).thenReturn(urlMock);
		when(urlMock.secure(true)).thenReturn(urlMock);
		when(urlMock.privateCdn(true)).thenReturn(urlMock);
		when(urlMock.signed(true)).thenReturn(urlMock);
		when(urlMock.version(any())).thenReturn(urlMock);
		when(urlMock.publicId(any())).thenReturn(urlMock);
		when(urlMock.generate()).thenReturn("https://res.cloudinary.com/test-cv-url");
	}



	@Test
	void uploadCv_ShouldReturnUploadResult_WhenSuccessful() throws Exception {
		byte[] fileBytes = new byte[]{1, 2, 3};
		when(multipartFile.getBytes()).thenReturn(fileBytes);

		Map<String, Object> mockResponse = new HashMap<>();
		mockResponse.put("public_id", "cvs/person_test/cv_abc123");
		mockResponse.put("version", "123456");

		when(uploaderMock.upload(any(), anyMap())).thenReturn(mockResponse);

		Map result = cloudinaryService.uploadCv(multipartFile, folder);

		assertNotNull(result);
		assertEquals("cvs/person_test/cv_abc123", result.get("public_id"));
		assertEquals("123456", result.get("version"));
	}

	@Test
	void uploadCv_ShouldThrowRuntimeException_WhenExceptionOccurs() throws Exception {
		when(multipartFile.getBytes()).thenThrow(new RuntimeException("Error uploading PDF to Cloudinary"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			cloudinaryService.uploadCv(multipartFile, folder);
		});

		assertEquals("Error uploading PDF to Cloudinary", exception.getMessage());
	}

	@Test
	void generateSignedUrl_ShouldReturnUrl_WhenValidData() {
		String publicId = "cvs/person_test/cv_abc123";
		String version = "1712345678";
		String testUrl = "https://res.cloudinary.com/cloud-name/raw/authenticated/--signature--/1712345678/cvs/person_test/cv_abc123.pdf";
		when(urlMock.generate()).thenReturn(testUrl);

		String url = cloudinaryService.generateSignedUrl(publicId, version);

		assertEquals(testUrl, url);
		assertNotNull(url);
		assertTrue(url.contains("cvs/person_test/cv_abc123"));
		assertTrue(url.contains("authenticated"));
		assertTrue(url.contains("1712345678"));
	}

	@Test
	void generateSignedUrl_ShouldThrowException_WhenVersionInvalid() {
		String publicId = "cvs/person_test/cv_abc123";
		String version = "invalid";

		Exception exception = assertThrows(NumberFormatException.class, () -> {
			cloudinaryService.generateSignedUrl(publicId, version);
		});

		assertEquals("For input string: \"invalid\"", exception.getMessage());
	}


	@Test
	void deleteAuthenticatedFile_ShouldDeleteFileSuccessfully() throws Exception {
		String publicId = "cvs/person_test/cv_abc123";

		Map<String, Object> mockDeleteResponse = new HashMap<>();
		mockDeleteResponse.put("result", "ok");

		when(uploaderMock.destroy(any(), anyMap())).thenReturn(mockDeleteResponse);

		assertDoesNotThrow(() -> cloudinaryService.deleteAuthenticatedFile(publicId));
	}

	@Test
	void deleteAuthenticatedFile_ShouldThrowRuntimeException_WhenResultIsNotOk() throws Exception {
		String publicId = "cvs/person_test/cv_invalid";

		Map<String, Object> mockDeleteResponse = new HashMap<>();
		mockDeleteResponse.put("result", "not_found");

		when(uploaderMock.destroy(any(), anyMap())).thenReturn(mockDeleteResponse);
		when(localizedMessageService.getMessage("cloudinary.error_delete_failure")).thenReturn("No se pudo eliminar el archivo.");
		when(localizedMessageService.getMessage("cloudinary.error_delete")).thenReturn("Error al eliminar el archivo.");

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			cloudinaryService.deleteAuthenticatedFile(publicId);
		});

		assertTrue(exception.getMessage().contains("No se pudo eliminar el archivo."));
		assertTrue(exception.getMessage().contains("not_found"));
	}




}