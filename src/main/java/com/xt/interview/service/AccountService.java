package com.xt.interview.service;

import com.xt.interview.dto.AccountDTO;
import com.xt.interview.dto.AccountPageQuery;
import com.xt.interview.dto.PageResult;

import java.util.List;

public interface AccountService {

    AccountDTO createAccount(AccountDTO dto);

    AccountDTO updateAccount(Long id, AccountDTO dto);

    void deleteAccount(Long id);

    AccountDTO getAccountById(Long id);

    List<AccountDTO> getAllAccounts();

    PageResult<AccountDTO> getAccountsPage(AccountPageQuery query);

    AccountDTO login(String username, String password);
}
