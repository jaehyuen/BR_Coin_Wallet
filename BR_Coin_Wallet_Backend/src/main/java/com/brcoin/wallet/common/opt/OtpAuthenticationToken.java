package com.brcoin.wallet.common.opt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.SpringSecurityCoreVersion;

public class OtpAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	private int otpCode;

	public int getOtpCode() {
		return otpCode;
	}

	public void setOtpCode(int otpCode) {
		this.otpCode = otpCode;
	}

	public OtpAuthenticationToken(Object principal, Object credentials, int otpCode) {
		super(principal, credentials);
		this.otpCode = otpCode;

	}

	public OtpAuthenticationToken(Object principal, Object credentials) {
		super(principal, credentials);
		this.otpCode = 0;

	}
	
	

}
