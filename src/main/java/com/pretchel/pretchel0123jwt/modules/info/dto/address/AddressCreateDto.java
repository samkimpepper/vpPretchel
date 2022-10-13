package com.pretchel.pretchel0123jwt.modules.info.dto.address;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressCreateDto {
    private String name;
    private String postCode;
    private String roadAddress;
    private String detailAddress;
    private String phoneNum;
    private Boolean isDefault;
}
