package com.brwallet.wallet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brwallet.wallet.service.WalletService;
import com.brwallet.wallet.vo.CreateWalletVo;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brwallet/wallet")
@RequiredArgsConstructor
public class WalletController {

	private final WalletService walletService;

	@Operation(summary = "지갑 생성", description = "회원가입 API")
	@PostMapping()
	public ResponseEntity<String> createWallet(@RequestBody CreateWalletVo createWalletVo) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(walletService.test());
	}

	@Operation(summary = "지갑 조회", description = "brcoin 지갑을 조회하는 API")
	@GetMapping
	public ResponseEntity<String> getWallet(@RequestParam(value = "walletId", required = true) String walletId) {
		walletService.getWallet(walletId);
		
		return ResponseEntity.status(HttpStatus.OK)
			.body("");
	}

}
