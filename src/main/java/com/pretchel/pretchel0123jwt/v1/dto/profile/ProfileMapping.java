package com.pretchel.pretchel0123jwt.v1.dto.profile;

import java.time.LocalDateTime;

public interface ProfileMapping {
    String getNickname();

    String getEventType();

    String getProfileImageUrl();

    String getBackgroundImageUrl();

    LocalDateTime getCreateDate();
}
