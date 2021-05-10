package com.brwallet.acount.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brwallet.acount.service.AuthService;
import com.brwallet.acount.service.RefreshTokenService;
import com.brwallet.acount.vo.JwtTokenVo;
import com.brwallet.acount.vo.LoginVo;
import com.brwallet.acount.vo.RefreshTokenVo;
import com.brwallet.acount.vo.RegisterVo;
import com.brwallet.common.vo.ResultVo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brwallet/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService         authService;
	private final RefreshTokenService refreshTokenService;

	private Logger                    logger = LoggerFactory.getLogger(this.getClass());

	// 회원가입 완료이후 opt 계정인증을 해야됨
	@Operation(summary = "회원가입", description = "회원가입 API")
	@PostMapping("/register")
	public ResponseEntity<ResultVo<String>> register(@Parameter(description = "회원가입 정보 DTO", required = true) @RequestBody @Valid RegisterVo registerVo) {

		logger.debug("************************ [register] POST /api/brwallet/auth/register start ************************");
		return ResponseEntity.status(HttpStatus.OK)
			.body(authService.register(registerVo));
	}

	@Operation(summary = "계정 활성화", description = "회원가입을 완료한 계정을 활성화 하는 API")
	@PostMapping("active")
	public ResponseEntity<ResultVo<String>> active(@Parameter(required = true) @RequestBody LoginVo loginVo) {

		logger.debug("************************ [active] POST /api/brwallet/auth/active start ************************");
		return ResponseEntity.status(HttpStatus.OK)
			.body(authService.active(loginVo));
	}

	@Operation(summary = "로그인", description = "로그인 API")
	@PostMapping("/login")
	public ResponseEntity<ResultVo<JwtTokenVo>> login(@Parameter(required = true) @RequestBody LoginVo loginVo) {

		logger.debug("************************ [login] POST /api/brwallet/auth/login start ************************");
		return ResponseEntity.status(HttpStatus.OK)
			.body(authService.login(loginVo));
	}

	@Operation(summary = "JWT 토큰 재발급", description = "JWT 토큰 재발급 API")
	@PostMapping("/refresh")
	public ResponseEntity<ResultVo<JwtTokenVo>> refreshToken(@Parameter(description = "리프레시 토큰 정보 DTO", required = true) @RequestBody RefreshTokenVo refreshTokenVo) {

		logger.debug("************************ [refresh] POST /api/brwallet/auth/refresh start ************************");
		return ResponseEntity.status(HttpStatus.OK)
			.body(authService.refreshToken(refreshTokenVo));

	}

	@Operation(summary = "로그아웃", description = "로그아웃 API")
	@PostMapping("/logout")
	public ResponseEntity<ResultVo> logout(@Parameter(description = "리프레시 토큰 정보 DTO", required = true) @RequestBody RefreshTokenVo refreshTokenVo) {

		logger.debug("************************ [logout] POST /api/brwallet/auth/logout start ************************");
		return ResponseEntity.status(HttpStatus.OK)
			.body(refreshTokenService.deleteRefreshToken(refreshTokenVo.getRefreshToken()));
	}

}
