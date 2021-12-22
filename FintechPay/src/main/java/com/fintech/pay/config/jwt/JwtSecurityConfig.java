package com.fintech.pay.config.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>{
	//SecurityConfigurerAdapter를 통해 TokenProvider를 주입받은 후 JwtFilter를 통해 시큐리티로직에 필터를 등록한다.
	private TokenProvider tokenProvider;

	/**
	 * @param tokenProvider
	 */
	public JwtSecurityConfig(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception{
		JwtFilter customFilter = new JwtFilter(tokenProvider);
		http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
		
		//항상 세션을 생성
		http.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
	}
}
