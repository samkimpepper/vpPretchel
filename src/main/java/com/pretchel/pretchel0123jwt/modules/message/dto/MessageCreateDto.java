package com.pretchel.pretchel0123jwt.modules.message.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MessageCreateDto {
    private String nickname;
    private String content;
    private int paid;
    private String giftId;
}
