package com.brcoin.wallet.acount.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brcoin.wallet.acount.vo.LoginVo;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import com.warrenstrange.googleauth.IGoogleAuthenticator;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class OtpService {

	
	private final IGoogleAuthenticator googleAuthenticator;

	public void testKey() {
		GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
		System.out.println("key is "+key.getKey());
		
		String qrCodeUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("jh", "test", key);
		int code = googleAuthenticator.getTotpPassword(key.getKey());
		
		System.out.println("code : "+code);
		System.out.println("qrCodeUrl : "+qrCodeUrl);
	}
	public void testlogin(LoginVo loginVo) {
		
//		int code = googleAuthenticator.getTotpPassword(key.getKey());
		boolean test=googleAuthenticator.authorize(loginVo.getUserId(), loginVo.getOtpCode());
		System.out.println(test);
		
	}

}