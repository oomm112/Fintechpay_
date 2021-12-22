package com.fintech.pay.data.dao.impl;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fintech.pay.data.dao.OpenBankingApiDAO;
import com.fintech.pay.data.dao.UserDAO;
import com.fintech.pay.data.entity.UserEntity;
import com.fintech.pay.data.repository.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class OpenBankingDAOImpl implements OpenBankingApiDAO{
	UserRepository userRepository;
	UserDAO userDAO;

	//쿼리 데이터 설정
	Date today = new Date();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	String time = dateFormat.format(today);
	public String bank_tran_id = "M202113689U";

	/**
	 * @param userRepository
	 * @param userDAO 
	 */
	@Autowired
	public OpenBankingDAOImpl(UserRepository userRepository, UserDAO userDAO) {
		this.userRepository = userRepository;
		this.userDAO = userDAO;
	}


	@Override
	public int addUserCode(String code, String id) {
		Optional<UserEntity> isUser = userDAO.getUserWithAuthorities(id);

		//이미 인증을 받아 코드가 존재하는 경우
		if (isUser.get().getCode() != null) {
			return 1;
		}
		System.out.println(isUser.get().getCode());
		//정상 등록시 0을 리턴
		UserEntity userEntity = isUser.get();
		userEntity.setCode(code);

		userRepository.save(userEntity);
		return 0;
	}


	@Override
	public int getToken() {
		UserEntity userEntity = userDAO.getMyUserWithAuthorities().get();
		if (userEntity.getCode() == null) {
			return 1;
		}

		UriComponents uri =
				UriComponentsBuilder
				.fromUriString("https://testapi.openbanking.or.kr/oauth/2.0/token")
				.queryParam("code", userEntity.getCode())
				.queryParam("client_id", "6cc9ec2e-52e9-473a-a392-4a783fed7443")
				.queryParam("client_secret", "60a467ea-7bba-42ac-8498-b50c7e24d70a")
				.queryParam("redirect_uri", "http://localhost:9099/openBanking/code")
				.queryParam("grant_type", "authorization_code")
				.build();

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders header = new HttpHeaders();
		HttpEntity<?> entity = new HttpEntity<>(header);

		ResponseEntity<Map> responseEntity = restTemplate.exchange(uri.toUriString(),HttpMethod.POST ,entity, Map.class);

		if (!responseEntity.getBody().containsKey("access_token")) {
			return 1;
		}

		userEntity.setAccess_token(String.valueOf(responseEntity.getBody().get("access_token")));
		userEntity.setExpires_in(Integer.parseInt(String.valueOf(responseEntity.getBody().get("expires_in"))));
		userEntity.setRefresh_token(String.valueOf(responseEntity.getBody().get("refresh_token")));
		userEntity.setScope(String.valueOf(responseEntity.getBody().get("scope")));
		userEntity.setToken_type(String.valueOf( responseEntity.getBody().get("token_type")));
		userEntity.setUser_seq_no(Integer.parseInt(String.valueOf(responseEntity.getBody().get("user_seq_no"))));

		System.out.println("정상값 + " + userEntity.getAccess_token());
		userRepository.save(userEntity);
		return 0;
	}


	@Override
	public ResponseEntity<String> getUserme() {
		Optional<UserEntity> user = userDAO.getMyUserWithAuthorities();

		Integer user_seq_no = user.get().getUser_seq_no();

		UriComponents uri =
				UriComponentsBuilder
				.fromUriString("https://testapi.openbanking.or.kr/v2.0/user/me")
				.queryParam("user_seq_no", user_seq_no)
				.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+user.get().getAccess_token());

		HttpEntity<?> entity = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(uri.toUriString(), HttpMethod.GET,entity, String.class);

		Optional<UserEntity> isUser = userDAO.getMyUserWithAuthorities();

		//핀테크넘버를 유저DB에 등록해준다.
		UserEntity userEntity = isUser.get();

		if (isUser.get().getFintech_number() != null) {
			System.out.println("이미 핀테크 번호가 존재함");
			return response;
		}

		if (!response.getBody().contains("api_tran_id")) {
			System.out.println("에러");
			return response;
		}

		//json값 파싱 구문들
		Gson gson = new Gson();
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(response.getBody().toString());

		JsonArray jsonArray = (JsonArray) jsonObject.get("res_list");

		//핀테크번호를 저장하기 위한 리스트
		ArrayList<String> datas = new ArrayList<>();

		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject data = (JsonObject) jsonArray.get(i);
			datas.add(gson.fromJson(data.get("fintech_use_num"), String.class));
		}

		userEntity.setFintech_number(datas.get(0));
		userRepository.save(userEntity);
		System.out.println("정상등록");

		return response;
	}


	@Override
	public ResponseEntity<Map> getBalance() {
		Optional<UserEntity> user = userDAO.getMyUserWithAuthorities();

		//난수생성
		Integer random = (int) (Math.random() * ( 1000000000 - 100000000)) + 100000000;

		UriComponents uri =
				UriComponentsBuilder
				.fromUriString("https://testapi.openbanking.or.kr/v2.0/account/balance/fin_num")
				.queryParam("bank_tran_id", bank_tran_id+random)
				.queryParam("fintech_use_num", user.get().getFintech_number())
				.queryParam("tran_dtime", time)
				.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+user.get().getAccess_token());

		HttpEntity<?> entity = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> response = restTemplate.exchange(uri.toUriString(), HttpMethod.GET,entity, Map.class);

		return response;
	}


	@Override
	public ResponseEntity<Map> getTransaction(String inquiry_type, String from_date,
			String to_date, String sort_order) {
		Optional<UserEntity> user = userDAO.getMyUserWithAuthorities();

		//난수 생성
		Integer random = (int) (Math.random() * ( 1000000000 - 100000000)) + 100000000;

		UriComponents uri =
				UriComponentsBuilder
				.fromUriString("https://testapi.openbanking.or.kr/v2.0/account/transaction_list/fin_num")
				.queryParam("bank_tran_id", bank_tran_id+random)
				.queryParam("fintech_use_num", user.get().getFintech_number())
				.queryParam("inquiry_type", inquiry_type)
				.queryParam("inquiry_base", "D")
				.queryParam("from_date", from_date)
				.queryParam("to_date", to_date)
				.queryParam("sort_order", sort_order)
				.queryParam("tran_dtime", time)
				.queryParam("befor_inquiry_trace_info", 123)
				.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+user.get().getAccess_token());

		HttpEntity<?> entity = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> response = restTemplate.exchange(uri.toUriString(), HttpMethod.GET,entity, Map.class);

		return response;
	}


	@Override
	public ResponseEntity<Map> getAccount(String sort_order, String include_cancel_yn) {
		Optional<UserEntity> user = userDAO.getMyUserWithAuthorities();

		UriComponents uri =
				UriComponentsBuilder
				.fromUriString("https://testapi.openbanking.or.kr/v2.0/account/list")
				.queryParam("user_seq_no", user.get().getUser_seq_no())
				.queryParam("include_cancel_yn", include_cancel_yn)
				.queryParam("sort_order", sort_order)
				.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+user.get().getAccess_token());

		HttpEntity<?> entity = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> response = restTemplate.exchange(uri.toUriString(), HttpMethod.GET,entity, Map.class);

		return response;
	}


	@Override
	public ResponseEntity<Map> deleteAccount() {
		Optional<UserEntity> user = userDAO.getMyUserWithAuthorities();

		//난수 생성
		Integer random = (int) (Math.random() * ( 1000000000 - 100000000)) + 100000000;

		URI uri =
				UriComponentsBuilder
				.fromUriString("https://testapi.openbanking.or.kr/v2.0/account/cancel")
				.build()
				.encode()
				.toUri();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bank_tran_id", bank_tran_id+random);
		map.put("scope", "inquiry");
		map.put("fintech_use_num", user.get().getFintech_number());

		RequestEntity<Map> requestEntity = RequestEntity
				.post(uri)
				.header("Authorization", "Bearer "+user.get().getAccess_token())
				.body(map);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST,requestEntity, Map.class);

		return response;
	}


	@Override
	public ResponseEntity<Map> updateAccount(String fintech_use_num, String account_alias) {
		Optional<UserEntity> user = userDAO.getMyUserWithAuthorities();

		URI uri =
				UriComponentsBuilder
				.fromUriString("https://testapi.openbanking.or.kr/v2.0/account/update_info")
				.build()
				.encode()
				.toUri();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fintech_use_num", fintech_use_num);
		map.put("account_alias", account_alias);

		RequestEntity<Map> requestEntity = RequestEntity
				.post(uri)
				.header("Authorization", "Bearer "+user.get().getAccess_token())
				.body(map);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST,requestEntity, Map.class);

		return response;
	}


	@Override
	public ResponseEntity<Map> deleteUser() {
		Optional<UserEntity> user = userDAO.getMyUserWithAuthorities();

		URI uri =
				UriComponentsBuilder
				.fromUriString("https://testapi.openbanking.or.kr/v2.0/user/close")
				.build()
				.encode()
				.toUri();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("client_use_code", "M202113689");
		map.put("user_seq_no", user.get().getUser_seq_no());

		RequestEntity<Map> requestEntity = RequestEntity
				.post(uri)
				.header("Authorization", "Bearer "+user.get().getAccess_token())
				.body(map);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST,requestEntity, Map.class);

		return response;
	}


	@Override
	public ResponseEntity<Map> getRealname(String bank_code_std, String account_num, String account_holder_info) {
		Optional<UserEntity> user = userDAO.getMyUserWithAuthorities();
		
		//난수 생성
		Integer random = (int) (Math.random() * ( 1000000000 - 100000000)) + 100000000;

		URI uri =
				UriComponentsBuilder
				.fromUriString("https://testapi.openbanking.or.kr/v2.0/inquiry/real_name")
				.build()
				.encode()
				.toUri();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bank_tran_id", bank_tran_id+random);
		map.put("bank_code_std", bank_code_std);
		map.put("account_num", account_num);
		//생년월일로 인증 할 수 있도록 구현함.
		map.put("account_holder_info_type", " ");
		map.put("account_holder_info", account_holder_info);
		map.put("tran_dtime", time);
		System.out.println(map.toString());
		RequestEntity<Map> requestEntity = RequestEntity
				.post(uri)
				.header("Authorization", "Bearer "+user.get().getAccess_token())
				.body(map);
		System.out.println(requestEntity.getHeaders());
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.POST,requestEntity, Map.class);

		return response;
	}

}
