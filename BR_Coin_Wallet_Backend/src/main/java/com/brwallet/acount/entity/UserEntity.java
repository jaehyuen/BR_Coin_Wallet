package com.brwallet.acount.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import com.brwallet.wallet.entity.WalletEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "USER")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long      id;

	@Column(name = "USER_NAME", nullable = false)
	private String    userName;

	@Column(name = "USER_ID", unique = true, nullable = false)
	private String    userId;

	@Column(name = "USER_PASSWORD", nullable = false)
	private String    userPassword;

	@Email
	@Column(name = "USER_EMAIL", nullable = false)
	private String    userEmail;

	@OneToOne(targetEntity = OtpEntity.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "OTP_KEY")
	private OtpEntity otpEntity;
	
	@OneToOne(targetEntity = WalletEntity.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "WALLET_ADDRESS")
	private WalletEntity walletEntity;

	@Column(name = "ACTIVE")
	private boolean   active;
}
