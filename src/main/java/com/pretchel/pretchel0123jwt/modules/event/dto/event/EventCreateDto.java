package com.pretchel.pretchel0123jwt.modules.event.dto.event;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Builder
public class EventCreateDto implements Serializable {
    private String nickName;
    private String eventType;
    private MultipartFile[] images;
    private String deadLine;

    public int imagesCount() {
        return images.length;
    }
}
