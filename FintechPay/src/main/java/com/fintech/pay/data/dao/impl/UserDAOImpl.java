package com.fintech.pay.data.dao.impl;

import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fintech.pay.data.dao.UserDAO;
import com.fintech.pay.data.dto.UserDTO;
import com.fintech.pay.data.entity.Authority;
import com.fintech.pay.data.entity.UserEntity;
import com.fintech.pay.data.repository.UserRepository;
import com.fintech.pay.util.SecurityUtil;

@Service
@Transactional
public class UserDAOImpl implements UserDAO{
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public UserDAOImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	//토큰을 가져오는 메소드
	//username이 db에 존재치 않으면 Authority와 User정보를 생성 후, UserRepository의 save메소드를 통해 db에 정보를 저장한다.
	@Transactional
	public UserEntity signup(UserDTO userDto){
		if (userRepository.findOneWithAuthoritiesById(userDto.getId()).orElse(null) != null) {
			throw new RuntimeException("이미 가입되어 있는 유저입니다.");
		}

		//일반 가입 유저는 Role_User의 권한을 하나 가지고있다.
		Authority authority = Authority.builder()
				.authorityName("ROLE_USER")
				.build();

		UserEntity userEntity = UserEntity.builder()
				.id(userDto.getId())
				.password(passwordEncoder.encode(userDto.getPassword()))
				.code(null)
				.name(userDto.getName())
				.resistration_number(userDto.getResistration_number())
				.tel(userDto.getTel())
				.activated(true)
				.authorities(Collections.singleton(authority))
				.build();

		return userRepository.save(userEntity);
	}

	//id를 기준으로 정보를 가져오는 메소드
	@Transactional(readOnly = true)
	public Optional<UserEntity> getUserWithAuthorities(String id) {
		return userRepository.findOneWithAuthoritiesById(id);
	}

	//SecurityContext에 저장된 user의 정보만 가져오는 메소드
	@Transactional(readOnly = true)
	public Optional<UserEntity> getMyUserWithAuthorities() {
		return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesById);
	}


	//1일경우 ID에 맞는 정보가 없는경우, 0인경우 정상처리
	@Override
	public int updateUser(String id, String tel, String password) {
		Optional<UserEntity> isUser = userRepository.findOneWithAuthoritiesById(id);

		//만약 id로 조회한 DB값이 없을경우
		if (isUser == null) {
			return 1;
		}
		
		password = passwordEncoder.encode(password);
		
		//유저 정보 수정  <패스워드 인코더로 인코딩>
		//pwd와 tel만 수정 가능.
		UserEntity userEntity = UserEntity.builder()
				.userId(isUser.get().getUserId())
				.id(isUser.get().getId())
				.password(password)
				.code(isUser.get().getCode())
				.name(isUser.get().getName())
				.tel(tel)
				.activated(true)
				.resistration_number(isUser.get().getResistration_number())
				.authorities(isUser.get().getAuthorities())
				.build();

		userRepository.save(userEntity);
		return 0;
	}

	@Override
	@Transactional
	public int deleteUser(String id, String password) {
		Optional<UserEntity> isUser = userRepository.findOneWithAuthoritiesById(id);
		
		if (isUser == null) {
			return 0;
		}
		String isUserPassword = isUser.get().getPassword();
		if (!passwordEncoder.matches(password, isUserPassword)) {
			return 1;
		}
		//유저 탈퇴
		userRepository.deleteById(isUser.get().getUserId());
		return 2;
	}
}
