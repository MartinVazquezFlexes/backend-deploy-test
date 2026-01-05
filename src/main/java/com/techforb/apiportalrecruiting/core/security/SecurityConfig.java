package com.techforb.apiportalrecruiting.core.security;

import com.cloudinary.Api.HttpMethod;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final AuthenticationManager authenticationManager;
	private final JwtFilter jwtFilter;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.
				authorizeHttpRequests(request -> request
						.requestMatchers(
								"/health",
								"/swagger-ui/**",
								"/swagger-ui.html",
								"/v3/api-docs/**",
								"/v3/api-docs.yaml",
								"/v3/api-docs/swagger-config",
								"/swagger-resources/**",
								"/webjars/**").permitAll()
						.requestMatchers(new RegexRequestMatcher(".*/auth/.*", null)).permitAll()
						.requestMatchers(new RegexRequestMatcher(".*/(auth|public)/.*", null)).permitAll()
						.requestMatchers(new RegexRequestMatcher(".*/role/self-assign", HttpMethod.POST.name())).hasRole("APPLICANT")
						.requestMatchers(new RegexRequestMatcher(".*/role-functional/.*", HttpMethod.GET.name())).hasRole("APPLICANT")
						.anyRequest().authenticated())
				// NOSONAR - CSRF protection intentionally disabled for stateless JWT API
				.csrf(csrf -> csrf.disable())

				.cors(cors -> cors.configurationSource(corsConfigurationSource()))

				// Stateless session management required for JWT-based authentication
				.sessionManagement(sessionManager ->
						sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)

				.authenticationManager(authenticationManager)

				.exceptionHandling(exceptions ->
						exceptions.authenticationEntryPoint(customAuthenticationEntryPoint)
				)

				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}


	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		//explicitly allowed origins
		config.setAllowCredentials(false);
		config.setAllowedOrigins(List.of(
				"http://localhost:4200",
				"https://frontend-deploy-test-gilt.vercel.app/"
		));

		config.setAllowedHeaders(List.of("*"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
