package com.techforb.apiportalrecruiting.core.security;

import com.cloudinary.Api.HttpMethod;
import com.techforb.apiportalrecruiting.core.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final AuthenticationManager authenticationManager;
	private final JwtFilter jwtFilter;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http.
				authorizeHttpRequests(request -> request
						.requestMatchers("/swagger-ui/**",
								"/swagger-ui.html",
								"/v3/api-docs/**",
								"/v3/api-docs.yaml",
								"/v3/api-docs/swagger-config",
								"/swagger-resources/**",
								"/webjars/**").permitAll()
						.requestMatchers(new RegexRequestMatcher(".*/auth/.*", null)).permitAll()
						.requestMatchers(new RegexRequestMatcher(".*/(auth|public)/.*", null)).permitAll()
						.requestMatchers(new RegexRequestMatcher(".*/role/self-assign", HttpMethod.POST.name())).hasRole("DEFAULT")
						.requestMatchers(new RegexRequestMatcher(".*/role-functional/.*", HttpMethod.GET.name())).hasRole("APPLICANT")
//                 .requestMatchers(new RegexRequestMatcher("./dev/.", null)).hasRole("DEVELOPER")
//                 .requestMatchers(new RegexRequestMatcher("./admin/.", null)).hasAnyRole("ADMIN", "DEVELOPER")
						.anyRequest().authenticated())
				.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(sessionManager -> sessionManager
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationManager(authenticationManager)
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(customAuthenticationEntryPoint))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		
		// Configuración para desarrollo
		config.setAllowCredentials(false);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
