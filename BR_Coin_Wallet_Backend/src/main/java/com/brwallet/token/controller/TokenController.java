package com.brwallet.token.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brwallet.common.vo.ResultVo;
import com.brwallet.token.service.TokenService;
import com.brwallet.token.vo.TokenVo;
import com.brwallet.token.vo.TransferVo;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brwallet/token")
@RequiredArgsConstructor
public class TokenController {

	private final TokenService tokenService;

	private Logger             logger = LoggerFactory.getLogger(this.getClass());

	@Operation(summary = "토큰 생성", description = "brcoin 기반 토큰을 생성하는 API")
	@PostMapping
	public ResponseEntity<ResultVo<String>> createToken(@RequestBody @Valid TokenVo tokenVo) {

		logger.debug("************************ [createToken] POST /api/brwallet/token start ************************");
		return ResponseEntity.status(HttpStatus.OK)
			.body(tokenService.createToken(tokenVo));
	}
	
	@Operation(summary = "토큰 송금", description = "brcoin 기반 토큰을 송금하는 API")
	@PostMapping("/transfer")
	public ResponseEntity<ResultVo<String>> transferToken(@RequestBody @Valid TransferVo transferVo) {

		logger.debug("************************ [transferToken] POST /api/brwallet/token/transfer start ************************");
		return ResponseEntity.status(HttpStatus.OK)
			.body(tokenService.transferToken(transferVo));
	}

}
