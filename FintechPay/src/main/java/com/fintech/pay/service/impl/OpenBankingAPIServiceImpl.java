package com.fintech.pay.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fintech.pay.data.dao.OpenBankingApiDAO;
import com.fintech.pay.service.OpenBankingAPIService;

@Service
public class OpenBankingAPIServiceImpl implements OpenBankingAPIService{
	OpenBankingApiDAO openBankingApiDAO;
	
	/**
	 * @param openBankingApiDAO
	 */
	@Autowired
	public OpenBankingAPIServiceImpl(OpenBankingApiDAO openBankingApiDAO) {
		this.openBankingApiDAO = openBankingApiDAO;
	}

	@Override
	public int addUserCode(String code, String id) {
		return openBankingApiDAO.addUserCode(code, id);
	}

	@Override
	public int getToken() {
		return openBankingApiDAO.getToken();
	}

	@Override
	public ResponseEntity<String> getUserme() {
		return openBankingApiDAO.getUserme();
	}

	@Override
	public ResponseEntity<Map> getBalance() {
		return openBankingApiDAO.getBalance();
	}

	@Override
	public ResponseEntity<Map> getTransaction(String inquiry_type, String from_date,
			String to_date, String sort_order) {
		return openBankingApiDAO.getTransaction(inquiry_type, from_date, to_date, sort_order);
	}

	@Override
	public ResponseEntity<Map> getAccount(String sort_order, String include_cancel_yn) {
		return openBankingApiDAO.getAccount(sort_order, include_cancel_yn);
	}

	@Override
	public ResponseEntity<Map> deleteAccount() {
		return openBankingApiDAO.deleteAccount();
	}

	@Override
	public ResponseEntity<Map> updateAccount(String fintech_use_num, String account_alias) {
		return openBankingApiDAO.updateAccount(fintech_use_num, account_alias);
	}

	@Override
	public ResponseEntity<Map> deleteUser() {
		return openBankingApiDAO.deleteUser();
	}

	@Override
	public ResponseEntity<Map> getRealname(String bank_code_std, String account_num, String account_holder_info) {
		return openBankingApiDAO.getRealname(bank_code_std, account_num, account_holder_info);
	}

	 
}
