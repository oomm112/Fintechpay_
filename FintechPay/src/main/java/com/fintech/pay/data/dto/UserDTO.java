package com.fintech.pay.data.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO{
	//ID
	//숫자나 영문자만 사용가능
	//최대 50글자 까지 가능
	@NotNull
	@Size(min = 3, max = 50)
	@Pattern(regexp = "^[0-9a-zA-Z]*$")
	private String id;

	//사용자 이름 한글만 가능.
	@NotNull
	@Pattern(regexp = "^[가-힣]*$")
	private String name;

	//사용자 주민등록번호
	//정규표현식 이용
	@NotNull
	@Pattern(regexp = "\\d{6}\\-[1-4]\\d{6}") 
	private String resistrationNumber;

	//사용자 핸드폰번호
	//정규표현식이용 ex) 000-0000-0000 형식
	@NotNull
	@Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}")
	private String tel;


	//Pwd
	//숫자나 영문자가 가능하며, 특수문자를 꼭 하나이상 포함해야한다.
	//8글자 이상 100글자 이하 가능
	@NotNull
	@Size(min = 8, max = 100)
	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$")
	private String password;

	//오픈뱅킹api의 인증코드
	private String code;
	
	//핀테크 번호
	private String fintechNumber;
}
