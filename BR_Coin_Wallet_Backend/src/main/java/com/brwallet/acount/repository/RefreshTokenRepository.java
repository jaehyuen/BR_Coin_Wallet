package com.brwallet.acount.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brwallet.acount.entity.RefreshTokenEntity;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
	Optional<RefreshTokenEntity> findByToken(String token);

	void deleteByToken(String token);
}