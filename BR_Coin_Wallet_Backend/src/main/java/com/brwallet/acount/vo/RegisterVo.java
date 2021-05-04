package com.brwallet.acount.vo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class RegisterVo {

	@NotEmpty
	private String userId;       // 사용자 ID

	@NotEmpty
	private String userPassword; // 사용자 비밀번호

	@NotEmpty
	@Email
	private String userEmail;    // 사용자 이메일

	@NotEmpty
	private String userName;     // 사용자 이름
}
