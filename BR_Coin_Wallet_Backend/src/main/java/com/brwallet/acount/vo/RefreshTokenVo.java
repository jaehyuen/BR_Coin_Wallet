package com.brwallet.acount.vo;

import lombok.Data;

@Data
public class RefreshTokenVo {

	private String refreshToken; // JWT 토큰 재발급시 필요한 토큰
	private String userId;       // 사용자 ID
}
