package com.pretchel.pretchel0123jwt.modules.event.dto.gift;

import com.pretchel.pretchel0123jwt.modules.event.domain.GiftState;

public interface GiftMapping {
    String getId();
    String getName();
    int getPrice();
    String getGiftImageUrl();
    String getLink();
    String getStory();
    GiftState getGiftState();
}
