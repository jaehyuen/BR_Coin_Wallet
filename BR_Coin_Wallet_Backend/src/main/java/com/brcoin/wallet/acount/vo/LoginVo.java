package com.brcoin.wallet.acount.vo;

import lombok.Data;

@Data
public class LoginVo {

	private String userId;       // 사용자 ID
	private String userPassword; // 사용자 비밀번호
}