package com.techforb.apiportalrecruiting.core.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.core.config.LocalizedMessageService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService{

    private String secretKey;

    @Value("${SECRET_KEY}")
    public void setSecretKey(String key) {
        this.secretKey = key;
    }

    private static final long TOKEN_TIME = 1000 * 60 * 60;
    private static final long TEMP_TOKEN_TIME = 1000 * 60 * 5; // 5 minutos
    
    private final ObjectMapper objectMapper;
    private final LocalizedMessageService localizedMessageService;
    
    public JwtServiceImpl(ObjectMapper objectMapper, LocalizedMessageService localizedMessageService) {
        this.objectMapper = objectMapper;
        this.localizedMessageService = localizedMessageService;
    }

    public String generateToken(UserDetails user){
        Map<String, Object> claims = new HashMap<>();
        var roles = user.getAuthorities().stream().toList();
        claims.put("roles", roles);

        return createToken(claims, user);
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    
    @Override
    public String encodeAuthData(String jwt, String email, Long userId) {
        try {
            Map<String, Object> authData = new HashMap<>();
            authData.put("jwt", jwt);
            authData.put("email", email);
            authData.put("userId", userId);
            authData.put("timestamp", System.currentTimeMillis());
            authData.put("expiresAt", System.currentTimeMillis() + TEMP_TOKEN_TIME); // 5 minutos
            
            String jsonData = objectMapper.writeValueAsString(authData);
            return Base64.getEncoder().encodeToString(jsonData.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(localizedMessageService.getMessage("jwt.encode.error"), e);
        }
    }
    


    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim (String token, Function<Claims, T> claimResolver){
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String createToken(Map<String, Object> claims, UserDetails userDetails){
        return Jwts
                .builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TOKEN_TIME))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey(){
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
