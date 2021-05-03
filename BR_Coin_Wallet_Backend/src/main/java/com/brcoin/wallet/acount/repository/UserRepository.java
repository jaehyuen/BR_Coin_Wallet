package com.brcoin.wallet.acount.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brcoin.wallet.acount.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByUserId(String userId);
	UserEntity findByUserIdAndUserPassword(String userId,String userPassword);
}
