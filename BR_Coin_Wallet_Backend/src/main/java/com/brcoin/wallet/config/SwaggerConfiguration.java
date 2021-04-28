package com.brcoin.wallet.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfiguration {
	@Bean
	public OpenAPI springShopOpenAPI() {

		SecurityScheme      securityScheme    = new SecurityScheme().type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.name("Authorization");
		
		SecurityRequirement schemaRequirement = new SecurityRequirement().addList("JWT Auth");

		return new OpenAPI().components(new Components().addSecuritySchemes("JWT Auth", securityScheme))
			.security(Arrays.asList(schemaRequirement))
			.info(new Info().title("BR_Coin_Wallet_Backend API")
				.description("brcoin wallet application")
				.version("v1.0.0")
				.license(new License().name("Apache 2.0")
					.url("http://springdoc.org")))
			.externalDocs(new ExternalDocumentation().description("git")
				.url("https://github.com/jaehyuen/BR_Coin_Wallet"));
	}
}
