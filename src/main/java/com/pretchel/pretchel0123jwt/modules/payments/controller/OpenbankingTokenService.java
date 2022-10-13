package com.pretchel.pretchel0123jwt.modules.payments.controller;

import com.pretchel.pretchel0123jwt.modules.payments.dto.OpenbankingAuthResponse;
import com.pretchel.pretchel0123jwt.modules.payments.dto.OpenbankingTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OpenbankingTokenService {
    @Value("${openbanking.token-request-url}")
    private String requestUrl;

    @Value("${openbanking.client-id}")
    private String clientId;

    @Value("${openbanking.client-secret}")
    private String clientSecret;

    @Value("${openbanking.scope}")
    private String scope;

    @Value("${openbanking.grant-type}")
    private String grantType;

    public OpenbankingAuthResponse getTokenTest() {
        // Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("response_type", "code");
        params.add("scope", "login inquiry transfer");
        params.add("state", "b80BLsfigm9OokPTjy03elbJqRHOfGSY");
        params.add("auth_type", "0");

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Merge
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        // POST request
        RestTemplate rt = new RestTemplate();
        ResponseEntity<OpenbankingAuthResponse> response = rt.exchange(
                "https://testapi.openbanking.or.kr/oauth/2.0/authorize",
                HttpMethod.POST,
                entity,
                OpenbankingAuthResponse.class
        );

        return response.getBody();
    }

    public String getToken() {
        // Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("scope", scope);
        params.add("grant_type", grantType);

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Merge
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        // POST request
        RestTemplate rt = new RestTemplate();
        ResponseEntity<OpenbankingTokenResponse> response = rt.exchange(
                requestUrl,
                HttpMethod.POST,
                entity,
                OpenbankingTokenResponse.class
        );

        log.info("Openbanking Token: " + response.getBody().getAccessToken());
        return response.getBody().getAccessToken();

    }
}
