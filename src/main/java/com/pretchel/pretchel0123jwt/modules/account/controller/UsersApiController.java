package com.pretchel.pretchel0123jwt.modules.account.controller;

import com.pretchel.pretchel0123jwt.infra.config.jwt.JwtTokenProvider;
import com.pretchel.pretchel0123jwt.infra.global.ResponseDto;
import com.pretchel.pretchel0123jwt.infra.util.CookieUtils;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.LoginTokenDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.LoginDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.ModifyInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.ModifyPasswordDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.infra.global.Response;
import com.pretchel.pretchel0123jwt.modules.info.service.AccountService;
import com.pretchel.pretchel0123jwt.modules.info.service.AddressService;
import com.pretchel.pretchel0123jwt.modules.account.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static com.pretchel.pretchel0123jwt.modules.oauth2.service.HttpCookieOAuth2AuthorizationRequestRepository.REFRESH_TOKEN;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UsersApiController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UsersService usersService;
    private final Response responseDto;
    private final AddressService addressService;
    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseDto.Empty signUp(@RequestBody UserSignupDto userSignupDto, Errors errors) {
//        if(errors.hasErrors()) {
//            return responseDto.invalidFields(Helper.refineErrors(errors));
//        }

        usersService.signUp(userSignupDto);
        return new ResponseDto.Empty();
    }

    @PostMapping("/login")
    public ResponseDto.Data<LoginTokenDto> login(@RequestBody @Validated LoginDto dto,
                                                 HttpServletResponse response) {
//        if(errors.hasErrors()) {
//            return responseDto.invalidFields(Helper.refineErrors(errors));
//        }


        return new ResponseDto.Data<>(usersService.login(dto, response));

//        CookieUtils.deleteCookie(request, response, ACCESS_TOKEN);
//        CookieUtils.addCookie(response, ACCESS_TOKEN, tokenInfo.getAccessToken(), 60 * 60 * 24 * 7);
    }

    @DeleteMapping("/logout")
    public ResponseDto.Empty logout(@RequestHeader("accesstoken") String accesstoken,
                                    @RequestHeader("refreshtoken") String refreshtoken) {
        //return usersService.logout(logout);
        //log.info(headers.toSingleValueMap().toString());

        usersService.logout(accesstoken, refreshtoken);
        return new ResponseDto.Empty();
    }

//    @PostMapping("/reissue")
//    public ResponseEntity<?> reissue(@RequestHeader @Validated UserRequestDto.Reissue reissue) {
//        System.out.println(reissue.getAccessToken());
//        // 그냥 Authorization 헤더에 있는거 가져오면 안되나
//        // 리프레시는 내가 쿠키에서 알아서 가져올까?
//
//        return usersService.reissue(reissue);
//    }

    @GetMapping("/reissue")
    public ResponseDto.Data<String> reissue2(HttpServletRequest request) {
        // 이걸 할 필요가 있나? 어차피 filter에서 하는데
        String accessToken = request.getHeader("Authorization");
        if(StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer")) {
            accessToken = accessToken.substring(7);
        } else {
            responseDto.fail("헤더에 액세스토큰이 없는듯", HttpStatus.NOT_FOUND);
        }

        Optional<Cookie> cookieOptional = CookieUtils.getCookie(request, REFRESH_TOKEN);
        if(cookieOptional.isEmpty()) {
            responseDto.fail("쿠키에 리프레시토큰이 없음 재로그인해야될듯", HttpStatus.NOT_FOUND);
        }
        Cookie cookie = cookieOptional.get();

        return new ResponseDto.Data<>(usersService.reissue2(accessToken, cookie.getValue()));
    }

    @PutMapping("/update")
    public ResponseDto.Empty update(@RequestBody ModifyInfoDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        usersService.update(dto, email);
        return new ResponseDto.Empty();
    }

    @PutMapping("/update-password")
    public ResponseDto.Empty updatePassword(@RequestBody ModifyPasswordDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        usersService.updatePassword(dto, email);
        return new ResponseDto.Empty();
    }



}
