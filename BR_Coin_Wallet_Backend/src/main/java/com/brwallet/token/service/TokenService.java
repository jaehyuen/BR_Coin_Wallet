package com.brwallet.token.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.brwallet.common.BridgePath;
import com.brwallet.common.Util;
import com.brwallet.common.vo.ResultVo;
import com.brwallet.token.vo.TokenVo;
import com.brwallet.token.vo.TransferVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final Util util;

	private Logger     logger = LoggerFactory.getLogger(this.getClass());

	public ResultVo<String> createToken(TokenVo tokenVo) {

		logger.debug("[createToken] start ");
		logger.debug("[createToken] tokenVo -> " + tokenVo);

		ResultVo<String> result = util.sendPost(BridgePath.POST_TOKEN.getValue(), tokenVo, String.class);

		logger.debug("[createToken] finish ");

		return result;

	}
	
	public ResultVo<String> transferToken(TransferVo transferVo) {

		logger.debug("[transferToken] start ");
		logger.debug("[transferToken] tokenVo -> " + transferVo);

		ResultVo<String> result = util.sendPost(BridgePath.POST_TOKEN_TRANSFER.getValue(), transferVo, String.class);

		logger.debug("[transferToken] finish ");

		return result;

	}

}