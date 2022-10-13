package com.pretchel.pretchel0123jwt.modules.account.controller;

import com.pretchel.pretchel0123jwt.infra.global.ResponseDto;
import com.pretchel.pretchel0123jwt.modules.account.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class PasswordFindController {
    private final UsersService usersService;

    @PostMapping("/find-email")
    public ResponseDto.Empty findPassword(@RequestBody String email) {

        usersService.sendEmail(email);
        return new ResponseDto.Empty();
    }

    @PostMapping("/confirm-email")
    public ResponseDto.Empty confirmEmail(@RequestBody String authCode) {

        usersService.confirmEmail(authCode);
        return new ResponseDto.Empty();
    }
}
