package com.brcoin.wallet.acount.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "OTP")
@AllArgsConstructor
@NoArgsConstructor
public class OtpEntity extends BaseEntity {

	@Id
	@Column(name = "OTP_KEY")
	private String optKey;

	@Column(name = "OTP_URL", unique = true, nullable = false, columnDefinition = "LONGTEXT")
	private String otpUrl;

	@Column(name = "OTP_FILE_PATH", unique = true, nullable = false)
	private String otpFilePath;

}
