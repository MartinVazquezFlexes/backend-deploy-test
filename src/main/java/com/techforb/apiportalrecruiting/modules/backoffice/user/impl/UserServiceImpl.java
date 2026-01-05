package com.techforb.apiportalrecruiting.modules.backoffice.user.impl;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.config.mapper.ModelMapperUtils;
import com.techforb.apiportalrecruiting.core.entities.Role;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.exceptions.UnauthorizedActionException;
import com.techforb.apiportalrecruiting.core.security.jwt.Jwt;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.core.security.firebase.FirebaseAuthenticationToken;
import com.techforb.apiportalrecruiting.core.security.firebase.FirebaseAuthService;
import com.techforb.apiportalrecruiting.core.services.RoleService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.CustomUserDetails;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserRepository;
import com.techforb.apiportalrecruiting.modules.backoffice.user.UserService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.EmailChangePasswordRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.EmailLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.GoogleLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.UserLoginResponseDTO;
import com.techforb.apiportalrecruiting.modules.portal.applications.services.PersonService;
import com.techforb.apiportalrecruiting.core.exceptions.PasswordMismatchException;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${API_KEY}")
    private String apiKey;
    private String firebaseAuthUrlWithPassword;
    private String firebaseAuthUrlWithProvider;
  private final UserRepository userRepository;
  private final PersonService personService;
  private final RoleService roleService;
  private final FirebaseAuthService firebaseAuthService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final ModelMapperUtils modelMapperUtils;
  private final PasswordEncoder passwordEncoder;
  private final LocalizedMessageService localizedMessageService;
  private final RestTemplate restTemplate;

    private static final String ROLE_NAME = "APPLICANT";
    private static final String EMAIL = "email";
    private static final String ID_TOKEN = "idToken";

    @PostConstruct
    public void init() {
        this.firebaseAuthUrlWithPassword =
                "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

        this.firebaseAuthUrlWithProvider =
                "https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=" + apiKey;
    }


	@Override
	public void emailRegister(EmailLoginRequestDTO newUser) throws FirebaseAuthException {
		String email = newUser.getEmail();
		String password = newUser.getPassword();
		firebaseAuthService.registerUser(email, password);

		if (userRepository.findByEmail(email).isEmpty()) addUserToDb(email, ROLE_NAME,password);
	}

	@Override
	public LoginResponseDTO emailLogin(EmailLoginRequestDTO loginRequestDTO) throws FirebaseAuthException {
		String email = loginRequestDTO.getEmail();
		String password = loginRequestDTO.getPassword();

		Map<String, Object> request = new HashMap<>();
		request.put(EMAIL, email);
		request.put("password", password);
		request.put("returnSecureToken", true);

    Map<String, Object> response;
     try {
       response = callFirebaseAuth(firebaseAuthUrlWithPassword, request);
    } catch (RestClientException e) {
        throw new AuthenticationServiceException(localizedMessageService.getMessage("firebase.auth.error", e.getMessage()), e);
    }

    if (response == null || !response.containsKey(ID_TOKEN)) {
        throw new AuthenticationServiceException(localizedMessageService.getMessage("firebase.invalid_id_token"));
    }

		String idToken = (String) response.get(ID_TOKEN);
   
   
		firebaseAuthService.verifyToken(idToken);

		if (userRepository.findByEmail(email).isEmpty()) addUserToDb(email, ROLE_NAME, password);

		Authentication auth = authenticationManager.authenticate(
				new FirebaseAuthenticationToken(idToken)
		);

		CustomUserDetails userAuthenticated = (CustomUserDetails) auth.getPrincipal();

		Jwt jwt = new Jwt(jwtService.generateToken(userAuthenticated));

		UserLoginResponseDTO userResponse = modelMapperUtils.map(userAuthenticated.getUserEntity(), UserLoginResponseDTO.class);

		return new LoginResponseDTO(userResponse, jwt);
	}

	@Override
	public LoginResponseDTO googleLogin(GoogleLoginRequestDTO googleRequest) throws FirebaseAuthException {
		String accessToken = googleRequest.getAccessToken();

		Map<String, Object> request = new HashMap<>();
		request.put("postBody", "access_token=" + accessToken + "&providerId=google.com");
		request.put("requestUri", "http://localhost");
		request.put("returnIdpCredential", true);
		request.put("returnSecureToken", true);

		Map<String, Object> response;
		try {
			response = callFirebaseAuth(firebaseAuthUrlWithProvider, request);
		} catch (RestClientException e) {
			throw new AuthenticationServiceException("Auth with firebase error: " + e.getMessage(), e);
		}

		if (response == null || !response.containsKey(ID_TOKEN)) {
      throw new AuthenticationServiceException(localizedMessageService.getMessage("firebase.invalid_id_token"));
    }

		String idToken = (String) response.get(ID_TOKEN);

		firebaseAuthService.verifyToken(idToken);

		String email = (String) response.get(EMAIL);

		if (response.containsKey("isNewUser") && userRepository.findByEmail(email).isEmpty())
			addUserToDb(email, ROLE_NAME, null);

		if (!response.containsKey("isNewUser") && userRepository.findByEmail(email).isEmpty())
			addUserToDb(email, ROLE_NAME, null);

		Authentication auth = authenticationManager.authenticate(
				new FirebaseAuthenticationToken(idToken)
		);

		CustomUserDetails userAuthenticated = (CustomUserDetails) auth.getPrincipal();

		Jwt jwt = new Jwt(jwtService.generateToken(userAuthenticated));

		UserLoginResponseDTO userResponse = modelMapperUtils.map(userAuthenticated.getUserEntity(), UserLoginResponseDTO.class);

		return new LoginResponseDTO(userResponse, jwt);
	}

	@Override
	public void changePassword(EmailChangePasswordRequestDTO changePasswordRequestDTO) throws FirebaseAuthException {
		String email = changePasswordRequestDTO.getEmail();
		UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
		String newPassword = changePasswordRequestDTO.getNewPassword();

		Map<String, Object> request = new HashMap<>();
		request.put(EMAIL, email);
		request.put("password", changePasswordRequestDTO.getOldPassword());

		try {
			callFirebaseAuth(firebaseAuthUrlWithPassword, request);
		} catch (RestClientException e) {
			throw new AuthenticationServiceException("Auth with firebase error: " + e.getMessage(), e);
		}

        if (!changePasswordRequestDTO.getConfirmPassword().equals(newPassword)) {
            throw new PasswordMismatchException(localizedMessageService.getMessage("user.passwords_not_match"));
        }

		firebaseAuthService.changeUserPassword(email, newPassword);
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}
    
  @Override
  @Transactional
  public boolean disableUser(Long id) {
      // Descomentar Cuando funcione la Autenticacion JWT
      UserEntity userContext=getUserFromContext();
      if(userContext.getId().equals(id)){

          UserEntity user= this.findById(id);

          if(user.getId().equals(id)){
              if(user.isEnabled()){
                  user.setEnabled(false);
                  userRepository.save(user);
              }
              return true;
          }
      }

      throw new UnauthorizedActionException(localizedMessageService.getMessage("user.without_permissions"));
  }

	@Override
	public UserEntity findById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(localizedMessageService.getMessage("user.not_found")));
	}

	public UserEntity getUserFromContext(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationServiceException(localizedMessageService.getMessage("user.not_authenticated"));
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new AuthenticationServiceException(localizedMessageService.getMessage("user.invalid_authentication"));
        }

        return ((CustomUserDetails) authentication.getPrincipal()).getUserEntity();

   }
   @Override
  public void addUserToDb(String email, String roleName, String password) {
    UserEntity newUser = new UserEntity();
    newUser.setEmail(email);
    newUser.setEnabled(true);
    newUser.setIsAccountNotLocked(true);
    newUser.setIsAccountNotExpired(true);
    newUser.setIsAccountNotLocked(true);
    if (password != null) newUser.setPassword(passwordEncoder.encode(password));

    List<Role> roles = new ArrayList<>();

    if (roleName != null) roles.add(roleService.findByName(roleName.toUpperCase()));

    newUser.setRoles(roles);

  UserEntity savedUser = userRepository.save(newUser);

  personService.createPerson(savedUser);
  }
  
  @Override
  public UserEntity createUser(String email, String roleName, String password, Object profile) {
    UserEntity newUser = new UserEntity();
    newUser.setEmail(email);
    newUser.setEnabled(true);
    newUser.setIsAccountNotLocked(true);
    newUser.setIsAccountNotExpired(true);
    newUser.setIsCredentialNotExpired(true);
    
    if (password != null) {
      newUser.setPassword(passwordEncoder.encode(password));
    } else {
      // Para OAuth, generar una contraseña aleatoria
      newUser.setPassword(passwordEncoder.encode("oauth_user_" + System.currentTimeMillis()));
    }

    List<Role> roles = new ArrayList<>();
    if (roleName != null) {
      roles.add(roleService.findByName(roleName.toUpperCase()));
    }
    newUser.setRoles(roles);

    UserEntity savedUser = userRepository.save(newUser);
    personService.createPerson(savedUser);
    
    return savedUser;
  }
  
  @Override
  public Optional<UserEntity> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  protected Map<String, Object> callFirebaseAuth(String url, Map<String, Object> request) {
    return restTemplate.postForObject(url, request, Map.class);
  }
}
