package com.brwallet.wallet.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.brwallet.client.CryptoClient;
import com.brwallet.common.Util;
import com.brwallet.common.vo.ResultVo;
import com.brwallet.wallet.vo.WalletVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {

	private final CryptoClient cryptoClient;
	private final Util         util;

	public String test() {
		Authentication test = SecurityContextHolder.getContext()
			.getAuthentication();
		System.out.println(test.getCredentials());
		System.out.println(test.getName());
		System.out.println(test.getPrincipal());

		try {
			Map<String, String> a = cryptoClient.generateKeys();
			System.out.println(a.get("private"));
			System.out.println(a.get("public"));
		} catch (NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public void getWallet(String walletId) {

		System.out.println("walletId is : " + walletId);
		String path = "/api/brcoin/wallet?walletId=" + walletId;
		System.out.println("path it : " + path);
		
		ResultVo<WalletVo> test1 = new ResultVo<WalletVo>();
		WalletVo test2 = new WalletVo();
		
		System.out.println("test1 type : "+test1.getClass().getGenericSuperclass());
		System.out.println("test2 type : "+test2.getClass().getGenericSuperclass());
		WalletVo a = util.sendGet(path, WalletVo.class);

		System.out.println("return is : " + a);

	}

}