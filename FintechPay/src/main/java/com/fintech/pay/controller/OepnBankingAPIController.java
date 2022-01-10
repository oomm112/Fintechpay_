package com.fintech.pay.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fintech.pay.data.dao.UserDAO;
import com.fintech.pay.service.OpenBankingAPIService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ResponseHeader;

@RestController
@RequestMapping("/openBanking")
@Api(tags = {"오픈뱅킹 관련 메소드"})
public class OepnBankingAPIController {

	private final OpenBankingAPIService openBankingAPIService;
	private final UserDAO userDAO;

	@Autowired
	public OepnBankingAPIController(OpenBankingAPIService openBankingAPIService, UserDAO userDAO) {
		this.openBankingAPIService = openBankingAPIService;
		this.userDAO = userDAO;
	}

	//사용자인증 uri
	//https://testapi.openbanking.or.kr/oauth/2.0/authorize?response_type=code&client_id=6cc9ec2e-52e9-473a-a392-4a783fed7443&redirect_uri=http%3A%2F%2Flocalhost%3A9099%2FopenBanking%2Fcode&scope=login%20inquiry%20transfer&client_info=test&state=b80BLsfigm9OokPTjy03elbJqRHOfGSY&auth_type=0&cellphone_cert_yn=Y&authorized_cert_yn=Y&account_hold_auth_yn=N&register_info=A

	//사용자 인증api로 코드를 신규발급 받은 후, 돌아올 메소드(리턴 uri)
	//코드값이 있을경우, 받아서 db에 등록해준다.
	@GetMapping(value = "/code")
	@ResponseHeader
	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	@ApiOperation(value="코드 발급")
	public Map<String, String> addUserCode(HttpServletRequest request) {
		Map<String, String> response = new HashMap<>();
		
		String code = request.getParameter("code");
		String id = userDAO.getMyUserWithAuthorities().get().getId();

		int response_code = openBankingAPIService.addUserCode(code, id);

		if (response_code == 0) {
			response.put("result", "코드가 정상적으로 등록되었습니다");
		}else {
			response.put("result", "코드 등록을 실패 하였습니다");
		}
		return response;
	}

	//오픈뱅킹을 사용하기위한 토큰을 발급받는 메소드
	@PostMapping(value = "/gettoken")
	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	@ApiOperation(value="토큰 발급")
	public Map<String, String> getToken(HttpSession httpSession){
		Map<String, String> response = new HashMap<>();

		int response_code = openBankingAPIService.getToken();

		if (response_code == 0) {
			response.put("result", "코드가 정상적으로 등록되었습니다"); 
		}else{ 
			response.put("result", "코드 등록을 실패 하였습니다");
		} 
		
		return response;
	}

	//사용자 조회 (돌아온 값 중, 핀테크 값만 DB에 등록해준다)
	@GetMapping(value = "/userme")
	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	@ApiOperation(value="사용자 조회 및 핀테크 번호 등록")
	public ResponseEntity<String> getUserme(){
		return openBankingAPIService.getUserme();
	}
	
	//잔액조회
	@GetMapping(value = "/balance")
	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	@ApiOperation(value="잔액 조회")
	public ResponseEntity<Map> getBalance(){
		return openBankingAPIService.getBalance();
	}
	
	//거래내역조회
	@GetMapping(value = "/transaction")
	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	@ApiOperation(value="거래내역 조회")
	public ResponseEntity<Map> getTransaction(@RequestParam String inquiry_type, @RequestParam String from_date,
			@RequestParam String to_date,@RequestParam String sort_order){
		return openBankingAPIService.getTransaction(inquiry_type, from_date, to_date, sort_order);
	}
	
	//등록계좌조회
	@GetMapping(value = "/account")
	@ApiOperation(value="등록 계좌조회")
	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	public ResponseEntity<Map> getAccount(@RequestParam String sort_order, @RequestParam String include_cancel_yn){
		return openBankingAPIService.getAccount(sort_order, include_cancel_yn);
	}
	
	//등록계좌해지
	@PostMapping(value = "/account/cancel")
	@ApiOperation(value="등록 계좌해지")
	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	public ResponseEntity<Map> deleteAccount(){
		return openBankingAPIService.deleteAccount();
	}
	
	//계좌 정보 변경 (관리자용)
	@PostMapping(value = "/account/update")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@ApiOperation(value="계좌정보변경")
	public ResponseEntity<Map> updateAccount(@RequestBody Map<String, String> map){
		String fintech_use_num = map.get("fintech_use_num");
		String account_alias = map.get("account_alias");
		
		return openBankingAPIService.updateAccount(fintech_use_num, account_alias);
	}
	
	//사용자 탈퇴
	@PostMapping(value = "/user/delete")
	@PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
	@ApiOperation(value="사용자 탈퇴")
	public ResponseEntity<Map> deleteUser(){
		return openBankingAPIService.deleteUser();
	}
	
	//계좌실명조회 (관리자용)
	@PostMapping(value = "/user/real")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	@ApiOperation(value="계좌실명조회[관리자용]")
	public ResponseEntity<Map> getRealname(@RequestBody Map<String, String> map){
		String bank_code_std = map.get("bank_code_std");
		String account_num = map.get("account_num");
		String account_holder_info = map.get("account_holder_info");

		return openBankingAPIService.getRealname(bank_code_std, account_num, account_holder_info);
	}
	
	//입금 이체
	
	//출금 이체
}
