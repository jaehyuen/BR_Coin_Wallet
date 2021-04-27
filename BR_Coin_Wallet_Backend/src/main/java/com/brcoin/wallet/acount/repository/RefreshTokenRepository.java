package com.brcoin.wallet.acount.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brcoin.wallet.acount.entity.RefreshTokenEntity;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
	Optional<RefreshTokenEntity> findByToken(String token);

	void deleteByToken(String token);
}