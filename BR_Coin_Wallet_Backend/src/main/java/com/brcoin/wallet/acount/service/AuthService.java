package com.brcoin.wallet.acount.service;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brcoin.wallet.acount.entity.OtpEntity;
import com.brcoin.wallet.acount.entity.UserEntity;
import com.brcoin.wallet.acount.repository.OtpRepository;
import com.brcoin.wallet.acount.repository.UserRepository;
import com.brcoin.wallet.acount.vo.JwtTokenVo;
import com.brcoin.wallet.acount.vo.LoginVo;
import com.brcoin.wallet.acount.vo.RefreshTokenVo;
import com.brcoin.wallet.acount.vo.RegisterVo;
import com.brcoin.wallet.common.JwtProvider;
import com.brcoin.wallet.common.Util;
import com.brcoin.wallet.common.email.EmailClient;
import com.brcoin.wallet.common.opt.OtpAuthenticationToken;
import com.brcoin.wallet.common.opt.OtpClient;
import com.brcoin.wallet.vo.ResultVo;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final PasswordEncoder       passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider           jwtProvider;
	private final RefreshTokenService   refreshTokenService;
	private final Util                  util;

	private final UserRepository        userRepository;
	private final OtpRepository         otpRepository;

	private Logger                      logger = LoggerFactory.getLogger(this.getClass());

	private final EmailClient           emailClient;
	private final OtpClient             otpClient;

	@Value("${email.message}")
	private String                      mailMessage;

	@Value("${email.subject}")
	private String                      mailSubject;

	/**
	 * 회원가입 서비스
	 * 
	 * @param registerDto 회원가입 관련 정보 DTO
	 * 
	 * @return 회원가입 결과 DTO
	 */

	public ResultVo<Object> register(RegisterVo registerVo) {

		try {

			GoogleAuthenticatorKey key   = otpClient.createKey();
			String                 qrUrl = otpClient.createQrUrl(registerVo.getUserName(), "test", key);

			// qr코드 이미지 저장
			util.saveImage(qrUrl, key.getKey());

			// otp 정보 생성
			OtpEntity otpEntity = new OtpEntity();
			otpEntity.setOptKey(key.getKey());
			otpEntity.setOtpUrl(qrUrl);
			otpEntity.setOtpFilePath(System.getProperty("user.dir") + "/otp/" + key.getKey() + ".jpg");

			otpRepository.save(otpEntity);

			// 유저 정보 엔티티 생성
			UserEntity userEntity = new UserEntity();

			userEntity.setUserName(registerVo.getUserName());
			userEntity.setUserId(registerVo.getUserId());
			userEntity.setUserEmail(registerVo.getUserEmail());
			userEntity.setUserPassword(passwordEncoder.encode(registerVo.getUserPassword()));
			userEntity.setOtpEntity(otpEntity);
			userEntity.setActive(false);

			// 유저 정보 엔티티 저장
			userRepository.save(userEntity);

			String str = "<h3>" + registerVo.getUserName() + "님 회원가입을 환영합니다.</h3><br>아래 설명을 따라서 OTP 등록을 진행해 주시기 바랍니다.<br><br>";

			emailClient.sendEmail(registerVo.getUserEmail(), mailSubject, str + mailMessage, key.getKey());

		} catch (Exception e) {

			logger.error(e.getMessage());
			e.printStackTrace();
			return util.setResult("9999", false, e.getMessage(), null);

		}
		return util.setResult("0000", true, "Success register ", null);
	}

	/**
	 * OTP 인증 서비스(계정 인증)
	 * 
	 * @param loginVo 로그인 관련 정보 DTO
	 * 
	 * @return
	 * 
	 */
	
	public ResultVo<Object> active(LoginVo loginVo) {

		UserEntity user = userRepository.findByUserId(loginVo.getUserId())
			.orElseThrow(IllegalArgumentException::new);

		if (!passwordEncoder.matches(loginVo.getUserPassword(), user.getUserPassword())) {
			return util.setResult("9999", false, "fail login", null);
		}

		//오티피 정보 확인시 계정 활성화
		if (otpClient.otpAuthorize(user.getOtpEntity()
			.getOptKey(), loginVo.getOtpCode())) {
			user.setActive(true);
			return util.setResult("0000", true, "Success login", null);
		} else {
			return util.setResult("9999", false, "fail login", null);
		}

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