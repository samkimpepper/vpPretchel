package com.pretchel.pretchel0123jwt.modules.gift;

import com.pretchel.pretchel0123jwt.modules.event.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.event.domain.GiftState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftSchedulerService {
    private final GiftQdslRepository giftQdslRepository;
    private final GiftRepository giftRepository;


    @Transactional
    public void findExpiredGifts() {
        List<Gift> gifts = giftQdslRepository.findByDeadLine();

        for(Gift gift: gifts) {
            gift.changeState(GiftState.expired, false);
        }
    }


    /*
    * state가 complete, finished, expired이고 isProcessed가 true인 애들
    *
    * */
    @Transactional
    public void moveToCompletedGift() {
        List<Gift> gifts = giftRepository.findAllByStateNotIn(GiftState.ongoing, GiftState.canceled);

        for(Gift gift: gifts) {
            // TODO: CompletedGift로 옮기고, 메세지들 fk도 바꿔야됨
        }
    }

}






