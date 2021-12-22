package com.fintech.pay.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.fintech.pay.data.dto.UserTokenDTO;

public interface OpenBankingAPIService {
	public int addUserCode(String code, String id);

	public int getToken();

	public ResponseEntity<String> getUserme();

	public ResponseEntity<Map> getBalance();

	public ResponseEntity<Map> getTransaction(String inquiry_type, String from_date, String from_time, String to_date);

	public ResponseEntity<Map> getAccount(String sort_order, String include_cancel_yn);

	public ResponseEntity<Map> deleteAccount();

	public ResponseEntity<Map> updateAccount(String fintech_use_num, String account_alias);

	public ResponseEntity<Map> deleteUser();

	public ResponseEntity<Map> getRealname(String bank_code_std, String account_num, String account_holder_info); 
}
