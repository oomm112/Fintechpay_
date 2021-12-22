package com.fintech.pay.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fintech.pay.data.dao.UserDAO;
import com.fintech.pay.data.dto.UserDTO;
import com.fintech.pay.data.entity.UserEntity;
import com.fintech.pay.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

	UserDAO userDAO;
		
	@Autowired
	public UserServiceImpl(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public UserEntity signup(UserDTO userDTO) {
		return userDAO.signup(userDTO);
	}


	@Override
	public Optional<UserEntity> getMyUserWithAuthorities() {
		return userDAO.getMyUserWithAuthorities();
	}


	@Override
	public Optional<UserEntity> getUserWithAuthorities(String id) {
		return userDAO.getUserWithAuthorities(id);
	}

	@Override
	public int updateUser(String id, String tel, String password) {
		return userDAO.updateUser(id, tel, password);
	}

	@Override
	public int deleteUser(String id, String password) {
		return userDAO.deleteUser(id, password);
	}

}
