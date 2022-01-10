package com.fintech.pay.controller;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.pay.config.jwt.JwtFilter;
import com.fintech.pay.config.jwt.TokenProvider;
import com.fintech.pay.data.dto.LoginDTO;
import com.fintech.pay.data.dto.RegiDTO;
import com.fintech.pay.data.dto.TokenDTO;
import com.fintech.pay.data.entity.UserEntity;
import com.fintech.pay.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = {"회원정보 메소드"})
@CrossOrigin
@RequestMapping(value = "/user")
public class UserController {
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final UserService userService;

	@Autowired
	public UserController(UserService userService, TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
		this.tokenProvider = tokenProvider;
		this.authenticationManagerBuilder = authenticationManagerBuilder;
		this.userService = userService;
	}

	//로그인
	@ApiOperation(value="로그인")
	@PostMapping("/authenticate")
	public ResponseEntity<TokenDTO> authorize( HttpServletRequest request, @Valid @RequestBody LoginDTO loginDTO){
		//loginDTO의 유저이름, 패스워드를 파라미터로 받고, 이를 이용해 UserNamePasswordAuthenticationToken을 생성한다.
		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(loginDTO.getId(), loginDTO.getPassword());

		//authenticationToken을 이용해서 Authentication객체를 생성하기 위하여, authenticate메소드가 실행이 될때,
		//loadUserByUsername메소드가 실행되게 된다.
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		//SecurityContextHolder에 저장된 정보를 이용해서 tokenProvider클래스의 createToken을 이용하여 토큰을 만든다.
		String jwt = tokenProvider.createToken(authentication);

		//리스폰스 헤더에 토큰을 넣어준다.
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER,jwt);

		//그 후 리스폰스 바디로 리턴해준다.
		return new ResponseEntity<>(new TokenDTO(jwt), httpHeaders, HttpStatus.OK);
	}

	//정보 수정 <관리자용>
	//전화번호와 비밀번호만 수정가능
	@ApiOperation(value="관리자용 유저 정보수정", notes="관리자권한을 가졌을때 유저 정보를 수정 할수있다.")
	@PostMapping(value = "/updateuser")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value="아이디") ,
		@ApiImplicitParam(name = "password", value="비밀번호"), 
		@ApiImplicitParam(name = "tel", value="전화번호") 
	})
	public Map<String, String> updateUser(@RequestParam String id,@RequestParam String tel,@RequestParam String password) {
		Map<String, String> response = new HashMap<>();
		int response_code = 0;
		
		//유효성검사
		boolean patternTel = Pattern.matches("\\d{3}-\\d{4}-\\d{4}", tel);
		boolean patternPassword = Pattern.matches("^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).{8,20}$", password);

		if (!patternTel) {
			response_code = 2;
		}else if(!patternPassword){
			response_code = 3;
		}

		if (patternPassword && patternTel) {			
			response_code = userService.updateUser(id, tel, password);
		}
		
		if (response_code == 0) {
			response.put("result", "정상적으로 수정되었습니다");
		}else if(response_code == 1){
			response.put("result", "수정을 실패하였습니다");
			response.put("reason", "일치하는 회원 정보가 없습니다.");
		}else if(response_code == 2) {
			response.put("result", "유효성 검사가 실패하였습니다.");
			response.put("reason", "전화번호의 패턴이 '010-1234-1234'형식과 일치하지 않습니다.");
		}else if(response_code == 3) {
			response.put("result", "유효성 검사가 실패하였습니다.");
			response.put("reason", "비밀번호 패턴이 [8~100자 / 특수문자 한글자 포함]와 일치하지 않습니다.");
		}
		return response;
	}

	//정보 수정 
	//전화번호와 비밀번호만 수정가능
	@ApiOperation(value="정보수정")
	@PostMapping(value = "/update")
	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "password", value="비밀번호"), 
		@ApiImplicitParam(name = "tel", value="전화번호") 
	})
	public Map<String, String> updateMyData(@RequestParam String tel, @RequestParam String password) {
		Map<String, String> response = new HashMap<>();
		int response_code = 0;
		
		//토큰에 담겨있는 아이디를 가져온다(현재 로그인한 아이디)
		String id = userService.getMyUserWithAuthorities().get().getId();
		
		//유효성 검사
		boolean patternTel = Pattern.matches("\\d{3}-\\d{4}-\\d{4}", tel);
		boolean patternPassword = Pattern.matches("^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).{8,20}$", password);

		if (!patternTel) {
			response_code = 2;
		}else if(!patternPassword){
			response_code = 3;
		}

		if (patternPassword && patternTel) {			
			response_code = userService.updateUser(id, tel, password);
		}
		
		if (response_code == 0) {
			response.put("result", "정상적으로 수정되었습니다");
		}else if(response_code == 1){
			response.put("result", "수정을 실패하였습니다");
			response.put("reason", "일치하는 회원 정보가 없습니다.");
		}else if(response_code == 2) {
			response.put("result", "유효성 검사가 실패하였습니다.");
			response.put("reason", "전화번호의 패턴이 '010-1234-1234'형식과 일치하지 않습니다.");
		}else if(response_code == 3) {
			response.put("result", "유효성 검사가 실패하였습니다.");
			response.put("reason", "비밀번호 패턴이 [8~100자 / 특수문자 한글자 포함]와 일치하지 않습니다.");
		}
		return response;
	}


	//회원탈퇴
	//현재 ID값을 받고, 입력된 PWD와 일치하는 정보가 있을경우 탈퇴
	@ApiOperation(value="회원탈퇴", notes = "현재 로그인된 ID를 받고, ID의 password를 post로 받아 탈퇴한다.")
	@DeleteMapping(value = "/delete")
	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "password", value="비밀번호", required=true) 
	})
	public Map<String, String> deleteUser(@RequestParam String password){
		Map<String, String> response = new HashMap<>();

		//토큰에 담겨있는 아이디를 가져온다(현재 로그인한 아이디)
		String id = userService.getMyUserWithAuthorities().get().getId();

		int response_code = userService.deleteUser(id, password);
		if (response_code == 2) {
			response.put("result", "정상적으로 탈퇴되었습니다");
		}else {
			response.put("result", "탈퇴되지 않았습니다");
			response.put("reason", "일치하는 회원 정보가 없습니다.");
		}
		return response;
	}

	//UserDTO객체를 파라미터로 받은후 USERservice의 signup메소드 수행
	@ApiOperation(value="회원가입")
	@PostMapping("/signup")
	public ResponseEntity<UserEntity> signup(@Valid @RequestBody RegiDTO regiDto) {
		return	ResponseEntity.ok(userService.signup(regiDto)); 
	}


	//@PreAuthorize를 통해 USER,ADMIN두가지 권한 모두 허용한다.
	@ApiOperation(value="정보조회")
	@GetMapping("/user")
	public ResponseEntity<UserEntity> getMyUserInfo() {
		return ResponseEntity.ok(userService.getMyUserWithAuthorities().get());
	}

	//ADMIN권한만 호출할수 있도록 설정
	@ApiOperation(value="유저정보 조회")
	@GetMapping("/finduser")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value="아이디", required=true, dataType =
				"String", paramType="path") 
	})
	public ResponseEntity<UserEntity> getUserInfo(@RequestParam String id) {
		return ResponseEntity.ok(userService.getUserWithAuthorities(id).get());
	}
}

