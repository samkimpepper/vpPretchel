package com.pretchel.pretchel0123jwt.modules.message;

import com.pretchel.pretchel0123jwt.infra.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.event.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.event.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.message.dto.MessageCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final GiftRepository giftRepository;

    @Transactional
    public void save(MessageCreateDto dto) {
        Gift gift = giftRepository.findById(dto.getGiftId()).orElseThrow(NotFoundException::new);
        Message message = Message.builder()
                .nickname(dto.getNickname())
                .content(dto.getContent())
                .paid(dto.getPaid())
                .gift(gift)
                .build();

        messageRepository.save(message);
        gift.pay(dto.getPaid());
        if(gift.isGraterthanPrice()) {
            gift.changeState(GiftState.completed, false);
        }

    }
}
