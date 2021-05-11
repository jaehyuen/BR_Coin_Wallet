package com.brwallet.wallet.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.brwallet.acount.entity.UserEntity;
import com.brwallet.acount.service.AuthService;
import com.brwallet.client.CryptoClient;
import com.brwallet.common.BridgePath;
import com.brwallet.common.Util;
import com.brwallet.common.error.exception.WalletExistException;
import com.brwallet.common.vo.ResultVo;
import com.brwallet.wallet.entity.WalletEntity;
import com.brwallet.wallet.repository.WalletRepository;
import com.brwallet.wallet.vo.CreateWalletVo;
import com.brwallet.wallet.vo.KeyPairVo;
import com.brwallet.wallet.vo.WalletVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {

	private final AuthService      authService;
	private final CryptoClient     cryptoClient;
	private final WalletRepository walletRepository;
	private final Util             util;

	private Logger                 logger = LoggerFactory.getLogger(this.getClass());

	public ResultVo<String> createWallet(CreateWalletVo createWalletVo) {

		logger.debug("[createWallet] start ");
		logger.debug("[createWallet] createWalletVo -> " + createWalletVo);

		Authentication   loginedUser = SecurityContextHolder.getContext()
			.getAuthentication();
		UserEntity       user        = authService.findUserByUserId(loginedUser.getName());
		ResultVo<String> result      = new ResultVo<String>();

		logger.debug("[createWallet] loginedUser is  -> " + loginedUser.getName());

		if (user.getWalletEntity() != null) {
			logger.debug("[createWallet] error ");
			throw new WalletExistException(user.getUserId() + " 계정에 이미 지갑이 존재합니다.");
		}

		try {
			KeyPairVo keyPair = cryptoClient.generateKeys();

			logger.debug("[createWallet] keyPair is  -> " + keyPair);

			createWalletVo.setPublicKey(keyPair.getPublicKey());

			result = util.sendPost(BridgePath.POST_WALLET.getValue(), createWalletVo, String.class);

			WalletEntity wallet = new WalletEntity();
			wallet.setWalletAddress(result.getResultData());
			wallet.setPublicKey(keyPair.getPublicKey());

			if (createWalletVo.isPrivKeyYn()) {
				wallet.setPrivateKey(keyPair.getPrivateKey());

			}

			walletRepository.save(wallet);

			user.setPrivateKeyYn(createWalletVo.isPrivKeyYn());
			user.setWalletEntity(wallet);
			authService.saveUserEntity(user);

		} catch (NoSuchAlgorithmException | NoSuchProviderException | IOException e) {

			logger.debug("[createWallet] error ");

		}

		logger.debug("[createWallet] finish ");
		return result;
	}

	public ResultVo<WalletVo> getWallet(String walletId) {

		logger.debug("[getWallet] start ");
		logger.debug("[getWallet] walletId -> " + walletId);

		ResultVo<WalletVo> result = util.sendGet(BridgePath.GET_WALLET.getValue() + walletId, WalletVo.class);

		if (result.getResultCode()
			.equals("9999")) {
			throw new IllegalArgumentException("지갑 주소가 잘못되었습니다. 지갑 주소 : " + walletId);

		}

		logger.debug("[getWallet] finish ");

		return result;

	}

}