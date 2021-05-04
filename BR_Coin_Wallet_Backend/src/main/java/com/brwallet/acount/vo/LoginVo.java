package com.brwallet.acount.vo;

import lombok.Data;

@Data
public class LoginVo {

	private String userId;       // 사용자 ID
	private String userPassword; // 사용자 비밀번호
	private int otpCode; // 사용자 비밀번호
}
