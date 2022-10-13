package com.pretchel.pretchel0123jwt.modules.payments.dto;

import lombok.Getter;

@Getter
public class OpenbankingTokenResponse {
    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private String scope;
    private String clientUseCode;
}
