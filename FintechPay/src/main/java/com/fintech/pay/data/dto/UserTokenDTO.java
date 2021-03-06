package com.fintech.pay.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserTokenDTO {
	//발급받은 토큰
	private String accessToken;

	//발급받은 토큰의 유형 (Bearer고정)
	private String tokenType;

	//발급받은 토큰 만료시간
	private Integer expiresIn;

	//발급받은 토큰갱신시 필요한 토큰
	private String refreshToken;

	//발급받은 토큰의 권한
	private String scope;

	//사용자 일련번호
	private Integer userSeqNo;
}
