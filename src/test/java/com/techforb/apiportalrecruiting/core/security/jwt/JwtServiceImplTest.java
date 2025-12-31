package com.techforb.apiportalrecruiting.core.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;
import java.util.List;
import java.util.Map;

  @ExtendWith(MockitoExtension.class)
    class JwtServiceImplTest {

        @InjectMocks
        private JwtServiceImpl jwtServiceImpl;
        @Mock
        private ObjectMapper objectMapper;
        @Mock
        private LocalizedMessageService localizedMessageService;

      @BeforeEach
      void setUp() {
          jwtServiceImpl.setSecretKey(
                  "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXphYmNkZWY="
          );
      }

    @Test
    void testEncodeAuthData_Success() throws Exception {
        String jwt="jwt-token";
        String email="test@email.com";
        Long userId=1L;
        
         Mockito.when(objectMapper.writeValueAsString(Mockito.any()))
           .thenAnswer(invocation -> {
               Object arg = invocation.getArgument(0);
               return new ObjectMapper().writeValueAsString(arg);
           });

        String encodedData = jwtServiceImpl.encodeAuthData(jwt, email, userId);
        Assertions.assertNotNull(encodedData);
        byte[] decoded = Base64.getDecoder().decode(encodedData);
        String decodedString = new String(decoded);
        ObjectMapper realMapper = new ObjectMapper();
        Map<String, Object> map = realMapper.readValue(decodedString, Map.class);

        Assertions.assertEquals("jwt-token", map.get("jwt"));
        Assertions.assertEquals("test@email.com", map.get("email"));
        Assertions.assertEquals(1, ((Number) map.get("userId")).intValue());
    }
      @Test
    void testEncodeAuthData_Error() throws Exception {
        Mockito.when(objectMapper.writeValueAsString(Mockito.any()))
                .thenThrow(new RuntimeException("json error"));

        Mockito.when(localizedMessageService.getMessage("jwt.encode.error"))
                .thenReturn("jwt.encode.error");

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> jwtServiceImpl.encodeAuthData("jwt", "email", 1L)
        );

        Assertions.assertEquals("jwt.encode.error", ex.getMessage());
    }

    @Test
    void testExtractUsername_Success() {
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        Mockito.when(userDetails.getUsername())
                .thenReturn("test@email.com");
        Mockito.when(userDetails.getAuthorities())
                .thenAnswer(invocation ->
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
        String token = jwtServiceImpl.generateToken(userDetails);
        String extractedUsername = jwtServiceImpl.extractUsername(token);
        Assertions.assertNotNull(extractedUsername);
        Assertions.assertEquals("test@email.com", extractedUsername);
    }

    @Test
    void testGenerateToken_Success() {
         UserDetails userDetails = Mockito.mock(UserDetails.class);

        Mockito.when(userDetails.getUsername())
                .thenReturn("test@email.com");

        Mockito.when(userDetails.getAuthorities())
                .thenAnswer(invocation ->
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        String token = jwtServiceImpl.generateToken(userDetails);

        Assertions.assertNotNull(token);
    }

    @Test
   void testValidateToken() {
         UserDetails userDetails = Mockito.mock(UserDetails.class);

        Mockito.when(userDetails.getUsername())
                .thenReturn("test@email.com");

        Mockito.when(userDetails.getAuthorities())
                .thenAnswer(invocation ->
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        String token = jwtServiceImpl.generateToken(userDetails);

        boolean isValid = jwtServiceImpl.validateToken(token, userDetails);

        Assertions.assertTrue(isValid);
    }
}