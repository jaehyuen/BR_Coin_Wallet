package com.brwallet.common.email;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class EmailClient {

	private final JavaMailSender sender;

	public void sendEmail(String toAddress, String subject, String body, String key) {

		MimeMessage        message  = sender.createMimeMessage();

		//이미지 세팅
		String             contents = body + "<img src=\"cid:" + key + ".jpg\">";
		FileSystemResource file     = new FileSystemResource(new File(System.getProperty("user.dir") + "/otp/" + key + ".jpg"));


		try {
			// helper 새팅 
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(toAddress);
			helper.setSubject(subject);
			helper.setText(contents, true);
			helper.addInline(key + ".jpg", file);

		} catch (MessagingException e) {
			e.printStackTrace();
		}

		sender.send(message);
	}

}