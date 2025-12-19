# Patrón Strategy para Autenticación

## 🎯 Descripción General

El sistema de autenticación utiliza el **Patrón Strategy** para manejar múltiples métodos de autenticación (Email, Google, LinkedIn) de manera flexible y extensible. Cada proveedor tiene su propia interfaz específica para mejor type safety.

## 🏗️ ¿Qué es el Patrón Strategy?

El **Patrón Strategy** permite definir una familia de algoritmos, encapsular cada uno y hacerlos intercambiables. En nuestro caso, cada método de autenticación es una estrategia diferente.

### Ventajas:
- ✅ **Extensibilidad**: Fácil agregar nuevos proveedores
- ✅ **Type Safety**: Cada estrategia tiene su propio tipo de request
- ✅ **Mantenibilidad**: Cada estrategia está aislada e independiente
- ✅ **Testabilidad**: Cada estrategia puede ser probada por separado
- ✅ **Inversión de Dependencias**: El controlador depende de la interfaz, no de la implementación
- ✅ **Flexibilidad**: Permite cambiar implementaciones sin modificar el código cliente

## 🔧 Implementación

### Interfaces Específicas

```java
// Email Authentication
public interface EmailAuthenticationStrategy {
    void register(EmailLoginRequestDTO request) throws FirebaseAuthException;
    LoginResponseDTO login(EmailLoginRequestDTO request) throws FirebaseAuthException;
    String getStrategyType();
}

// Google Authentication
public interface GoogleAuthenticationStrategy {
    void register(GoogleLoginRequestDTO request) throws FirebaseAuthException;
    LoginResponseDTO login(GoogleLoginRequestDTO request) throws FirebaseAuthException;
    String getStrategyType();
}

// LinkedIn Authentication
public interface LinkedInAuthenticationStrategy {
    void register(LinkedInCallbackRequestDTO request) throws FirebaseAuthException;
    LoginResponseDTO login(LinkedInCallbackRequestDTO request) throws FirebaseAuthException;
    String getStrategyType();
}
```

### Interfaz AuthenticationContextService

```java
public interface AuthenticationContextService {
    void register(String strategyType, Object request) throws FirebaseAuthException;
    LoginResponseDTO login(String strategyType, Object request) throws FirebaseAuthException;
}
```

### Implementación AuthenticationContext

```java
@Component
@RequiredArgsConstructor
public class AuthenticationContext implements AuthenticationContextService {
    private final EmailAuthenticationStrategy emailStrategy;
    private final GoogleAuthenticationStrategy googleStrategy;
    private final LinkedInAuthenticationStrategy linkedInStrategy;
    
    @Override
    public LoginResponseDTO login(String strategyType, Object request) throws FirebaseAuthException {
        switch (strategyType.toUpperCase()) {
            case "EMAIL":
                if (request instanceof EmailLoginRequestDTO) {
                    return emailStrategy.login((EmailLoginRequestDTO) request);
                }
                throw new IllegalArgumentException("Request debe ser de tipo EmailLoginRequestDTO");
            case "GOOGLE":
                if (request instanceof GoogleLoginRequestDTO) {
                    return googleStrategy.login((GoogleLoginRequestDTO) request);
                }
                throw new IllegalArgumentException("Request debe ser de tipo GoogleLoginRequestDTO");
            case "LINKEDIN":
                if (request instanceof LinkedInCallbackRequestDTO) {
                    return linkedInStrategy.login((LinkedInCallbackRequestDTO) request);
                }
                throw new IllegalArgumentException("Request debe ser de tipo LinkedInCallbackRequestDTO");
            default:
                throw new IllegalArgumentException("Estrategia no soportada: " + strategyType);
        }
    }
}
```

### Uso en el Controlador

```java
@RestController
public class UserController {
    private final AuthenticationContextService authenticationContext;
    
    @PostMapping("/auth/email-login")
    public ResponseEntity<LoginResponseDTO> emailLogin(@RequestBody EmailLoginRequestDTO request) {
        LoginResponseDTO response = authenticationContext.login("EMAIL", request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/auth/google-login")
    public ResponseEntity<LoginResponseDTO> googleLogin(@RequestBody GoogleLoginRequestDTO request) {
        LoginResponseDTO response = authenticationContext.login("GOOGLE", request);
        return ResponseEntity.ok(response);
    }
}
```

## 🔐 Estrategias Disponibles

### 1. Email Authentication
- **Archivo**: `EmailAuthenticationStrategyImpl.java`
- **Funcionalidad**: Autenticación tradicional con email y contraseña
- **Flujo**: Valida credenciales con Firebase → Busca/Crea usuario → Genera JWT

### 2. Google Authentication
- **Archivo**: `GoogleAuthenticationStrategyImpl.java`
- **Funcionalidad**: Autenticación OAuth 2.0 con Google
- **Flujo**: Valida token con Firebase → Obtiene datos de Google → Busca/Crea usuario → Genera JWT

### 3. LinkedIn Authentication
- **Archivo**: `LinkedInAuthenticationStrategyImpl.java`
- **Funcionalidad**: Autenticación OAuth 2.0 con LinkedIn
- **Flujo**: Intercambia código por token → Obtiene perfil → Busca/Crea usuario → Genera JWT

## 🚀 Cómo Agregar una Nueva Estrategia

### Paso 1: Crear Interfaz Específica
```java
public interface TuEstrategiaAuthenticationStrategy {
    void register(TuRequestDTO request) throws FirebaseAuthException;
    LoginResponseDTO login(TuRequestDTO request) throws FirebaseAuthException;
    String getStrategyType();
}
```

### Paso 2: Crear Implementación
```java
@Component("TU_ESTRATEGIA")
@RequiredArgsConstructor
public class TuEstrategiaAuthenticationStrategyImpl implements TuEstrategiaAuthenticationStrategy {
    // Implementar métodos específicos
}
```

### Paso 3: Actualizar AuthenticationContext
```java
// Agregar nueva estrategia al constructor y switch
case "TU_ESTRATEGIA":
    if (request instanceof TuRequestDTO) {
        return tuEstrategiaStrategy.login((TuRequestDTO) request);
    }
```

### Paso 4: Agregar Endpoint
```java
@PostMapping("/auth/tu-estrategia-login")
public ResponseEntity<LoginResponseDTO> tuEstrategiaLogin(@RequestBody TuRequestDTO request) {
    return ResponseEntity.ok(authenticationContext.login("TU_ESTRATEGIA", request));
}
```

