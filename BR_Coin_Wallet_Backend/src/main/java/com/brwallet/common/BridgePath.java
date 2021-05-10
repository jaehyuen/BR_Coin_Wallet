package com.brwallet.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BridgePath {

	GET_WALLET(0, "/api/brcoin/wallet?walletId="), // 지갑 조회
	POST_WALLET(1, "/api/brcoin/wallet"); // 지갑 조회

	private final int    index;
	private final String value;
}
