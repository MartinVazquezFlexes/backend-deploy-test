package com.techforb.apiportalrecruiting.modules.backoffice.user;

import com.google.firebase.auth.FirebaseAuthException;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtService;
import com.techforb.apiportalrecruiting.core.security.linkedin.LinkedInService;
import com.techforb.apiportalrecruiting.modules.backoffice.user.dtos.*;
import com.techforb.apiportalrecruiting.modules.backoffice.user.strategies.AuthenticationContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Autenticacion", description = "Endpoints para autenticación y registro de usuarios")
public class UserController {
        private final UserService userService;
        private final AuthenticationContextService authenticationContext;
        private final LinkedInService linkedInService;
        private final JwtService jwtService;
        private final LocalizedMessageService localizedMessageService;
        @Value("${front.end-url}")
        private String FRONTEND_URL;

        @Operation(summary = "Registro de usuario por email", description = "Registra un usuario con email y contraseña mediante Firebase Authentication.", responses = {
                        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
        })
        @PostMapping("/auth/email-register")
        public ResponseEntity<?> emailRegister(@RequestBody @Valid EmailLoginRequestDTO userRequest)
                        throws FirebaseAuthException {
                authenticationContext.register("EMAIL", userRequest);
                return ResponseEntity.status(201).build();
        }

        @Operation(summary = "Inicio de sesión con email", description = "Inicia sesión con email y contraseña y devuelve un token JWT.", responses = {
                        @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
        })
        @PostMapping("/auth/email-login")
        public ResponseEntity<LoginResponseDTO> emailLogin(@RequestBody @Valid EmailLoginRequestDTO loginRequest)
                        throws FirebaseAuthException {
                LoginResponseDTO response = authenticationContext.login("EMAIL", loginRequest);
                return ResponseEntity.status(200).body(response);
        }

        @Operation(summary = "Inicio de sesión con Google", description = "Autentica a un usuario con Google OAuth y devuelve un token JWT.", responses = {
                        @ApiResponse(responseCode = "200", description = "Autenticación exitosa", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Token inválido o credenciales incorrectas", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
        })
        @PostMapping("/auth/google-login")
        public ResponseEntity<LoginResponseDTO> googleLogin(@RequestBody @Valid GoogleLoginRequestDTO googleToken)
                        throws FirebaseAuthException {
                LoginResponseDTO response = authenticationContext.login("GOOGLE", googleToken);
                return ResponseEntity.status(200).body(response);
        }

        @Operation(summary = "Obtener URL de autorización de LinkedIn", description = "Retorna la URL para iniciar el flujo de autorización de LinkedIn OAuth.", responses = {
                        @ApiResponse(responseCode = "200", description = "URL de autorización generada exitosamente"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
        })
        @PostMapping("/auth/linkedin/authorize")
        public ResponseEntity<String> getLinkedInAuthorizationUrl() {
                String state = UUID.randomUUID().toString();
                String authUrl = linkedInService.getAuthorizationUrl(state);
                System.out.println("Generated LinkedIn Auth URL: " + authUrl);
                return ResponseEntity.status(200).body(authUrl);
        }

        @Operation(summary = "Callback de LinkedIn OAuth", description = "Maneja el callback de LinkedIn después de la autorización y redirige al frontend con datos codificados.", responses = {
                        @ApiResponse(responseCode = "302", description = "Redirección al frontend con datos de autenticación"),
                        @ApiResponse(responseCode = "400", description = "Error en la autorización", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
        })
        @GetMapping("/auth/linkedin/callback")
        public void linkedInCallback(@RequestParam(required = false) String code,
                        @RequestParam(required = false) String state, HttpServletResponse response)
                        throws FirebaseAuthException, IOException {
                if (code == null || state == null) {
                        response.sendError(HttpStatus.BAD_REQUEST.value(),
                                        localizedMessageService.getMessage("auth.linkedin.code_required"));
                        return;
                }

                try {
                        LinkedInCallbackRequestDTO callbackRequest = new LinkedInCallbackRequestDTO(code, state);
                        LoginResponseDTO loginResponse = authenticationContext.login("LINKEDIN", callbackRequest);

                        String encodedData = jwtService.encodeAuthData(
                                        loginResponse.getJwt().token(),
                                        loginResponse.getUser().getEmail(),
                                        loginResponse.getUser().getId());

                        String redirectUrl = FRONTEND_URL + "/#" + encodedData;

                        response.sendRedirect(redirectUrl);

                } catch (Exception e) {
                        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                        localizedMessageService.getMessage("auth.linkedin.processing_error"));
                }
        }

        @Operation(summary = "Cambio de contraseña", description = "Permite a un usuario cambiar su contraseña mediante su correo electrónico.", responses = {
                        @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
        })
        @PostMapping("/change-password")
        public ResponseEntity<?> emailChangePassword(
                        @RequestBody @Valid EmailChangePasswordRequestDTO emailChangePasswordRequestDTO)
                        throws FirebaseAuthException {
                userService.changePassword(emailChangePasswordRequestDTO);
                return ResponseEntity.status(200).build();
        }

        @Operation(summary = "Deshabilitar usuario", description = "Solo El usuario propietario puede desabilitar su cuenta, debe estar logueado", responses = {
                        @ApiResponse(responseCode = "200", description = "Usuario deshabilitado exitosamente"),
                        @ApiResponse(responseCode = "403", description = "El usuario logueado es distinto al que esta intentando deshabilitar", content = @Content),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
        })
        @PatchMapping("/user-disable/{id}")
        public ResponseEntity<?> disableUser(@PathVariable Long id) {
                return ResponseEntity.status(200).body(userService.disableUser(id));
        }
}