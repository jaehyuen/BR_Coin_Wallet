package com.brwallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.IGoogleAuthenticator;

@EnableJpaAuditing
@SpringBootApplication
public class BrCoinWalletBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrCoinWalletBackendApplication.class, args);
	}

	@Bean
	IGoogleAuthenticator googleAuthenticator() {
		return new GoogleAuthenticator();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
