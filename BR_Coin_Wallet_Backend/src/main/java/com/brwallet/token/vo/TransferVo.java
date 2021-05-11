package com.brwallet.token.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;

@Data
public class TransferVo {

	@NotBlank
	private String fromAddr;   // 보내는 지갑주소
	@NotBlank
	private String toAddr;     // 받는 지갑주소
	@NotBlank
//	@Pattern(regexp = "^\\d(.\\d+)?+$")
	private String amount;     // 보내는 량
	@NotBlank
	@Pattern(regexp = "^[0-9]+$")
	private String tokenId;    // 토큰 아이디
	@NotBlank
	private String unlockDate; // 거래정지 날짜 (unix timestamp)

	private String signature;  // 서명 데이터값 (base64 인코딩)

}
