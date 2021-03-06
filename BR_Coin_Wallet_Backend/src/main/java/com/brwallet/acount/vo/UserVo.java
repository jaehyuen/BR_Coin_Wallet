package com.brwallet.acount.vo;

import lombok.Data;

@Data
public class UserVo {

	private Long    id;           // ID 값
	private String  userId;       // 사용자 ID
	private String  userPassword; // 사용자 비밀번호
	private String  userEmail;    // 사용자 이메일
	private String  userName;     // 사용자 이름
	private String  otpKey;       // opt 키 
	private boolean active;       // 계정 활성화 상태

}
