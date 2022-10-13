package com.pretchel.pretchel0123jwt.modules.payments.controller;

import com.pretchel.pretchel0123jwt.infra.global.ResponseDto;
import com.pretchel.pretchel0123jwt.modules.payments.dto.OpenbankingAuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Controller
public class OpenbankingTokenApiController {
    @Value("${openbanking.client-id}")
    private String clientId;

    @Value("${openbanking.redirect-uri}")
    private String redirectUri;

    @Value("${openbanking.response-type}")
    private String responseType;

    @Value("${openbanking.scope}")
    private String scope;

    @Value("${openbanking.state}")
    private String state;

    @Value("${openbanking.auth-type}")
    private String authType;

    @Value("${openbanking.code-request-url}")
    private String codeRequestUrl;

    private final OpenbankingTokenService openbankingTokenService;

    @GetMapping("/openbanking/token/test")
    public ResponseDto.Data<String> tokenTest() {
        String token = openbankingTokenService.getToken();
        return new ResponseDto.Data<>(token);
    }

    @GetMapping("/openbanking/auth/test")
    public String authTest() {
        OpenbankingAuthResponse response = openbankingTokenService.getTokenTest();
        log.info("Openbanking Auth Test: " + response.getCode());
        return "home";
    }

    @GetMapping("/oauth2/openbanking/redirect")
    public String authRedirect() {
        return "openbanking-auth";
    }

    @GetMapping("/api/openbanking")
    public void getCodeTest(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String targetUrl = UriComponentsBuilder.fromHttpUrl(codeRequestUrl)
                .queryParam("response_type", responseType)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "login inquiry transfer")
                .queryParam("state", state)
                .queryParam("auth_type", authType)
                .build().toUriString();

        response.sendRedirect(targetUrl);
    }


}
