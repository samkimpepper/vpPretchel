package com.pretchel.pretchel0123jwt.modules.event.dto.event;

import java.time.LocalDateTime;
import java.util.Date;

public interface EventMapping {
    String getNickname();

    String getEventType();

    String getProfileImageUrl();

    String getBackgroundImageUrl();

    Date getDeadLine();

    LocalDateTime getCreateDate();
}
