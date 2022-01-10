package com.fintech.pay.data.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
	@NotNull
	@Size(min = 3, max = 50)
	@ApiModelProperty(value="아이디", example = "oomm113")
	private String id;
	
	@NotNull
	@Size(min = 3, max = 100)
	@ApiModelProperty(value="비밀번호", example = "password")
	private String password;
}
