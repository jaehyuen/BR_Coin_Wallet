package com.brwallet.wallet.vo;

import lombok.Data;

@Data
public class BalanceInfoVo {
	private String balance;
	private int    tokenId;
	private int    unlockDate;

}
