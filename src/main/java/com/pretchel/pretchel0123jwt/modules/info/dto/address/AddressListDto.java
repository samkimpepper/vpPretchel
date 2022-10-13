package com.pretchel.pretchel0123jwt.modules.info.dto.address;

import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressListDto {
    private String id;
    private String name;
    private String postCode;
    private String roadAddress;
    private String detailAddress;
    private String phoneNum;
    private Boolean isDefault;


    public static AddressListDto fromAddress(Address address) {
        return AddressListDto.builder()
                .id(address.getId().toString())
                .name(address.getName())
                .postCode(address.getPostCode())
                .roadAddress(address.getRoadAddress())
                .detailAddress(address.getDetailAddress())
                .phoneNum(address.getPhoneNum())
                .isDefault(address.getIsDefault())
                .build();
    }
}
