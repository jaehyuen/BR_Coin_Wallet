package com.brwallet.wallet.vo;

import lombok.Data;

@Data
public class CreateWalletVo {

	private String  jwtToken;  // JWT 토큰
	private boolean privKeyYn;
	

}
