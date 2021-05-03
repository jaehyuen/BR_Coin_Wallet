package com.brcoin.wallet.acount.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brcoin.wallet.acount.service.AuthService;
import com.brcoin.wallet.acount.service.RefreshTokenService;
import com.brcoin.wallet.acount.vo.LoginVo;
import com.brcoin.wallet.acount.vo.RefreshTokenVo;
import com.brcoin.wallet.acount.vo.RegisterVo;
import com.brcoin.wallet.vo.ResultVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wallet/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService         authService;
	private final RefreshTokenService refreshTokenService;
	
	
	
	@Operation(summary = "회원가입", description = "회원가입 API")
	@PostMapping("/register")
	public ResponseEntity<ResultVo> register(@Parameter(description = "회원가입 정보 DTO", required = true) @RequestBody @Valid RegisterVo registerVo) {

		return ResponseEntity.status(HttpStatus.OK).body(authService.register(registerVo));
	}
	
	@Operation(summary = "계정 인증", description = "회원가입을 완료한 계정을 인증하는 API")
	@PostMapping("/otpcheck")
	public ResponseEntity<ResultVo> optCheck(@Parameter(required = true) @RequestBody LoginVo loginVo) {
		
		System.out.println("test");
		return ResponseEntity.status(HttpStatus.OK).body(authService.optCheck(loginVo));
	}

	@Operation(summary = "로그인", description = "로그인 API")
	@PostMapping("/login")
	public ResponseEntity<ResultVo> login(@Parameter(required = true) @RequestBody LoginVo loginVo) {
		
		System.out.println("test");
		return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginVo));
	}

	@Operation(summary = "JWT 토큰 재발급", description = "JWT 토큰 재발급 API")
	@PostMapping("/refresh")
	public ResponseEntity<ResultVo> refreshToken(@Parameter(description = "리프레시 토큰 정보 DTO", required = true) @RequestBody RefreshTokenVo refreshTokenVo) {

		return ResponseEntity.status(HttpStatus.OK).body(authService.refreshToken(refreshTokenVo));

	}

	@Operation(summary = "로그아웃", description = "로그아웃 API")
	@PostMapping("/logout")
	public ResponseEntity<ResultVo> logout(@Parameter(description = "리프레시 토큰 정보 DTO", required = true) @RequestBody RefreshTokenVo refreshTokenVo) {

		return ResponseEntity.status(HttpStatus.OK).body(refreshTokenService.deleteRefreshToken(refreshTokenVo.getRefreshToken()));
	}
	
//	@PostMapping("/test")
//	public ResponseEntity<String> saveLocation() {
//
//		emailService.sendEmail("violet65206520@gmail.com", "스프링을 이용한 메일 전송", "되는지 테스트 하는 거예요");
//
//		return ResponseEntity.status(HttpStatus.OK)
//			.body("");
//	}
//	
//	@GetMapping(value = "/test",produces = MediaType.IMAGE_JPEG_VALUE)
//	public @ResponseBody byte[] imgetest()throws IOException{
//
//		emailService.sendEmail("violet65206520@gmail.com", "스프링을 이용한 메일 전송", "되는지 테스트 하는 거예요");
//		InputStream in  =getClass().getResourceAsStream("/");
//		return IOUtils.
//
//	}
	 


}


