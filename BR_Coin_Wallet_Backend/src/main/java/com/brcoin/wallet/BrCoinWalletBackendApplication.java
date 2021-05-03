package com.brcoin.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

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

}
