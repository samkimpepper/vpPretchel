package com.pretchel.pretchel0123jwt.modules.info.service;

import com.pretchel.pretchel0123jwt.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountCreateDto;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountListDto;
import com.pretchel.pretchel0123jwt.global.Response;
import com.pretchel.pretchel0123jwt.modules.account.service.UserService;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.repository.AccountRepository;
import com.pretchel.pretchel0123jwt.modules.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final Response responseDto;
    private final UserService userService;

    public Account findById(String accountId) {
        return accountRepository.findById(accountId).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void createAccount(AccountCreateDto dto, String email) {
        Users users = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("잘못된 유저 이메일"));

        Account account = Account.builder()
                .name(dto.getName())
                .accountNum(dto.getAccountNum())
                .bank(dto.getBank())
                .bankCode(dto.getBankCode())
                .birthday(dto.getBirthday())
                .users(users)
                .build();

        accountRepository.save(account);

        if(dto.getIsDefault()) {
            users.setDefaultAccount(account);
        }
    }

    @Transactional
    public List<AccountListDto> getAllMyAccounts(String email) {
        Users users = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("잘못된 유저 이메일"));

        //List<AccountMapping> accounts = accountRepository.findAllByUserId(users);
        List<Account> accountList = accountRepository.findAllByUsers(users);

        return accountList
                .stream()
                .map(account -> {
                    return AccountListDto.fromAccount(account);
                })
                .collect(Collectors.toList());
    }
}
