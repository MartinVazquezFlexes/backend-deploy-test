package com.techforb.apiportalrecruiting.core.services;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.entities.UserEntity;
import com.techforb.apiportalrecruiting.core.dtos.users.EmailChangePasswordRequestDTO;
import com.techforb.apiportalrecruiting.core.dtos.users.EmailLoginRequestDTO;
import com.techforb.apiportalrecruiting.core.dtos.users.GoogleLoginRequestDTO;
import com.techforb.apiportalrecruiting.core.dtos.users.LoginResponseDTO;

import java.util.Optional;

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
