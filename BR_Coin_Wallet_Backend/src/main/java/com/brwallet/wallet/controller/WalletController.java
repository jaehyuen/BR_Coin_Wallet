package com.brwallet.wallet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brwallet.common.vo.ResultVo;
import com.brwallet.wallet.service.WalletService;
import com.brwallet.wallet.vo.CreateWalletVo;
import com.brwallet.wallet.vo.WalletVo;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brwallet/wallet")
@RequiredArgsConstructor
public class WalletController {

	private final WalletService walletService;

	private Logger              logger = LoggerFactory.getLogger(this.getClass());

	@Operation(summary = "지갑 생성", description = "brcoin 지갑을 생성하는 API")
	@PostMapping()
	public ResponseEntity<ResultVo<String>> createWallet(@RequestBody CreateWalletVo createWalletVo) {

		logger.debug("************************ [createWallet] POST /api/brwallet/wallet start ************************");
		return ResponseEntity.status(HttpStatus.OK)
			.body(walletService.createWallet(createWalletVo));
	}

	@Operation(summary = "지갑 조회", description = "brcoin 지갑을 조회하는 API")
	@GetMapping
	public ResponseEntity<ResultVo<WalletVo>> getWallet(@RequestParam(value = "walletId", required = true) String walletId) {

		logger.debug("************************ [getWallet] GET /api/brwallet/wallet start ************************");
		return ResponseEntity.status(HttpStatus.OK)
			.body(walletService.getWallet(walletId));
	}

}
