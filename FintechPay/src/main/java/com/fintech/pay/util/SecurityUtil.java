package com.fintech.pay.util;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
	private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
	
	private SecurityUtil() {
		
	}
	
	
	//SecurityContext의 Authentication객체를 이용해 username을 리턴해주는 유틸성 메소드
	public static Optional<String> getCurrentUsername(){
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null) {
			logger.debug("Security Context에 인증 정보가 없습니다.");
			return Optional.empty();
		}
		
		String username = null;
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
			username = springSecurityUser.getUsername();
		}else if(authentication.getPrincipal() instanceof String){
			username = (String) authentication.getPrincipal();
		}
		return Optional.ofNullable(username);
	}
}
