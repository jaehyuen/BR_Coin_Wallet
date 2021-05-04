package com.brwallet.common.opt;

import org.springframework.stereotype.Component;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import com.warrenstrange.googleauth.IGoogleAuthenticator;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class OtpClient {

	private final IGoogleAuthenticator googleAuthenticator;

	public GoogleAuthenticatorKey createKey() {
		return googleAuthenticator.createCredentials();
	}

	public String createQrUrl(String name, String body, GoogleAuthenticatorKey key) {
		return GoogleAuthenticatorQRGenerator.getOtpAuthURL(name, body, key);
	}

	public boolean otpAuthorize(String key, int code) {

		return googleAuthenticator.authorize(key, code);

	}

}