package com.techforb.apiportalrecruiting.modules.portal.applications.services.impl;

import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.entities.*;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.security.cloudinary.CloudinaryService;
import com.techforb.apiportalrecruiting.core.services.UserService;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.CvWithCreationDateDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.dtos.cv.ResponsePagCvDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.ApplicationRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.repositories.CvRepository;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.CvService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class CvServiceImplTest {

	@MockitoBean
	private CvRepository cvRepository;

	@Autowired
	private CvService cvService;

	@MockitoBean
	private CloudinaryService cloudinaryService;

	@MockitoBean
	private MultipartFile cvFile;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private ApplicationRepository applicationRepository;

	@MockitoBean
	private LocalizedMessageService localizedMessageService;

	private Cv cv;
	private Person person;
	private String paramFolder;
	private UserEntity user;
	Map<String, Object> cloudinaryResponse;

	@BeforeEach
	void setUp() {
		Country country = new Country();
		country.setName("Argentina");

		Province province = new Province();
		province.setCountry(country);

		City city = new City();
		city.setProvince(province);

		Direction direction = new Direction();
		direction.setCity(city);

		user = new UserEntity();
		user.setEmail("test@example.com");
		user.setId(1L);
		user.setRoles(List.of(new Role(1L,"RECRUITER",List.of())));

		person = new Person();
		person.setId(1L);
		person.setUser(user);
		person.setSkills(List.of());

		user.setPerson(person);


		cv = new Cv();
		cv.setId(1L);
		cv.setVersion("123456");
		cv.setIsLast(true);
		cv.setPerson(person);
		cv.setName("CV - Juan Pérez");
		cv.setPublicId("cvs/person_1/cv_abc123");

		paramFolder = "/person_1";

		cloudinaryResponse = Map.of(
				"public_id", "cvs/person_1/cv_abc123.pdf",
				"version", "123456"
		);
	}

	@Test
	void uploadCv_Success() {
		when(cvFile.isEmpty()).thenReturn(false);
		when(cvFile.getContentType()).thenReturn("application/pdf");
		when(cvFile.getSize()).thenReturn(2 * 1024 * 1024L);

		when(cloudinaryService.uploadCv(cvFile, paramFolder)).thenReturn(cloudinaryResponse);
		when(cvRepository.save(any(Cv.class))).thenReturn(cv);

		Cv result = cvService.uploadCv(cvFile, person, paramFolder, false);

		assertEquals(cv.getPublicId(), result.getPublicId());
		assertEquals(cv.getVersion(), result.getVersion());
		assertEquals(cv.getPerson(), result.getPerson());

		verify(cvRepository, times(1)).save(any(Cv.class));
	}

	@Test
	void uploadCvSuccessAndFromProfileTrue() {
		Cv newCv = new Cv();
		newCv.setId(2L);
		newCv.setVersion("1234567");
		newCv.setIsLast(true);
		newCv.setPerson(person);
		newCv.setName("CV - Juan Pérez");
		newCv.setPublicId("cvs/person_1/cv_abc123");

		String paramFolderr = "/person_1";

		Map<String, Object> cloudinaryResponsee = Map.of(
				"public_id", "cvs/person_1/cv_abc123.pdf",
				"version", "123456"
		);

		when(cvFile.isEmpty()).thenReturn(false);
		when(cvFile.getContentType()).thenReturn("application/pdf");
		when(cvFile.getSize()).thenReturn(2 * 1024 * 1024L);

		when(cloudinaryService.uploadCv(cvFile, paramFolderr)).thenReturn(cloudinaryResponsee);
		when(cvRepository.save(any(Cv.class))).thenReturn(newCv);

		Cv result = cvService.uploadCv(cvFile, person, paramFolderr, true);
		cv.setIsLast(false);

		assertEquals(newCv.getPublicId(), result.getPublicId());
		assertEquals(newCv.getVersion(), result.getVersion());
		assertEquals(newCv.getPerson(), result.getPerson());
		assertTrue(newCv.getIsLast());
		assertFalse(cv.getIsLast());

		verify(cvRepository, times(1)).save(any(Cv.class));
	}


	@Test
	void uploadCv_Fails_WhenFileIsEmpty() {
		when(cvFile.isEmpty()).thenReturn(true);
		when(localizedMessageService.getMessage("cv.pdf_file"))
				.thenReturn("El CV debe ser un archivo PDF");
		Exception exception = assertThrows(RuntimeException.class, () -> cvService.uploadCv(cvFile, person, paramFolder, false));
		assertEquals("El CV debe ser un archivo PDF", exception.getMessage());
		verifyNoInteractions(cvRepository, cloudinaryService);
	}

	@Test
	void uploadCv_Fails_WhenFileIsNotPDF() {
		when(cvFile.isEmpty()).thenReturn(false);
		when(cvFile.getContentType()).thenReturn("image/png");
		when(localizedMessageService.getMessage("cv.pdf_file"))
				.thenReturn("El CV debe ser un archivo PDF");

		Exception exception = assertThrows(RuntimeException.class, () -> cvService.uploadCv(cvFile, person, paramFolder, false));
		assertEquals("El CV debe ser un archivo PDF", exception.getMessage());
		verifyNoInteractions(cvRepository, cloudinaryService);
	}

	@Test
	void uploadCv_Fails_WhenFileExceedsSizeLimit() {
		when(cvFile.isEmpty()).thenReturn(false);
		when(cvFile.getContentType()).thenReturn("application/pdf");
		when(cvFile.getSize()).thenReturn(3 * 1024 * 1024L);
		when(localizedMessageService.getMessage("cv.size"))
				.thenReturn("El CV no debe exceder los 2MB");

		Exception exception = assertThrows(RuntimeException.class, () -> cvService.uploadCv(cvFile, person, paramFolder, false));
		assertEquals("El CV no debe exceder los 2MB", exception.getMessage());
		verifyNoInteractions(cvRepository, cloudinaryService);
	}

	@Test
	void uploadCv_Fails_WhenCloudinaryThrowsError() {
		when(cvFile.isEmpty()).thenReturn(false);
		when(cvFile.getContentType()).thenReturn("application/pdf");
		when(cvFile.getSize()).thenReturn(1024 * 1024L);

		when(cloudinaryService.uploadCv(cvFile, paramFolder))
				.thenThrow(new RuntimeException("Error al subir el archivo"));

		Exception exception = assertThrows(RuntimeException.class, () -> cvService.uploadCv(cvFile, person, paramFolder, false));

		assertEquals("Error al subir el archivo", exception.getMessage());
		verifyNoInteractions(cvRepository);
	}

    @Test
    void uploadCv_Fails_WhenCloudinaryReturnsNull() {
        when(cvFile.isEmpty()).thenReturn(false);
        when(cvFile.getContentType()).thenReturn("application/pdf");
        when(cvFile.getSize()).thenReturn(1024L);

        when(cloudinaryService.uploadCv(cvFile, paramFolder)).thenReturn(null);
        when(localizedMessageService.getMessage("cv.not_be_null")).thenReturn("no puede ser null");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cvService.uploadCv(cvFile, person, paramFolder, false));

        assertEquals("no puede ser null", ex.getMessage());
        verifyNoInteractions(cvRepository);
    }


    @Test
	void getFilteredCvs_ShouldReturnEmptyPage_WhenNoResultsFound() {
		UserEntity userEntity = new UserEntity();
		userEntity.setEmail("test@example.com");
		userEntity.setId(1L);
		userEntity.setRoles(List.of(new Role(1L,"RECRUITER",List.of())));

		String country = "Desconocido";
		String skill = "Cobol";
		Pageable pageable = PageRequest.of(0, 10);

		Page<Cv> emptyPage = Page.empty(pageable);
		when(userService.getUserFromContext()).thenReturn(userEntity);
		when(cvRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

		Page<ResponsePagCvDTO> result = cvService.getFilteredCvs(country, skill, pageable);

		assertNotNull(result);
		assertEquals(0, result.getTotalElements());
	}

	@Test
	void getFilteredCvs_ShouldThrowException_WhenRepositoryFails() {
		UserEntity userEntity = new UserEntity();
		userEntity.setEmail("test@example.com");
		userEntity.setId(1L);
		userEntity.setRoles(List.of(new Role(1L,"RECRUITER",List.of())));

		String country = "Argentina";
		String skill = "Java";
		Pageable pageable = PageRequest.of(0, 10);

		when(userService.getUserFromContext()).thenReturn(userEntity);
		when(cvRepository.findAll(any(Specification.class), eq(pageable)))
				.thenThrow(new RuntimeException("Database error"));

		Exception exception = assertThrows(RuntimeException.class, () -> {
			cvService.getFilteredCvs(country, skill, pageable);
		});

		assertEquals("Database error", exception.getMessage());
	}


	@Test
	void shouldReturnAllCvsOfAuthenticatedUserSortedByCreationDateDesc() {
		Long userId = 1L;


		Cv cv1 = Cv.builder()
				.id(1L)
				.creationDate(LocalDateTime.now().minusDays(1))
				.name("CV1")
				.isLast(true)
				.publicId("cv1_public_id")
				.version("v1")
				.build();

		Cv cv2 = Cv.builder()
				.id(2L)
				.name("CV2")
				.creationDate(LocalDateTime.now())
				.isLast(false)
				.publicId("cv2_public_id")
				.version("v2")
				.build();
		when(userService.getUserFromContext()).thenReturn(user);
		when(cloudinaryService.generateSignedUrl("cv1_public_id", "v1"))
				.thenReturn("http://fakeurl.com/cv1");
		when(cloudinaryService.generateSignedUrl("cv2_public_id", "v2"))
				.thenReturn("http://fakeurl.com/cv2");

		List<Cv> cvList = Arrays.asList(
				cv2,
				cv1
		);
		Pageable pageable = PageRequest.of(0, 10, Sort.by("creationDate").descending());
		when(cvRepository.findAll(any(Specification.class), eq(pageable)))
				.thenReturn(new PageImpl<>(cvList));


		Page<CvWithCreationDateDTO> result = cvService.getCvsById(userId, null, pageable);


		assertEquals(2, result.getContent().size());
		assertTrue(result.getContent().get(0).getCreationDate().isAfter(result.getContent().get(1).getCreationDate()));
	}

	@Test
	void shouldReturnOnlyLastCvsWhenIsLastTrue() {
		Long userId = 1L;

		Cv cvLast = Cv.builder()
				.id(1L)
				.creationDate(LocalDateTime.now().minusDays(1))
				.name("CVLast")
				.isLast(true)
				.publicId("cvLast_public_id")
				.version("v1")
				.build();

		when(userService.getUserFromContext()).thenReturn(user);

		List<Cv> cvList = List.of(cvLast);
		Pageable pageable = PageRequest.of(0, 10);
		when(cvRepository.findAll(any(Specification.class), eq(pageable)))
				.thenReturn(new PageImpl<>(cvList));


		Page<CvWithCreationDateDTO> result = cvService.getCvsById(userId, true, pageable);


		assertEquals(1, result.getContent().size());
		assertEquals(true, (boolean) result.getContent().get(0).getIsLast());
	}

	@Test
	void shouldThrowExceptionWhenUserTriesToAccessOtherUsersCvs() {
		Long loggedInId = 2L;
		Long requestId = 1L;

		Person otherPerson = new Person();
		otherPerson.setId(2L);

		UserEntity authenticatedUser = new UserEntity();
		authenticatedUser.setId(loggedInId);
		authenticatedUser.setPerson(otherPerson);
		when(userService.getUserFromContext()).thenReturn(authenticatedUser);
		when(localizedMessageService.getMessage("user.without_permissions"))
				.thenReturn("El usuario que intenta ejecutar la accion no tiene permiso necesario.");

		PageRequest pageRequest = PageRequest.of(0, 10);

		UnauthorizedActionException ex = assertThrows(
				UnauthorizedActionException.class,
				() -> cvService.getCvsById(requestId, null, pageRequest)
		);

		assertEquals(
				"El usuario que intenta ejecutar la accion no tiene permiso necesario.",
				ex.getMessage()
		);
  }

@Test
void deleteCvByIdAndPersonId() throws IOException {
    Long cvId = 1L;
    when(this.cvRepository.findById(cvId)).thenReturn(Optional.of(cv));
    when(this.localizedMessageService.getMessage("cv.unauthorized_deletion")).thenReturn("Operacion no autorizada. Este CV pertenece a otro usuario.");
    when(this.cvRepository.findAllByPersonIdOrderByIdDesc(anyLong())).thenReturn(List.of(cv));
    
    when(this.applicationRepository.findByCvId(anyLong())).thenReturn(List.of());
    
    doNothing().when(this.cloudinaryService).deleteAuthenticatedFile(cv.getPublicId());
    
    boolean result = cvService.deleteCvByIdAndPersonId(cvId, 1L);
    assertTrue(result);
    
    verify(this.cvRepository).delete(any(Cv.class));
}

    @Test
    void deleteCv_shouldThrow_WhenPersonMismatch(){
        when(cvRepository.findById(1L)).thenReturn(Optional.of(cv));
        when(localizedMessageService.getMessage("cv.unauthorized_deletion")).thenReturn("nope");

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> cvService.deleteCvByIdAndPersonId(1L, 999L));
    }

    @Test
    void deleteCv_shouldSkipLastLogic_WhenCvIsNotLast() throws Exception {
        cv.setIsLast(false);
        when(cvRepository.findById(1L)).thenReturn(Optional.of(cv));
        when(applicationRepository.findByCvId(1L)).thenReturn(List.of());

        boolean ok = cvService.deleteCvByIdAndPersonId(1L, 1L);

        assertTrue(ok);
        verify(cvRepository, never()).findAllByPersonIdOrderByIdDesc(anyLong());
    }

    @Test
    void deleteCv_shouldSetNextLatestAsLast_WhenDeletingLastCv() throws Exception {
        cv.setIsLast(true);
        Cv next = new Cv();
        next.setId(2L);
        next.setIsLast(false);

        when(cvRepository.findById(1L)).thenReturn(Optional.of(cv));
        when(cvRepository.findAllByPersonIdOrderByIdDesc(1L)).thenReturn(List.of(cv, next));
        when(applicationRepository.findByCvId(1L)).thenReturn(List.of());

        when(cvRepository.save(any(Cv.class))).thenAnswer(inv -> inv.getArgument(0));

        cvService.deleteCvByIdAndPersonId(1L, 1L);

        assertTrue(next.getIsLast());
        verify(cvRepository).save(next);
    }

    @Test
    void deleteCv_shouldDetachCvFromApplications_WhenApplicationsExist() throws Exception {
        when(cvRepository.findById(1L)).thenReturn(Optional.of(cv));

        Application a1 = new Application(); a1.setCv(cv);
        Application a2 = new Application(); a2.setCv(cv);
        when(applicationRepository.findByCvId(1L)).thenReturn(List.of(a1, a2));
        when(applicationRepository.save(any(Application.class))).thenAnswer(inv -> inv.getArgument(0));

        cvService.deleteCvByIdAndPersonId(1L, 1L);

        assertNull(a1.getCv());
        assertNull(a2.getCv());
        verify(applicationRepository, times(2)).save(any(Application.class));
    }

    @Test
    void deleteCv_shouldNotCallCloudinary_WhenPublicIdIsNull() throws Exception {
        cv.setPublicId(null);
        when(cvRepository.findById(1L)).thenReturn(Optional.of(cv));
        when(applicationRepository.findByCvId(1L)).thenReturn(List.of());

        cvService.deleteCvByIdAndPersonId(1L, 1L);

        verify(cloudinaryService, never()).deleteAuthenticatedFile(anyString());
    }


    @Test
	void findCvById_withOutThrow() {
		Long cvId=1L;
		UserEntity userEntity=new UserEntity();

		Person personTest=new Person();
		personTest.setUser(userEntity);
		Cv cvToSearh=new Cv(1L,"v1","publicId","cv.pdf",true, LocalDateTime.now(),personTest);
		when(this.cvRepository.findById(1L)).thenReturn(Optional.of(cvToSearh));
		Cv resposne=this.cvService.findCvById(cvId);
		assertNotNull(resposne);
		assertEquals(cvToSearh.getId(),resposne.getId());
		assertEquals(cvToSearh.getName(),resposne.getName());
		verify(cvRepository, times(1)).findById(1L);

	}

	@Test
	void findCvById_withThrow() {
		Long cvId=1L;
		UserEntity userEntity=new UserEntity();
		Person personTested=new Person();
		personTested.setUser(userEntity);

		when(this.localizedMessageService.getMessage("cv.not_found")).thenReturn("El CV no fue encontrado.");
		Exception exception = assertThrows(EntityNotFoundException.class, () -> cvService.findCvById(cvId));
		assertEquals("El CV no fue encontrado.",exception.getMessage());
		verify(localizedMessageService).getMessage("cv.not_found");
	}

    @Test
    void uploadCv_fromProfileTrue_shouldUnsetExistingLastCv() {
        when(cvFile.isEmpty()).thenReturn(false);
        when(cvFile.getContentType()).thenReturn("application/pdf");
        when(cvFile.getSize()).thenReturn(1024L);

        when(cloudinaryService.uploadCv(cvFile, paramFolder)).thenReturn(cloudinaryResponse);

        Cv existingLast = new Cv();
        existingLast.setId(99L);
        existingLast.setIsLast(true);

        when(cvRepository.findByPersonIdAndIsLastTrue(person.getId()))
                .thenReturn(Optional.of(existingLast));


        when(cvRepository.save(any(Cv.class))).thenAnswer(inv -> inv.getArgument(0));

        Cv result = cvService.uploadCv(cvFile, person, paramFolder, true);

        assertTrue(result.getIsLast());
        assertFalse(existingLast.getIsLast());
        verify(cvRepository).save(existingLast);
    }

    @Test
    void getFilteredCvs_shouldThrow_WhenUserIsNotRecruiter() {
        UserEntity userEntity = new UserEntity();
        userEntity.setRoles(List.of(new Role(1L, "APPLICANT", List.of())));

        when(userService.getUserFromContext()).thenReturn(userEntity);
        when(localizedMessageService.getMessage("user.without_permissions"))
                .thenReturn("nope");

        Pageable pageable = PageRequest.of(0, 10); // ← fuera del lambda

        UnauthorizedActionException ex = assertThrows(
                UnauthorizedActionException.class,
                () -> cvService.getFilteredCvs("AR", "Java", pageable)
        );

        assertEquals("nope", ex.getMessage());
        verifyNoInteractions(cvRepository);
    }


    @Test
    void getFilteredCvs_shouldMapToDto_andIncludeCountryAndSkills() {
        when(userService.getUserFromContext()).thenReturn(user);

        Skill s1 = new Skill();
        s1.setDescription("Java");
        person.setSkills(List.of(s1));

        Cv cvWithData = new Cv();
        cvWithData.setId(1L);
        cvWithData.setPublicId("pid");
        cvWithData.setVersion("v1");
        cvWithData.setName("CV1");
        cvWithData.setPerson(person);

        when(cloudinaryService.generateSignedUrl("pid", "v1")).thenReturn("signed-url");

        Pageable pageable = PageRequest.of(0, 10);
        when(cvRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(cvWithData), pageable, 1));

        Page<ResponsePagCvDTO> result = cvService.getFilteredCvs("Argentina", "Java", pageable);

        assertEquals(1, result.getTotalElements());
        ResponsePagCvDTO dto = result.getContent().get(0);

        assertEquals("signed-url", dto.getCvUrl());
        assertEquals("CV1", dto.getCvName());
        assertEquals("test@example.com", dto.getPersonEmail());
        assertEquals("Argentina", dto.getPersonCountry());
        assertEquals(List.of("Java"), dto.getPersonSkills());
    }

    @Test
    void getFilteredCvs_shouldReturnNullCountry_whenDirectionIsNull() {
        when(userService.getUserFromContext()).thenReturn(user);

        Person p = new Person();
        p.setUser(user);
        p.setDirection(null);
        p.setSkills(List.of());

        Cv cv2 = new Cv();
        cv2.setPublicId("pid2");
        cv2.setVersion("v2");
        cv2.setName("CV2");
        cv2.setPerson(p);

        when(cloudinaryService.generateSignedUrl("pid2", "v2")).thenReturn("signed-url-2");

        Pageable pageable = PageRequest.of(0, 10);
        when(cvRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(cv2), pageable, 1));

        Page<ResponsePagCvDTO> result = cvService.getFilteredCvs("X", "Y", pageable);

        ResponsePagCvDTO dto = result.getContent().get(0);
        assertNull(dto.getPersonCountry());
    }

}