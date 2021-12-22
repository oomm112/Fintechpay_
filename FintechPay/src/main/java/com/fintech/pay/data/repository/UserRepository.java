package com.fintech.pay.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fintech.pay.data.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>{
	//해당 쿼리가 수행이 될때 lazy조회가 아닌 eager조회로 authorities정보를 가져오는 어노테이션
	@EntityGraph(attributePaths = "authorities")
	Optional<UserEntity> findOneWithAuthoritiesById(String id);
}
