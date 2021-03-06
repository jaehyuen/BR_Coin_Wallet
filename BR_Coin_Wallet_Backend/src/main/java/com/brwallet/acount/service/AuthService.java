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
	 * ???????????? ?????????
	 * 
	 * @param registerDto ???????????? ?????? ?????? DTO
	 * 
	 * @return ???????????? ?????? DTO
	 */

	public ResultVo<String> register(RegisterVo registerVo) {

		try {
			logger.debug("[register] start ");
			logger.debug("[register] registerVo -> " + registerVo);

			GoogleAuthenticatorKey key   = otpClient.createKey();
			String                 qrUrl = otpClient.createQrUrl(registerVo.getUserName(), "brwallet", key);

			// qr?????? ????????? ??????
			util.saveImage(qrUrl, key.getKey());

			// otp ?????? ??????
			OtpEntity otp = new OtpEntity();
			otp.setOptKey(key.getKey());
			otp.setOtpUrl(qrUrl);
			otp.setOtpFilePath(System.getProperty("user.dir") + "/otp/" + key.getKey() + ".jpg");

			otpRepository.save(otp);

			logger.debug("[register] otpEntity -> " + otp);

			// ?????? ?????? ????????? ??????
			UserEntity user = new UserEntity();

			user.setUserName(registerVo.getUserName());
			user.setUserId(registerVo.getUserId());
			user.setUserEmail(registerVo.getUserEmail());
			user.setUserPassword(passwordEncoder.encode(registerVo.getUserPassword()));
			user.setOtpEntity(otp);
			user.setActive(false);
			user.setPrivateKeyYn(false);

			// ?????? ?????? ????????? ??????
			saveUserEntity(user);

			logger.debug("[register] userEntity -> " + user);

			String str = "<h3>" + registerVo.getUserName() + "??? ??????????????? ???????????????.</h3><br>?????? ????????? ????????? OTP ????????? ????????? ????????? ????????????.<br><br>";

			emailClient.sendEmail(registerVo.getUserEmail(), mailSubject, str + mailMessage, key.getKey());

		} catch (Exception e) {

			logger.debug("[register] error ");

		}
		logger.debug("[register] finish ");
		return util.setResult("0000", true, "Success register ", null);
	}

	/**
	 * OTP ?????? ?????????(?????? ??????)
	 * 
	 * @param loginVo ????????? ?????? ?????? DTO
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

		// ????????? ?????? ????????? ?????? ?????????
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
	 * ????????? ?????????
	 * 
	 * @param loginDto ????????? ?????? DTO
	 * 
	 * @return ????????? ?????? DTO
	 */

	public ResultVo<JwtTokenVo> login(LoginVo loginVo) {

		logger.debug("[login] start ");
		logger.debug("[login] loginVo -> " + loginVo);

		// ????????? ?????? (????????? ????????? 401??????)
		Authentication authenticate = authenticationManager.authenticate(new OtpAuthenticationToken(loginVo.getUserId(), loginVo.getUserPassword(), loginVo.getOtpCode()));

		SecurityContextHolder.getContext()
			.setAuthentication(authenticate);

		// ?????? ???????????? JWT ?????? ??????
		String     token      = jwtProvider.generateToken(authenticate);

		// ????????? ??????
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
	 * JWT ?????? ????????? ?????????
	 * 
	 * @param refreshTokenDto ?????? ????????? ?????? DTO
	 * 
	 * @return ?????? ????????? ?????? DTO
	 */

	@Transactional(readOnly = true)
	public ResultVo<JwtTokenVo> refreshToken(RefreshTokenVo refreshTokenVo) {

		logger.debug("[refreshToken] start ");
		logger.debug("[refreshToken] refreshTokenVo -> " + refreshTokenVo);

		// ???????????? ?????? ????????? ??????
		refreshTokenService.validateRefreshToken(refreshTokenVo.getRefreshToken());

		// JWT ?????? ?????????
		String     token      = jwtProvider.generateTokenWithUserName(refreshTokenVo.getUserId());

		// ????????? ??????
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
		return userRepository.findOptionalByUserId(userId)
			.orElseThrow(() -> new IllegalArgumentException("can't find user " + userId + " in database"));
	}

}