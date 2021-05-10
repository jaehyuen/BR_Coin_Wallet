package com.brwallet.acount.service;

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

import com.brwallet.acount.entity.OtpEntity;
import com.brwallet.acount.entity.UserEntity;
import com.brwallet.acount.repository.OtpRepository;
import com.brwallet.acount.repository.UserRepository;
import com.brwallet.acount.vo.JwtTokenVo;
import com.brwallet.acount.vo.LoginVo;
import com.brwallet.acount.vo.RefreshTokenVo;
import com.brwallet.acount.vo.RegisterVo;
import com.brwallet.common.JwtProvider;
import com.brwallet.common.Util;
import com.brwallet.common.email.EmailClient;
import com.brwallet.common.opt.OtpAuthenticationToken;
import com.brwallet.common.opt.OtpClient;
import com.brwallet.common.vo.ResultVo;
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

	public ResultVo<String> register(RegisterVo registerVo) {

		try {
			logger.debug("[register] start ");
			logger.debug("[register] registerVo -> " + registerVo);

			GoogleAuthenticatorKey key   = otpClient.createKey();
			String                 qrUrl = otpClient.createQrUrl(registerVo.getUserName(), "brwallet", key);

			// qr코드 이미지 저장
			util.saveImage(qrUrl, key.getKey());

			// otp 정보 생성
			OtpEntity otp = new OtpEntity();
			otp.setOptKey(key.getKey());
			otp.setOtpUrl(qrUrl);
			otp.setOtpFilePath(System.getProperty("user.dir") + "/otp/" + key.getKey() + ".jpg");

			otpRepository.save(otp);

			logger.debug("[register] otpEntity -> " + otp);

			// 유저 정보 엔티티 생성
			UserEntity user = new UserEntity();

			user.setUserName(registerVo.getUserName());
			user.setUserId(registerVo.getUserId());
			user.setUserEmail(registerVo.getUserEmail());
			user.setUserPassword(passwordEncoder.encode(registerVo.getUserPassword()));
			user.setOtpEntity(otp);
			user.setActive(false);
			user.setPrivateKeyYn(false);

			// 유저 정보 엔티티 저장
			saveUserEntity(user);

			logger.debug("[register] userEntity -> " + user);

			String str = "<h3>" + registerVo.getUserName() + "님 회원가입을 환영합니다.</h3><br>아래 설명을 따라서 OTP 등록을 진행해 주시기 바랍니다.<br><br>";

			emailClient.sendEmail(registerVo.getUserEmail(), mailSubject, str + mailMessage, key.getKey());

		} catch (Exception e) {

			logger.debug("[register] error ");

		}
		logger.debug("[register] finish ");
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

	public ResultVo<String> active(LoginVo loginVo) {

		logger.debug("[active] start ");
		logger.debug("[active] loginVo -> " + loginVo);

		UserEntity user = findUserByUserId(loginVo.getUserId());

		if (!passwordEncoder.matches(loginVo.getUserPassword(), user.getUserPassword())) {
			return util.setResult("9999", false, "fail login", null);
		}

		// 오티피 정보 확인시 계정 활성화
		if (otpClient.otpAuthorize(user.getOtpEntity()
			.getOptKey(), loginVo.getOtpCode())) {

			user.setActive(true);
			logger.debug("[active] finish ");

			return util.setResult("0000", true, "Success login", null);
		} else {

			logger.debug("[active] error ");
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

		logger.debug("[login] start ");
		logger.debug("[login] loginVo -> " + loginVo);

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

		logger.debug("[login] jwtTokenVo -> " + jwtTokenVo);

		logger.debug("[login] finish ");
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

		logger.debug("[refreshToken] start ");
		logger.debug("[refreshToken] refreshTokenVo -> " + refreshTokenVo);

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

		logger.debug("[refreshToken] jwtTokenVo -> " + jwtTokenVo);

		logger.debug("[refreshToken] finish ");
		return util.setResult("0000", true, "Success refresh token", jwtTokenVo);
	}

	public UserEntity saveUserEntity(UserEntity userEntity) {
		return userRepository.save(userEntity);
	}

	public UserEntity findUserByUserId(String userId) {
		return userRepository.findByUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException("can't find user " + userId + " in database"));
	}

}