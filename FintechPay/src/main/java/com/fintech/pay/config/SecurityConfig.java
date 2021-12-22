package com.fintech.pay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fintech.pay.config.jwt.JwtAccessDeniedHandler;
import com.fintech.pay.config.jwt.JwtAuthenticationEntryPoint;
import com.fintech.pay.config.jwt.JwtSecurityConfig;
import com.fintech.pay.config.jwt.TokenProvider;

@EnableWebSecurity
//@PreAuthorize 어노테이션을 메소드 단위로 추가하기위해서 사용
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	private final TokenProvider tokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	

    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }
    
    //PasswordEncoder를 빈객체로 주입
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //스웨거 인증 필요없이 사용가능 위해
	@Override
	public void configure(WebSecurity web) {
		web
		.ignoring()
		.antMatchers(
				"/swagger-ui.html#/"
				);
	}

	 @Override
	    protected void configure(HttpSecurity httpSecurity) throws Exception {
	        httpSecurity
	                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
	                .csrf().disable()

	                .exceptionHandling()
	                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
	                .accessDeniedHandler(jwtAccessDeniedHandler)

	                // enable h2-console
	                .and()
	                .headers()
	                .frameOptions()
	                .sameOrigin()

	                // 세션을 사용하지 않기 때문에 STATELESS로 설정
	                .and()
	                .sessionManagement()
	                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

	                .and()
	                .authorizeRequests()
	                .antMatchers("/user/authenticate").permitAll()
	                .antMatchers("/user/signup").permitAll()
	                .antMatchers("/openBanking/code").permitAll()

	                .anyRequest().authenticated()

	                .and()
	                .apply(new JwtSecurityConfig(tokenProvider));
	    }
}
