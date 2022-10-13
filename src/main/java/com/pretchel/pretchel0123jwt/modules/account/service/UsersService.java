package com.pretchel.pretchel0123jwt.modules.account.service;

import com.pretchel.pretchel0123jwt.infra.config.jwt.JwtTokenProvider;
import com.pretchel.pretchel0123jwt.infra.global.exception.InvalidInputException;
import com.pretchel.pretchel0123jwt.infra.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.infra.util.CookieUtils;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.LoginTokenDto;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.UserEventsDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.UserInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.LoginDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.ModifyInfoDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.ModifyPasswordDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.modules.account.exception.ExpiredTokenException;
import com.pretchel.pretchel0123jwt.modules.account.exception.InvalidTokenException;
import com.pretchel.pretchel0123jwt.modules.account.exception.PasswordNotMatchException;
import com.pretchel.pretchel0123jwt.modules.account.exception.UserAlreadyExistsException;
import com.pretchel.pretchel0123jwt.modules.account.domain.Authority;
import com.pretchel.pretchel0123jwt.modules.account.domain.ConfirmPasswordCode;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.account.repository.UsersRepository;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.UserResponseDto;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.pretchel.pretchel0123jwt.modules.oauth2.service.HttpCookieOAuth2AuthorizationRequestRepository.REFRESH_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate redisTemplate;
    private final ConfirmPasswordCodeService confirmPasswordCodeService;
    private final EventRepository profileRepository;

    @Transactional
    public Users findUserByEmail(String email) {
        return usersRepository.findByEmail(email).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void signUp(UserSignupDto userSignupDto) {
        if(usersRepository.existsByEmail(userSignupDto.getEmail())) {
            Users user = usersRepository.findByEmail(userSignupDto.getEmail()).orElseThrow(NotFoundException::new);
            log.info(user.getEmail() + " " + user.getPhoneNumber());

            throw new UserAlreadyExistsException();
        }

        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = new Date(sdf.parse(userSignupDto.getBirthday()).getTime());
        } catch(ParseException ex) {
            ex.printStackTrace();
            throw new InvalidInputException();
        }

        Users user = Users.builder()
                .email(userSignupDto.getEmail())
                .password(passwordEncoder.encode(userSignupDto.getPassword()))
                .birthday(date)
                .phoneNumber(userSignupDto.getPhoneNumber())
                .gender(userSignupDto.getGender())
                .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                .build();
        usersRepository.save(user);
    }

    @Transactional
    public LoginTokenDto login(LoginDto dto, HttpServletResponse response) {
        usersRepository.findByEmail(dto.getEmail()).orElseThrow(NotFoundException::new);

        UsernamePasswordAuthenticationToken authenticationToken = dto.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        int cookieMaxAge = tokenInfo.getRefreshTokenExpirationTime().intValue() / 60;
        CookieUtils.addCookie(response, REFRESH_TOKEN, tokenInfo.getRefreshToken(), cookieMaxAge);

        return new LoginTokenDto(tokenInfo.getAccessToken());
    }

    @Transactional
    public void update(ModifyInfoDto dto, String email) {
        Date date = null;
        if(dto.getBirthday() != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                date = new Date(sdf.parse(dto.getBirthday()).getTime());
            } catch(ParseException ex) {
                ex.printStackTrace();
                throw new InvalidInputException();
            }
        }

        Users user = usersRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        user.update(date, dto.getPhoneNumber());
    }

    @Transactional
    public void updatePassword(ModifyPasswordDto dto, String email) {
        Users user = usersRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        if(passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            if(!dto.getNewPassword().equals(dto.getCheckPassword())) {
                throw new PasswordNotMatchException();
            }
            user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
            return;
        }
        throw new PasswordNotMatchException();
    }

//    public ResponseEntity<?> reissue(UserRequestDto.Reissue reissue) {
//        if(!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
//            return responseDto.fail("Refresh Token 정보가 유효하지 않다능 앙", HttpStatus.BAD_REQUEST);
//        }
//
//        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());
//
//        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
//        if(ObjectUtils.isEmpty(refreshToken)) {
//            return responseDto.fail("잘못된 요청", HttpStatus.BAD_REQUEST);
//        }
//        if(!refreshToken.equals(reissue.getRefreshToken())) {
//            return responseDto.fail("Refresh Token 정보가 일치하지 않다능 앙", HttpStatus.BAD_REQUEST);
//        }
//
//        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
//
//        redisTemplate.opsForValue()
//                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
//
//        return responseDto.success(tokenInfo, "Token 정보 갱신됨", HttpStatus.OK);
//    }

    // 액세스 토큰만 재발급
    public String reissue2(String accessToken, String refreshToken) {
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        String refreshTokenRedis = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
        if(ObjectUtils.isEmpty(refreshTokenRedis)) {
            throw new ExpiredTokenException();
        }
        if(!refreshTokenRedis.equals(refreshToken)) {
            throw new InvalidTokenException();
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(authentication);
        return newAccessToken;
    }

    public void logout(String accessToken, String refreshToken) {
        if(!jwtTokenProvider.validateToken(accessToken)) {
            throw new InvalidTokenException();
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        // 리프레시 삭제
        if(redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // 쿠키에서도 리프레시 삭제는 컨트롤러에서 해야되나

        // 액세스 토큰은 로그아웃된 토큰으로 처리
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        SecurityContextHolder.clearContext();
    }

    public List<UserEventsDto> getUserEvents(String email) {
        Users users = usersRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        //List<EventMapping> profiles = profileRepository.findProfilesByUserId(users);
        List<Event> events = profileRepository.findAllByUsers(users);

        return events.stream()
                .map(event -> {
                    return UserEventsDto.fromEvent(event);
                })
                .collect(Collectors.toList());
    }

    public UserInfoDto getUserInfo(String email) {
        Users users = usersRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        AccountInfoDto account = null;
        if(users.getDefaultAccount() != null) {
            account = AccountInfoDto.fromAccount(users.getDefaultAccount());
        }

        return UserInfoDto.fromUser(users, account);

    }

    public void sendEmail(String email) {
        Users users = usersRepository.findByEmail(email).orElseThrow(NotFoundException::new);

        confirmPasswordCodeService.sendEmailConfirmCode(users.getId(), users.getEmail());
    }

    public void confirmEmail(String authCode) {
        Optional<ConfirmPasswordCode> optionalFindCode = confirmPasswordCodeService.findByIdAndExpiryDateAfterAndExpired(authCode);
        if(!optionalFindCode.isPresent()) {
            throw new InvalidInputException();
        }
        ConfirmPasswordCode findCode = optionalFindCode.get();
        findCode.setExpired();

        //Optional<Users> usersOptional = usersRepository.findById(findCode.getUserId());
        //Users users = usersOptional.get();
    }


}
