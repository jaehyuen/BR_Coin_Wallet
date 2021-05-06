package com.brwallet.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brwallet.wallet.entity.WalletEntity;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, String> {
	
}
