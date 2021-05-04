package com.brcoin.wallet.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.brcoin.wallet.common.Util;
import com.brcoin.wallet.vo.ResultVo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ControllerAdvice
public class ControllerExceptionHandler {

	private final Util util;

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<ResultVo> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {

		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(util.setResult("9999", false, e.getMessage(), null));
	}

	// @Vaild 어노테이션 오류시 리턴
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ResultVo> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(util.setResult("9999", false, e.getMessage(), null));
	}

	// 디비조회 에러
	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<ResultVo> handleIllegalArgumentException(IllegalArgumentException e) {

		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(util.setResult("9999", false, e.getMessage(), null));
	}

	// 로그인 에러
	@ExceptionHandler(BadCredentialsException.class)
	protected ResponseEntity<ResultVo> test(BadCredentialsException e) {

		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(util.setResult("9999", false, e.getMessage(), null));
	}
}
