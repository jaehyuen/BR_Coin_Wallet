package com.brwallet.token.vo;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TokenVo {

	@NotBlank
	private String          owner;       // 토큰 주인 주소
	@NotBlank
	private String          symbol;      // 토큰 심볼
	@NotBlank
	private String          totalSupply; // 토큰 총 공급량
	@NotBlank
	private String          name;        // 토큰 이름
	@NotNull
	private String          information; // 토큰 정보
	@NotNull
	private String          url;         // 토큰 관련 url
	@Max(value = 8)
	private int             decimal;     // 토큰 소수점 자리수 최대 8

	private List<ReserveVo> reserve;     // 최초 분배 관련

}
