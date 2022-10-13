package com.pretchel.pretchel0123jwt.modules.account.controller;

import com.pretchel.pretchel0123jwt.infra.global.ResponseDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.UserEventsDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.UserInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserInfoController {
    private final UsersService usersService;

    @GetMapping("/my-events")
    public ResponseDto.DataList<UserEventsDto> userEventsInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto.DataList<>(usersService.getUserEvents(email));
    }


    @GetMapping("/user-info")
    public ResponseDto.Data<UserInfoDto> userInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto.Data<>(usersService.getUserInfo(email));
    }
}
