package com.brwallet.wallet.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.brwallet.acount.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "WALLET")
@AllArgsConstructor
@NoArgsConstructor
public class WalletEntity extends BaseEntity {

	@Id
	@Column(name = "WALLET_ADDRESS", unique = true, nullable = false, columnDefinition = "LONGTEXT")
	private String otpUrl;

	@Column(name = "WALLET_PUB_KEY", unique = true, nullable = false, columnDefinition = "LONGTEXT")
	private String publicKey;

	@Column(name = "WALLET_PRIV_KEY", unique = true, nullable = true, columnDefinition = "LONGTEXT")
	private String privateKey;

}
