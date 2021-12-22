package com.fintech.pay.data.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "userlist")
@Getter
@Setter
@Builder
@DynamicUpdate
public class UserEntity{

	@JsonIgnore
	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(name = "id", length = 50, unique = true)
	private String id;

	@Column(name = "name", length = 50, unique = true)
	private String name;

	@Column(name = "resistration_number", length = 20)
	private String resistration_number;

	@Column(name = "tel", length = 30)
	private String tel;

	@JsonIgnore
	@Column(name = "password")
	private String password;

	@Column(name = "code")
	private String code;

	@JsonIgnore
	@Column(name = "activated")
	private boolean activated;

	//발급받은 토큰
	@Column(name = "access_token", length = 1000)
	private String access_token;

	//발급받은 토큰의 유형 (Bearer고정)
	@Column(name = "token_type")
	private String token_type;

	//발급받은 토큰 만료시간
	@Column(name = "expires_in")
	private Integer expires_in;

	//발급받은 토큰갱신시 필요한 토큰
	@Column(name = "refresh_token", length = 1000)
	private String refresh_token;

	//발급받은 토큰의 권한
	@Column(name = "scope")
	private String scope;

	//사용자 일련번호
	@Column(name = "user_seq_no")
	private Integer user_seq_no;

	//핀테크 유저번호
	@Column(name = "fintech_number")
	private String fintech_number;
	
	@ManyToMany
	@JoinTable(
			name = "user_authority",
			joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
			inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
	private Set<Authority> authorities;


}
