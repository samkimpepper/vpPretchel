package com.pretchel.pretchel0123jwt.modules.payments.dto;

import lombok.Getter;

@Getter
public class OpenbankingAuthResponse {
    private String code;
    private String scope;
    private String clientInfo;
    private String state;
}
