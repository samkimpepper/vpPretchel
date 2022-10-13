package com.pretchel.pretchel0123jwt.modules.gift;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GiftSchedular {
    private final GiftSchedulerService giftSchedulerService;

    // 만료된 선물 있는지(마감기한으로) 검사
    // 매일 00시 00분 00초에 실행
    @Scheduled(cron="0 0 0 * * *")
    public void checkExpiredGifts() {

    }
}
