package com.techforb.apiportalrecruiting.modules.backoffice.user;

import java.util.Optional;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.EmailChangePasswordRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.EmailLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.GoogleLoginRequestDTO;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.LoginResponseDTO;

public interface UserService {

    void emailRegister (EmailLoginRequestDTO newUser) throws FirebaseAuthException;
  
    LoginResponseDTO emailLogin(EmailLoginRequestDTO user) throws FirebaseAuthException;
  
    LoginResponseDTO googleLogin(GoogleLoginRequestDTO accessToken) throws FirebaseAuthException;
  
    void changePassword (EmailChangePasswordRequestDTO changePasswordRequestDTO) throws FirebaseAuthException;
  
    boolean disableUser(Long id);
  
    UserEntity findById(Long id);
  
    UserEntity getUserFromContext();

    UserEntity createUser(String email, String roleName, String password, Object profile);

    Optional<UserEntity> findByEmail(String email);

    void addUserToDb(String email, String roleName, String password);
  
}
