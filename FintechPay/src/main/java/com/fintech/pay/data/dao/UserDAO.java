package com.fintech.pay.data.dao;

import java.util.Optional;

import com.fintech.pay.data.dto.UserDTO;
import com.fintech.pay.data.entity.UserEntity;

public interface UserDAO {
	//회원가입
	UserEntity signup(UserDTO userDto);

	//유저정보조회 (관리자용)
	Optional<UserEntity> getMyUserWithAuthorities();

	//내 정보조회
	Optional<UserEntity> getUserWithAuthorities(String id);

	//유저 정보 수정
	int updateUser(String id, String tel, String password);
	
	//유저 탈퇴
	int deleteUser(String id, String password);
}
