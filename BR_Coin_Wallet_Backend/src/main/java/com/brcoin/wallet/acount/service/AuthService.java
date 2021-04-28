package com.brcoin.wallet.acount.service;

import java.time.Instant;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brcoin.wallet.acount.entity.UserEntity;
import com.brcoin.wallet.acount.repository.UserRepository;
import com.brcoin.wallet.acount.vo.JwtTokenVo;
import com.brcoin.wallet.acount.vo.LoginVo;
import com.brcoin.wallet.acount.vo.RefreshTokenVo;
import com.brcoin.wallet.acount.vo.RegisterVo;
import com.brcoin.wallet.common.JwtProvider;
import com.brcoin.wallet.common.Util;
import com.brcoin.wallet.common.opt.OtpAuthenticationToken;
import com.brcoin.wallet.vo.ResultVo;
import com.warrenstrange.googleauth.IGoogleAuthenticator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final PasswordEncoder       passwordEncoder;
	private final UserRepository        userRepository;
	private final AuthenticationManager authenticationManager;
	private final IGoogleAuthenticator  googleAuthenticator;
	private final JwtProvider           jwtProvider;
	private final RefreshTokenService   refreshTokenService;
	private final Util                  util;

	private Logger                      logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 회원가입 서비스
	 * 
	 * @param registerDto 회원가입 관련 정보 DTO
	 * 
	 * @return 회원가입 결과 DTO
	 */

	public ResultVo<Object> register(RegisterVo registerVo) {

		try {

			logger.info("this is registerVo : " + registerVo);

			// 유저 정보 엔티티 생성
			UserEntity userEntity = new UserEntity();

			userEntity.setUserName(registerVo.getUserName());
			userEntity.setUserId(registerVo.getUserId());
			userEntity.setUserEmail(registerVo.getUserEmail());
			userEntity.setUserPassword(passwordEncoder.encode(registerVo.getUserPassword()));
			userEntity.setOtpKey(googleAuthenticator.createCredentials()
				.getKey());
			userEntity.setActive(true);

			logger.info("this is userEntity : " + userEntity);

			// 유저 정보 엔티티 저장
			userRepository.save(userEntity);

		} catch (Exception e) {

			logger.error(e.getMessage());
			e.printStackTrace();
			return util.setResult("9999", false, e.getMessage(), null);

		}
		return util.setResult("0000", true, "Success register", null);
	}

	/**
	 * 로그인 서비스
	 * 
	 * @param loginDto 로그인 정보 DTO
	 * 
	 * @return 로그인 결과 DTO
	 */

	public ResultVo<JwtTokenVo> login(LoginVo loginVo) {

		// 로그인 시작 (로그인 실패시 401리턴)
		Authentication authenticate = authenticationManager.authenticate(new OtpAuthenticationToken(loginVo.getUserId(), loginVo.getUserPassword(), loginVo.getOtpCode()));

		SecurityContextHolder.getContext()
			.setAuthentication(authenticate);

		// 정상 로그인시 JWT 토큰 발급
		String     token      = jwtProvider.generateToken(authenticate);

		// 리턴값 생성
		JwtTokenVo jwtTokenVo = JwtTokenVo.builder()
			.accessToken(token)
			.refreshToken(refreshTokenService.generateRefreshToken()
				.getToken())
			.expiresAt(Instant.now()
				.plusMillis(jwtProvider.getJwtExpirationInMillis()))
			.userId(loginVo.getUserId())
			.build();

		return util.setResult("0000", true, "Success login", jwtTokenVo);
	}

	/**
	 * JWT 토큰 재발급 서비스
	 * 
	 * @param refreshTokenDto 토큰 재발급 관련 DTO
	 * 
	 * @return 토근 재발급 결과 DTO
	 */

	@Transactional(readOnly = true)
	public ResultVo<JwtTokenVo> refreshToken(RefreshTokenVo refreshTokenVo) {

		// 리프레쉬 토큰 유효성 검사
		refreshTokenService.validateRefreshToken(refreshTokenVo.getRefreshToken());

		// JWT 토큰 재발급
		String     token      = jwtProvider.generateTokenWithUserName(refreshTokenVo.getUserId());

		// 리턴값 생성
		JwtTokenVo jwtTokenVo = JwtTokenVo.builder()
			.accessToken(token)
			.refreshToken(refreshTokenVo.getRefreshToken())
			.expiresAt(Instant.now()
				.plusMillis(jwtProvider.getJwtExpirationInMillis()))
			.userId(refreshTokenVo.getUserId())
			.build();

		return util.setResult("0000", true, "Success refresh token", jwtTokenVo);
	}

}