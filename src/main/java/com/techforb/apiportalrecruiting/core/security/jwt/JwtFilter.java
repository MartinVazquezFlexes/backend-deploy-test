package com.techforb.apiportalrecruiting.core.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techforb.apiportalrecruiting.modules.backoffice.user.CustomUserDetails;
import com.techforb.apiportalrecruiting.modules.backoffice.user.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Permitir peticiones OPTIONS (preflight CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String token = getToken(request);

            if(token == null){
                filterChain.doFilter(request,response);
                return;
            }

            String username = null;

            username = jwtService.extractUsername(token);

            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                CustomUserDetails userDetails = (CustomUserDetails) userDetailsServiceImpl.loadUserByUsername(username);

                if(jwtService.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null,userDetails.getAuthorities()
                    );

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }

                filterChain.doFilter(request,response);
            } else {
                filterChain.doFilter(request,response);
            }
        } catch (ServletException | IOException ex){
            setErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response, ex.getClass().getSimpleName(), ex.getMessage());
        } catch (JwtException ex) {
            setErrorResponse(HttpServletResponse.SC_BAD_REQUEST, response,ex.getClass().getSimpleName(), ex.getMessage());
        }

    }

    private void setErrorResponse(int status, HttpServletResponse response,String error , String message) throws IOException {
        Map<String, String> errorsDetail = new HashMap<>();
        errorsDetail.put("error", error);
        errorsDetail.put("message", message);

        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorsDetail));
    }

    private String getToken(HttpServletRequest request){
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(token) && token.startsWith("Bearer ")) return token.substring(7);
        return null;
    }
}
