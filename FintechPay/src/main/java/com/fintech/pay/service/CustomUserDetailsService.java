package com.fintech.pay.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.fintech.pay.data.entity.UserEntity;
import com.fintech.pay.data.repository.UserRepository;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService{
	private final UserRepository userRepository;

	/**
	 * @param userRepository
	 */
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	
	//로그인시 db에서 유저정보와 권한정보를 가져오게 된다. 해당 정보를 기반으로 userDetails.User객체를 생성해서 리턴해준다.
	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String id) {
		return userRepository.findOneWithAuthoritiesById(id)
				.map(userEntity -> createUser(id, userEntity))
				.orElseThrow(() -> new UsernameNotFoundException(id + " -> 데이터베이스에서 찾을 수 없습니다."));
	}

	private User createUser(String id, UserEntity userEntity) {
		if (!userEntity.isActivated()) {
			throw new RuntimeException(id + " -> 활성화되어 있지 않습니다.");
		}
		List<GrantedAuthority> grantedAuthorities = userEntity.getAuthorities().stream()
				.map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
				.collect(Collectors.toList());
		return new User(userEntity.getId(),
				userEntity.getPassword(),
				grantedAuthorities);
	}

}
