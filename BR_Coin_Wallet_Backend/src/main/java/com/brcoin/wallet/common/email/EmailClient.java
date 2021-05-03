package com.brcoin.wallet.common.email;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
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
public class EmailClient {

	private final IGoogleAuthenticator googleAuthenticator;
	private final JavaMailSender       sender;

	public void testKey() {
		GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
		System.out.println("key is " + key.getKey());

		String qrCodeUrl = GoogleAuthenticatorQRGenerator.getOtpAuthURL("jh", "test", key);
		int    code      = googleAuthenticator.getTotpPassword(key.getKey());

		System.out.println("code : " + code);
		System.out.println("qrCodeUrl : " + qrCodeUrl);
	}

	public void testlogin(LoginVo loginVo) {

//		int code = googleAuthenticator.getTotpPassword(key.getKey());

		boolean test = googleAuthenticator.authorize(loginVo.getUserId(), loginVo.getOtpCode());
		System.out.println(test);

	}

	public void sendEmail(String toAddress, String subject, String body, String key) {

		MimeMessage        message  = sender.createMimeMessage();
		
		

		String             contents = body + "<img src=\"cid:" + key + ".jpg\">";
		FileSystemResource file     = new FileSystemResource(new File(System.getProperty("user.dir") + "/otp/" + key + ".jpg"));


//
		try {
			MimeMessageHelper  helper   = new MimeMessageHelper(message,true, "UTF-8");
			helper.setTo(toAddress);
			helper.setSubject(subject);
//			helper.setText(body);
			helper.setText(contents, true);
			helper.addInline(key + ".jpg", file);

		} catch (MessagingException e) {
			e.printStackTrace();
		}


		sender.send(message);
	}

}