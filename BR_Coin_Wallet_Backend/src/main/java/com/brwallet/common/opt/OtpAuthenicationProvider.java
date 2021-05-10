package com.brwallet.common.opt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.brwallet.acount.service.UserDetailsServiceImpl;
import com.warrenstrange.googleauth.IGoogleAuthenticator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OtpAuthenicationProvider implements AuthenticationProvider {

	private final UserDetailsServiceImpl userDetailsService;
	private final PasswordEncoder        passwordEncoder;
	private final IGoogleAuthenticator   googleAuthenticator;

	private Logger                       logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String userId   = (String) authentication.getPrincipal();
		String password = (String) authentication.getCredentials();
		int    optCode  = 0;

		if (authentication.getClass()
			.equals(OtpAuthenticationToken.class)) {

			optCode = ((OtpAuthenticationToken) authentication).getOtpCode();
		}

		OtpUser user = userDetailsService.loadUserByUsername(userId);

		logger.debug("[createWallet] userId -> " + userId);
		logger.debug("[createWallet] password -> " + password);
		logger.debug("[createWallet] optCode -> " + optCode);
		logger.debug("[createWallet] optCode -> " + optCode);

		if (!userId.equals(user.getUsername())) {
			throw new BadCredentialsException(userId + " 계정의 아이디가 틀렸습니다.");
		}

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException(userId + " 계정의 비밀번호가 틀렸습니다.");
		}

		if (!user.isEnabled()) {
			throw new BadCredentialsException(userId + " 계정이 잠겨 있습니다.");
		}

		if (!googleAuthenticator.authorize(user.getOtpKey(), optCode)) {
			throw new BadCredentialsException(userId + " 계정의 OTP 코드가 틀렸습니다.");
		}

		return new UsernamePasswordAuthenticationToken(userId, password, user.getAuthorities());

	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return true;
	}

}
