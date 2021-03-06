package com.brwallet.acount.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brwallet.acount.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findOptionalByUserId(String userId);
	UserEntity findByUserIdAndUserPassword(String userId,String userPassword);
}
