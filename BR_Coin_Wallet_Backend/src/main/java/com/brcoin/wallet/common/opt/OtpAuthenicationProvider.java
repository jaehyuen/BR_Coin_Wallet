package com.brcoin.wallet.common.opt;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.brcoin.wallet.acount.service.UserDetailsServiceImpl;
import com.warrenstrange.googleauth.IGoogleAuthenticator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OtpAuthenicationProvider implements AuthenticationProvider {

	private final UserDetailsServiceImpl userDetailsService;

	private final PasswordEncoder        passwordEncoder;

	private final IGoogleAuthenticator   googleAuthenticator;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String userId   = (String) authentication.getPrincipal();
		String password = (String) authentication.getCredentials();
		int    optCode  = 0;

		if (authentication.getClass()
			.equals(OtpAuthenticationToken.class)) {

			optCode = ((OtpAuthenticationToken) authentication).getOtpCode();
		}

		System.out.println("in auth");
		System.out.println("userId " + userId);
		System.out.println("password " + password);
		System.out.println("optCode " + optCode);

		OtpUser user = userDetailsService.loadUserByUsername(userId);
		System.out.println("optkey " + user.getOtpKey());

		if (!userId.equals(user.getUsername())) {
			throw new BadCredentialsException(userId + " 계정의 아이디가 틀림");
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
