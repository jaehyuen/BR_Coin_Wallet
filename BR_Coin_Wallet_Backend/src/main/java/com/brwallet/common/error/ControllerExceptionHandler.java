package com.brwallet.common.error;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import com.brwallet.common.Util;
import com.brwallet.common.vo.ResultVo;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ControllerAdvice
public class ControllerExceptionHandler {

	private final Util util;

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<ResultVo<String>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(util.setResult("9999", false, e.getMessage(), null));
	}

	// @Vaild 어노테이션 오류시 리턴
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<ResultVo<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(util.setResult("9999", false, e.getMessage(), null));
	}

	// 디비조회 에러
	@ExceptionHandler(IllegalArgumentException.class)
	protected ResponseEntity<ResultVo<String>> handleIllegalArgumentException(IllegalArgumentException e) {
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(util.setResult("9999", false, e.getMessage(), null));
	}

	// 로그인 에러
	@ExceptionHandler(BadCredentialsException.class)
	protected ResponseEntity<ResultVo<String>> test(BadCredentialsException e) {
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(util.setResult("9999", false, e.getMessage(), null));
	}

	@ExceptionHandler({ NoSuchAlgorithmException.class, NoSuchProviderException.class, IOException.class })
	protected ResponseEntity<ResultVo<String>> test(Exception e) {
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(util.setResult("9999", false, e.getMessage(), null));
	}

	// http 에러
	@ExceptionHandler(HttpClientErrorException.class)
	protected ResponseEntity<ResultVo<String>> handleHttpClientErrorExceptionException(HttpClientErrorException e) {
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
			.body(util.setResult("9999", false, e.getMessage(), null));
	}

}
