package com.techforb.apiportalrecruiting.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

	@Value("${app.url}")
	private String url;

	@Value("${app.organization}")
	private String organization;

	@Value("${app.contact-email}")
	private String contactEmail;

	@Bean
	public OpenAPI openApi(
			@Value("${app.name}") String appName,
			@Value("${app.desc}") String appDescription,
			@Value("${app.version}") String appVersion) {

		Info info = new Info()
				.title(appName)
				.version(appVersion)
				.description(appDescription)
				.contact(new Contact()
						.name(organization)
						.email(contactEmail));

		Server server = new Server()
				.url(url)
				.description(appDescription);

		return new OpenAPI()
				.components(new Components()
						.addSecuritySchemes("Bearer Authentication",
								new SecurityScheme()
										.name("Authorization")
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
				.info(info)
				.addServersItem(server);
	}

	@Bean
	public ModelResolver modelResolver(ObjectMapper objectMapper) {
		return new ModelResolver(objectMapper);
	}
}
