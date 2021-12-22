package com.fintech.pay.data.dao;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.fintech.pay.data.dto.UserTokenDTO;

public interface OpenBankingApiDAO {

	int addUserCode(String code, String id);
	
	int getToken();

	ResponseEntity<String> getUserme();

	ResponseEntity<Map> getBalance();

	ResponseEntity<Map> getTransaction(String inquiry_type, String from_date, String from_time,
			String to_date);

	ResponseEntity<Map> getAccount(String sort_order, String include_cancel_yn);

	ResponseEntity<Map> deleteAccount();

	ResponseEntity<Map> updateAccount(String fintech_use_num, String account_alias);

	ResponseEntity<Map> deleteUser();

	ResponseEntity<Map> getRealname(String bank_code_std, String account_num, String account_holder_info);
}
